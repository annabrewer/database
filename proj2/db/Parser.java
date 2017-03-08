package db;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {

    // Valid column names should only contain numbers and letters
    private static final String VALID_NAME = "\\w\\d";

    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
                                COMMA = "\\s*,\\s*",
                                AND   = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
                                 LOAD_CMD   = Pattern.compile("load " + REST),
                                 STORE_CMD  = Pattern.compile("store " + REST),
                                 DROP_CMD   = Pattern.compile("drop table " + REST),
                                 INSERT_CMD = Pattern.compile("insert into " + REST),
                                 PRINT_CMD  = Pattern.compile("print " + REST),
                                 SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*" +
                                               "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
                                 SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                                               "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                                               "([\\w\\s+\\-'<>=!.]+?(?:\\s+and\\s+" +
                                               "[\\w\\s+\\-'<>=!.]+?)*))?"),
                                 CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                                                       SELECT_CLS.pattern()),
                                 INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                                               "\\s*(?:,\\s*.+?\\s*)*)");

    // HashMap of the database's tables for the parser to reference
    protected HashMap<String, Table> tables = new HashMap<>();

    public Parser(HashMap<String, Table> tbls) {
        tables = tbls;
    }
    public static void main(String[] args) {
        Parser p = new Parser(new HashMap<>());
        if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        }
        System.out.println("evaluating");
        p.eval(args[0]);
    }

    public void eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else {
            System.err.printf("Malformed query: %s\n", query);
        }
    }

    public String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            String name = m.group(1);
            String columns = m.group(2);
            return createNewTable(name, columns.split(COMMA));

        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            String name = m.group(1);
            String colExpr = m.group(2);
            String tbls = m.group(3);
            String conds = m.group(4);
            return createFromSelect(name, colExpr, tbls, conds);
        } else {
            return "ERROR: Malformed create: " + expr;
        }
    }

    private String createNewTable(String name, String[] columns) {
        if (tables.keySet().contains(name)) {
            return "ERROR: Table already exists: " + name;
        }

        if (!validName(name)) {
            return "ERROR: Invalid table name: " + name;
        }

        String result = evalColumn(columns);
        if (!validColumn(columns)) {
            return result;
        }

        Table newTable = new Table(name, getColumns(columns));
        tables.put(name, newTable);
        return " ";
    }

    private String createFromSelect(String name, String colExpr, String tbls, String conds) {
        if (!validName(name)) {
            return "ERROR: Invalid table name: " + name;
        } else if (tables.keySet().contains(name)) {
            return "ERROR: Table already exists: " + name;
        } else if (!containsAllTables(tbls)) {
            return "ERROR: No such table: " + missingTable(tbls);
        } else {
            return selectAs(name, colExpr, tbls, conds);
        }
    }

    public String select(String expr) {
        Matcher m;
        if (!(m = SELECT_CMD.matcher(expr)).matches()) {
            return "ERROR: Malformed select: " + expr;
        } else {
            String colExpr = m.group(1);
            String tbls = m.group(2);
            String conds = m.group(3);
            return select(colExpr, tbls, conds);
        }
    }

    private String selectAs(String name, String colExpr, String tbls, String conds) {
        if (conds == null) {
            return selectAs(name, colExpr, tbls);
        }
        Table t = tableFromSelect(name, colExpr, tbls, conds);
        tables.put(name, t);
        return " ";
    }

    private String selectAs(String name, String colExpr, String tbls) {
        String result = evalColumnExpression(colExpr);

        if (result.equals(" ")) {
            Table t = tableFromSelect(name, colExpr, tbls);
            tables.put(name, t);
            return " ";
        } else {
            return result;
        }
    }

    private String select(String colExpr, String tbls, String conds) {
        if (conds == null) {
            return select(colExpr, tbls);
        } else {
            String result = evalConditional(conds);

            if (result.equals(" ")) {
                Table t = tableFromSelect("temp", tbls, conds);
                return t.toString();
            } else {
                return result;
            }
        }
    }

    private String select(String colExpr, String tbls) {
        String result = evalColumnExpression(colExpr);

        if (result.equals(" ")) {
            Table t = tableFromSelect("temp", colExpr, tbls);
            return t.toString();
        } else {
            return result;
        }
    }

    private Table tableFromSelect(String name, String colExpr, String tbls, String conds) {
        Table t = join(tbls);
        return t;
    }

    private Table tableFromSelect(String name, String colExpr, String tbls) {
        return join(tbls);
    }


    private Table join(String tbls) {
        String[] list = listTables(tbls);
        if (list.length == 1) {
            return tables.get(list[0]);
        }

        String t1 = list[0];
        String t2 = list[1];
        Table joined = Table.join(tables.get(t1), tables.get(t2), "temp");

        for (int i = 2; i < list.length; i++) {
            Table t = tables.get(list[i]);
            joined = Table.join(joined, t, "temp");
        }

        return joined;
    }

    private static String[] listTables(String tbls) {
        return tbls.split(COMMA);
    }

    private boolean containsTable(String tbl) {
        return tables.keySet().contains(tbl);
    }

    private boolean containsAllTables(String tbls) {
        String[] list = listTables(tbls);

        for (String tbl : list) {
            if (!containsTable(tbl)) {
                return false;
            }
        }
        return true;
    }

    private String missingTable(String tbls) {
        String[] list = listTables(tbls);

        for (String tbl : list) {
            if (!containsTable(tbl)) {
                return tbl;
            }
        }
        return " ";
    }

    private String evalConditional(String conds) {
        String conditional = "([\\w\\s+\\-'<>=!.]+?(?:\\s+and\\s+" +
                             "[\\w\\s+\\-'<>=!.]+?)*)?";
        Pattern cond = Pattern.compile(conditional);

        Matcher m;
        if (!(m = cond.matcher(conds)).matches()) {
            return "ERROR: Malformed conditional: " + conds;
        } else {
            return " ";
        }
    }

    private String evalColumnExpression(String colExpr) {
        String columnExpression = "([^,]+?(?:,[^,]+?)*)";
        Pattern expression = Pattern.compile(columnExpression);

        Matcher m;
        if (!(m = expression.matcher(colExpr)).matches()) {
            return "ERROR: Malformed column expression: " + colExpr;
        } else {
            return " ";
        }
    }

    private String validColumnExpression(String colExpr, Table t) {
        String[] expressions = colExpr.split(COMMA);

        for (String expr : expressions) {
            String[] split = expr.split(" ");
            String result = expressionHandler(split, t);

            if (!result.equals(" ")) {
                return result;
            }
        }
        return " ";
    }

    private String expressionHandler(String[] expr, Table t) {
        if (expr.length == 1) {
            String column = expr[0];
            if (!t.containsColumn(column)) {
                return "ERROR: No such column with name: " + column;
            } else {
                return " ";
            }
        } else {
            String[] operations = new String[]{"+", "-", "*", "/"};
            String column = expr[0];
            String operation = expr[1];
            String literal = expr[1];

            for (String op : operations) {
                if (!operation.equals(op)) {
                    return "ERROR: Malformed column expression" +
                            column + operation + literal;
                }
            }

            if (!t.containsColumn(column)) {
                return "ERROR: Malformed column expression: " + column + operation + literal;
            } else if (!t.containsColumn(literal) && !isLiteral(literal)) {
                return "ERROR: Malformed column expression: " + column + operation + literal;
            } else {
                return " ";
            }
        }
    }

    private static boolean validName(String name) {
        if (Character.isDigit(name.charAt(0))) {
            return false;
        }

        String[] characters = name.split("");
        for (String ch : characters) {
            if (!ch.matches(VALID_NAME)) {
                return false;
            }
        }

        return true;
    }

    private static boolean validColumn(String[] col) {
        return evalColumn(col).equals(" ");
    }

    private static String evalColumn(String[] columnInfo) {
        if (columnInfo.length == 1) {
            return "ERROR: Malformed column declaration: " + columnInfo[0];
        } else if (!columnInfo[1].equals("int") ||
                   !columnInfo[1].equals("float") ||
                   !columnInfo[1].equals("string")) {
            return "ERROR: Invalid type" + columnInfo[1];
        } else {
            return " ";
        }
    }

    private boolean isLiteral(String literal) {
        String[] valildFormat = new String[]{"\\d+", "\\d+\\.", "\\'+(.*)\\'"};

        for (String format : valildFormat) {
            if (literal.matches(format)) {
                return true;
            }
        }

        return false;
    }

    private String parseValue(String value) {
        String[] validFormat = new String[]{"\\d+", "\\d+\\.", "\\'+(.*)\\'"};
        for (String format : validFormat) {
            if (value.matches(format)) {
                return " ";
            }
        }

        return "ERROR: Malformed data entry: " + value;
    }

    private String[] listConditions(String conds) {
        return conds.split(AND);
    }

    private String[] listColumnExpressions(String colExpr) {
        return colExpr.split(COMMA);
    }

    private String[] splitOperation(String op) {
        return op.split("\\s");
    }

    //private Table evaluateExpression()

    private static LinkedHashMap<String, Class> getColumns(String[] columns) {
        LinkedHashMap<String, Class> cols = new LinkedHashMap<>();
        for (String col : columns) {
            String[] c = col.split("\\s");
            cols.put(c[0], getType(c[1]));
        }
        return cols;
    }

    private static Class getType(String t) {
        if (t.equals("int")) {
            return Integer.class;
        } else if (t.equals("float")) {
            return Float.class;
        } else {
            return String.class;
        }
    }

    /* Fill these in.
     */
    public String loadTable(String name) {
        return " ";
    }

    public String storeTable(String name) {
        return " ";
    }

    public String dropTable(String name) {
        return " ";
    }

    public String insertRow(String expr) {
        return " ";
    }

    public String printTable(String name) {
        return " ";
    }
}
