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

    /* Creates a new table with string name and with the columns provided in
     * columns. Columns should be in the format ["name type", "name type"...].
     */
    private String createNewTable(String name, String[] columns) {
        String result;
        if (!(result = checkTableName(name)).equals(" ")) {
            return result;
        } else if (!(result = validateColumns(columns)).equals(" ")) {
            return result;
        } else {
            Table newTable = new Table(name, getColumns(columns));
            tables.put(name, newTable);
            return " ";
        }
    }

    /* Creates a table by selecting from a list of tables. Arguments are in the format:
     *    - name: String of the name
     *    - colExpr: can either be:
     *        - "<column name> <operation> <column name or literal> as <name>,....."
     *        - "<column name>, <column name>, ...."
     *    - tbls: "<table name>, "<table name>,....."
     *    - conds: "<column name> <conditional> <column name or literal>, ...."
     * Select statements with column operations and column conditionals aren't supported.
     */
    private String createFromSelect(String name, String colExpr, String tbls, String conds) {
        Table tbl;
        Table joined = join(tbls);
        String result;
        if (!(result = checkTableName(name)).equals(" ")) {
            return result;
        } else if (!(result = checkTables(tbls)).equals(" ")) {
            return result;
        } else if (!(result = checkColumnExpressions(colExpr, joined)).equals(" ")) {
            return result;
        } else {
            tbl = tableFromSelect(name, colExpr, tbls);
            if (conds == null) {
                tables.put(name, tbl);
                return " ";
            } else {
                tbl =
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
        String result = checkColumnExpressions(colExpr);

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
        String result = checkColumnExpressions(colExpr);

        if (result.equals(" ")) {
            Table t = tableFromSelect("temp", colExpr, tbls);
            return t.toString();
        } else {
            return result;
        }
    }

    /* Returns the table resulting from selecting from the given tables using the
     * given columns with the given conditions applied to them. Formatting of inputs:
     *     - name: String name for resulting table
     *     - colExpr: can either be
     *         - <column> <arithmetic> <column or literal> as <name>....
     *         - <column>, <column>,...
     */
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

    private String checkTables(String tbls) {
        if (!containsAllTables(tbls)) {
            return "ERROR: No such table: " + missingTable(tbls);
        }
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

    /* Checks to see if the given column expressions are valid. ColExpr is in
     * the format of either:
     *     - "<column name> <operation> <column name or literal> as <name>,....."
     *     - "<column name>, <column name>, ...."
     */
    private String checkColumnExpressions(String colExpr, Table t) {
        String[] columnExpressions = colExpr.split(",+\\s");
        for (String expression : columnExpressions) {
            String result;
            if (!(result = validColumnExpression(expression, t)).equals(" ")) {
                return result;
            }
        }
        return " ";
    }

    /* Takes in a column expression and the table the expression is referring to.
     * Checks to see if this particular column expression is valid. ColExpr is in the
     * format of either:
     *    - <column name> <arithmetic> <column name or literal> as <name>
     *    - <column name>
     */
    private String validColumnExpression(String colExpr, Table t) {
        String arithmetic = "\\S+\\s+(\\+|-|\\*|/)+\\s+\\S+\\s+as+\\s+\\S+";
        if (colExpr.matches(arithmetic)) {
            return validArithmetic(colExpr, t);
        } else {
            return checkTableName(colExpr);
        }
    }

    /* Takes in a column expression with arithmetic and the table it refers to.
     * Checks to see if this expression is valid. ColExpr should be in the format:
     *     - <column name> <arithmetic> <column name or literal> as <name>
     * ColExpr is valid if:
     *     - the first column name is in the table being referred to
     *     - the arithmetic operation is either +, -, /,
     *     - the third operand is either a valid column name or literal
     *         - valid literals either a digit or a sequence of characters
     *           surrounded by single quotes, i.e. 1, or 'randomcharacters'
     *     - operation isn't being done on a string with a float or int, vice versa
     *     - operations involving strings should only have + in the expression
     *     - the provided name is a valid name
     */
    private String validArithmetic(String colExpr, Table t) {
        String[] parts = colExpr.split("\\sas\\s"); // [<expression>, <name>]
        String arithmetic = parts[0];
        String name = parts[1];

        String result;
        if (!(result = checkTableName(name)).equals(" ")) {
            return result;
        } else if (!(result = validArithmeticParts(colExpr, t)).equals(" ")) {
            return result;
        } else if (!t.containsColumn(colExpr)) {
            return "ERROR: No such column with name: " + colExpr;
        } else {
            return " ";
        }
    }

    /* Checks if the parts of this arithmetic operation are correct. The array parts
     * is in the format [<column name>, <arithmetic>, <column or literal>]. Every part is valid if:
     *     - the first column name is in the table being referred to
     *     - the arithmetic operation is either +, -, /,
     *     - the third operand is either a valid column name or literal
     *         - valid literals either a digit or a sequence of characters
     *           surrounded by single quotes, i.e. 1, or 'randomcharacters'
     *     - operation isn't being done on a string with a float or int, vice versa
     *     - operations involving strings should only have + in the expression
     */
    private String validArithmeticParts(String expr, Table t) {
        String[] parts = expr.split(" ");
        String column = parts[0];
        String arithmetic = parts[1];
        String operand = parts[2];

        if (!arithmetic.matches("[-+/*]")) {
            return "ERROR: Malformed column expression :" + expr;
        } else if (!t.containsColumn(column)) {
            return "ERROR: No such column with name: " + column;
        } else if (!t.containsColumn(operand) || !validLiteral(operand)) {
            return "ERROR: Malformed column expression: " + expr;
        } else if (t.containsColumn(operand)) {
            Class type1 = t.getColumnTypes().get(column);
            Class type2 = t.getColumnTypes().get(operand);
            if (!compatibleTypes(type1, type2) || !arithmetic.equals("+")) {
                return "ERROR: Incompatible types: " +
                        typeToString(type1) + " and " + typeToString(type2);
            } else {
                return " ";
            }
        } else if (validLiteral(operand)) {
            Class type1 = t.getColumnTypes().get(column);
            Class type2 = t.getColumnTypes().get(getValueType(operand));
            if (!compatibleTypes(type1, type2) || !arithmetic.equals("+")) {
                return "ERROR: Incompatible types: " +
                        typeToString(type1) + " and " + typeToString(type2);
            } else {
                return " ";
            }
        } else {
            return " ";
        }
    }

    /* Checks if the string is a valid literal. A valid literal cannot have
     * new tabs, new lines, or commas.
     */
    private boolean validLiteral(String literal) {
        String[] validFormat = new String[]{"-?\\d+", "-?(\\.\\d+|\\d+\\.\\d+|\\d+\\.)",
                                            "'+[^\\t\\n,'\"]+'"};
        for (String format : validFormat) {
            if (literal.matches(format)) {
                return true;
            }
        }

        return false;
    }

    private boolean compatibleTypes(Class t1, Class t2) {
        if (t1 != String.class && t2 == String.class) {
            return false;
        } else if (t1 == String.class && t2 != String.class) {
            return false;
        } else {
            return true;
        }
    }

    /* Checks the formatting of a value and returns it's corresponding
     * type. If it matches with none of them, it returns an error message.
     */
    private String getValueType(String value) {
        String validInt = "-?\\d+";
        String validFloat = "-?(\\.\\d+|\\d+\\.\\d+|\\d+\\.)";
        String validString = "'+[^\\t\\n,'\"]+'";

        if (value.equals("NOVALUE")) {
            return "NOVALUE";
        } else if (value.equals("NaN")) {
            return "NaN";
        } else if (value.matches(validInt)) {
            return "int";
        } else if (value.matches(validFloat)) {
            return "float";
        } else if (value.matches(validString)) {
            return "string";
        } else {
            return "ERROR: Malformed data entry: " + value;
        }
    }

    private String typeToString(Class type) {
        if (type == Integer.class) {
            return "int";
        } else if (type == Float.class) {
            return "float";
        } else {
            return "string";
        }
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

    /* Takes in a list of columns that's in the format ["name type", "name type"..]
     * and puts it in a LinkedHashMap mapping each column name to its specified type.
     */
    private static LinkedHashMap<String, Class> getColumns(String[] columns) {
        LinkedHashMap<String, Class> cols = new LinkedHashMap<>();
        for (String col : columns) {
            String[] c = col.split("\\s");
            cols.put(c[0], getType(c[1]));
        }
        return cols;
    }

    // Returns the corresponding class if the supplied type is int, float, or string.
    private static Class getType(String type) {
        if (type.equals("int")) {
            return Integer.class;
        } else if (type.equals("float")) {
            return Float.class;
        } else {
            return String.class;
        }
    }

    /* Checks to see if the inputted name is valid. A name is valid if:
     *    - it doesn't start with a number
     *    - only contains letters and numbers
     */
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

    /* Checks if a given name is a valid table name. It is valid if:
     *     - it doesn't start with number and contains only letters and
     *       numbers
     *     - a table of that name isn't already stored in memory
     */
    private String checkTableName(String name) {
        if (!validName(name)) {
            return "ERROR: Invalid table name: " + name;
        } else if (tables.containsKey(name)) {
            return "ERROR: Table already exists: " + name;
        } else {
            return " ";
        }
    }

    /* Columns should be in the format ["name type", "name type",....]. Calls
     * validates each column in the array by calling validColumn on each element.
     */
    private String validateColumns(String[] columns) {
        String result;
        for (String col : columns) {
            String[] columnInfo = col.split(" ");
            if (!(result = validColumn(columnInfo)).equals(" ")) {
                return result;
            }
        }

        return " ";

    }

    /* Takes in an array columnInfo that should be of length two that should be
     * int the format: ["columnName", "columnType"]. Checks if the
     * column information is valid. It is valid if:
     *     - ColumnName doesn't start with a number and contains only
     *       letters and numbers
     *     - ColumnType is equal to either int, float, or string.
     *     - ColumnInfo should be a two element array containing columnName and
     *       ColumnType
     */
    private String validColumn(String[] columnInfo) {
        if (columnInfo.length == 1) {
            return "ERROR: Malformed column declaration: " + columnInfo[0];
        }

        String name = columnInfo[0];
        String type = columnInfo[1];
        if (!validName(name)) {
            return "ERROR: Invalid column name: " + name;
        } else if (!correctColumnType(type)) {
            return "ERROR: Invalid type: " + type;
        } else {
            return " ";
        }
    }

    private boolean correctColumnType(String type) {
        String[] types = new String[]{"int", "float", "string"};

        for (String t : types) {
            if (type.equals(t)) {
                return true;
            }
        }

        return false;
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
