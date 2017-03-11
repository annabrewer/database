package db;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {

    // Various common constructs, simplifies parsing.
    private static final String COMMA = "\\s*,\\s*",
                                AND   = "\\s+and\\s+";

    // Stage 2 syntax, contains the clauses of commands. Stage 1 handled in Database
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

    }


    /* Parses a command for creating a table. Determines if the create
     * command is either a create new or create from selecting command.
     * If the command matches neither, returns an error message.
     */
    public String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            String name = m.group(1);
            String columns = m.group(2);
            return createNewTable(name, columns);
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

    /* Parses a command to creates a new table with string name and with the
     * columns provided in the columns array. Columns should be in the format:
     *     - ["name type", "name type"...].
     * If the command is valid, returns an empty string.
     */
    private String createNewTable(String name, String columns) {
        String[] cols = columns.split(COMMA);
        String result;
        if (!(result = checkTableName(name)).equals(" ")) {
            return result;
        } else if (!(result = validateColumns(cols)).equals(" ")) {
            return result;
        } else {
            return "";
        }
    }

    /* Parses a command to create a table by selecting from a list of tables.
     * Arguments are in the format:
     *    - name: String of the name
     *    - colExpr: can either be:
     *        - "<column name> <operation> <column name or literal> as <name>,....."
     *        - "<column name>, <column name>, ...."
     *    - tbls: "<table name>, "<table name>,....."
     *    - conds: "<column name> <conditional> <column name or literal>, ...."
     * Select statements with column operations and column conditionals aren't supported.
     * If the command is correct, returns an empty string
     */
    private String createFromSelect(String name, String colExpr, String tbls, String conds) {
        Table joined = join(tbls);
        String result;
        if (!(result = checkTableName(name)).equals(" ")) {
            return result;
        } else if (!(result = checkTables(tbls)).equals(" ")) {
            return result;
        } else if (!(result = checkColumnExpressions(colExpr, joined)).equals(" ")) {
            return result;
        } else {
            if (conds == null) {
                return "";
            } else {
                if (!(result = checkConditionalExpressions(joined, conds)).equals(" ")) {
                    return result;
                } else {
                    return "";
                }
            }
        }
    }

    /* Parses a command for selecting from a table. Splits up the
     * command and analyzes it. If the command doesn't match the
     * format, returns an error message.
     */
    public String select(String expr) {
        Matcher m;
        if (!(m = SELECT_CLS.matcher(expr)).matches()) {
            return "ERROR: Malformed select: " + expr;
        } else {
            String colExpr = m.group(1);
            String tbls = m.group(2);
            String conds = m.group(3);
            return select(colExpr, tbls, conds);
        }
    }

    /* Analyzes the syntax of a select statement. Returns an error message
     * if the syntax is incorrect, or an empty string otherwise. Format of
     * parameters:
     *     - colExpr: <column> <arithmetic> <column or literal>,..... or
     *                <column>, <column>,.... or
     *                *, indicates selected all the columns
     *     - tbls: <table>, <table>,....
     *     - conds: <column> <conditional> <column or literal> and .....
     */
    private String select(String colExpr, String tbls, String conds) {
        String result;
        if (!(result = checkTables(tbls)).equals(" ")) {
            return result;
        } else if (!(result = checkColumnExpressions(colExpr, join(tbls))).equals(" ")) {
            return result;
        } else {
            if (conds != null) {
                Table t = tableFromSelect("dummy", colExpr, tbls);
                if (!(result = checkConditionalExpressions(t, conds)).equals(" ")) {
                    return result;
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    /* Returns the table resulting from selecting from the given tables using the
     * given columns with the given conditions applied to them. Formatting of inputs:
     *     - name: String name for resulting table
     *     - colExpr: <column>, <column>,... or * which means to select all columns of tbls
     *     - tbls: <table>, <table>,....
     *     - conds: <column> <conditional> <column or literal>
     */
    public Table tableFromSelect(String name, String colExpr, String tbls, String conds) {
        Table t;
        if (colExpr.equals("*")) {
            t = join(tbls);
        } else {
            String[] cols = colExpr.split(COMMA);
            ArrayList<String> selectedColumns = new ArrayList<>();

            for (String c : cols) {
                selectedColumns.add(c);
            }

            t = join(tbls).select(selectedColumns, "dummy");
        }

        Table newTable = evaluateConditionalExpressions(conds, t);
        return newTable;
    }

    /* Returns the table resulting from selecting from the given tables using the
     * given columns. Formatting of inputs:
     *     - name: String name for resulting table
     *     - colExpr: <column>, <column>,... or * which means to select all columns of tbls
     *     - tbls: <table>, <table>,....
     */
    public Table tableFromSelect(String name, String colExpr, String tbls) {
        Table joined = join(tbls);
        if (colExpr.equals("*")) {
            return joined;
        } else {
            ArrayList<Column> columns = evaluateColumnExpressions(colExpr, joined);
            LinkedHashMap<String, Class> columnInfo = new LinkedHashMap<>();
            for (Column c : columns) {
                String n = c.getName();
                Class t = c.getColumnType();
                columnInfo.put(n, t);
            }

            Table newTable = new Table(name, columnInfo);
            newTable.insertValues(columns);
            return newTable;
        }
    }

    /* Evaluates several conditional statements being called onto the given table.
     * The conditional statements are in the format:
     *     - <column> <conditional> <column or literal,....
     * Returns the table resulting from the conditional statements.
     */
    private Table evaluateConditionalExpressions(String conds, Table tbl) {
        String[] expressions = conds.split(AND);

        for(String cond : expressions) {
            tbl = evaluateConditionalExpression(cond, tbl);
        }

        return tbl;
    }

    /* Evaluates the given conditional statement being called onto the given table.
     * The conditional statement is in the format:
     *     - <column> <conditional> <column or literal>
     * Returns the table resulting from this conditional statement.
     */
    private Table evaluateConditionalExpression(String cond, Table tbl) {
        String[] parts = cond.split(" ");
        String column = parts[0];
        Conditionals conditional = getConditional(parts[1]);
        String operand = parts[2];
        if (operand.matches("-?(\\.\\d+|\\d+\\.\\d+|\\d+\\.)|" +
                                   "-?\\d+|'+[^\\t\\n,'\"]+'")) {
            return tbl.select(column, conditional, getValue(operand), "dummy");
        } else {
            return tbl.select(column, conditional, operand, "dummy");
        }
    }

    private Conditionals getConditional(String c) {
        if (c.equals("<")) {
            return Conditionals.LESS_THAN;
        } else if (c.equals("<=")) {
            return Conditionals.LESS_OR_EQUAL_TO;
        } else if (c.equals("==")) {
            return Conditionals.EQUALS;
        } else if (c.equals("!=")) {
            return Conditionals.NOT_EQUAL_TO;
        } else if (c.equals(">")) {
            return Conditionals.GREATER_THAN;
        } else {
            return Conditionals.GREATER_OR_EQUAL_TO;
        }
    }

    /* Evaluates the column expressions given and returns a list of columns that are
     * the product of those expressions. ColExpr is in the format:
     *     - <column> <arithmetic> <column or literal> as <name>,.....
     *     - or <column>, <column>,....
     */
    private ArrayList<Column> evaluateColumnExpressions(String colExpr, Table tbl) {
        String[] expressions = colExpr.split(COMMA);
        ArrayList<Column> columns = new ArrayList<>();

        for (String expr : expressions) {
            String[] parts = expr.split("\\s+as\\s+");
            String e = parts[0];
            String n;
            Column column;
            Column dummy = evaluateColumnExpression(e, tbl);
            if (parts.length == 1) {
                n = parts[0];
                Class type = tbl.getColumnTypes().get(n);
                column = new Column(n, type);
                column.addValues(dummy.getValues());
            } else {
                n = parts[1];
                column = new Column(n, dummy.getColumnType());
                column.addValues(dummy.getValues());
            }
            columns.add(column);
        }

        return columns;
    }


    /* Evaluates the given column expression and returns a dummy column of
     * the values that are the result of that expression.
     * Expr is in the format:
     *     - <column> <arithmetic> <column or literal>
     *     - or <column>
     */
    private Column evaluateColumnExpression(String expr, Table tbl) {
        Pattern format = Pattern.compile("(\\w+)\\s*+([-+/*])\\s*(\\S+)");
        Matcher m = format.matcher(expr);
        Class type;
        ArrayList<Value> values;

        if (!m.matches()) {
            Column col = tbl.getColumns().get(expr);
            values = col.getValues();
            type = col.getColumnType();
        } else {
            Column c = tbl.getColumns().get(m.group(1));
            Arithmetic operation = getArithmeticOperation(m.group(2));
            String operand = m.group(3);

            if (operand.matches("-?(\\.\\d+|\\d+\\.\\d+|\\d+\\.)|" +
                    "-?\\d+|'+[^\\t\\n,'\"]+'")) {
                Value v = getValue(operand);
                Column col = operation.apply(c, v, "dummy");
                values = col.getValues();
                type = col.getColumnType();
            } else {
                Column other = tbl.getColumns().get(operand);
                Column col = operation.apply(c, other, "dummy");
                values = col.getValues();
                type = col.getColumnType();
            }
        }
        Column dummy = new Column("dummy", type);
        dummy.addValues(values);
        return dummy;
     }

    private Arithmetic getArithmeticOperation(String arithmetic) {
        if (arithmetic.equals("+")) {
            return Arithmetic.ADD;
        } else if (arithmetic.equals("-")) {
            return Arithmetic.SUBTRACT;
        } else if (arithmetic.equals("*")) {
            return Arithmetic.MULTIPLY;
        } else {
            return Arithmetic.DIVIDE;
        }
     }

     private Value getValue(String value) {
        String i = "-?\\d+";
        String f = "-?(\\.\\d+|\\d+\\.\\d+|\\d+\\.)";
        String s = "'+[^\\t\\n,'\"]+'";
        if (value.matches(i)) {
            int item = Integer.parseInt(value);
            return new Value(item);
        } else if (value.matches(f)) {
            float item = Float.parseFloat(value);
            return new Value(item);
        } else {
            return new Value(value);
        }
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
        return tables.containsKey(tbl);
    }

    private String checkTables(String tbls) {
        if (!containsAllTables(tbls)) {
            return "ERROR: No such table: " + missingTable(tbls);
        } else {
            return " ";
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

    /* Checks if the conditional statements are all valid. Conds should be in
     * the format:
     *     - <column> <conditional> <column or literal> and ......
     * A conditional is valid if:
     *     - it generally fits the above format
     *     - the listed columns are contained in the table being used in the statement
     *     - if the second operand isn't a column, then it must be a valid literal
     */
    private String checkConditionalExpressions(Table tbl, String conds) {
        String validConditional = "\\w+\\s+[<>=!]+\\s+\\S+?(\\s+and\\s+\\w+\\s+[<>=!]+\\s+\\S+\\s*)*";
        if (!conds.matches(validConditional)) {
            return "ERROR: Malformed conditional: " + conds;
        }
        String[] separatedConditionals = conds.split(AND);
        String result;
        for (String conditionals : separatedConditionals) {
            if (!(result = checkConditionalExpression(tbl, conds)).equals(" ")) {
                return result;
            }
        }
        return " ";
    }

    /* Checks if this particular conditional statement is valid. The string cond
     * is in the format:
     *     - <column> <conditional> <column or literal>
     * A conditional is valid if:
     *     - it generally fits the above format
     *     - the listed columns are contained in the table being used in the statement
     *     - if the second operand isn't a column, then it must be a valid literal
     * Prints an error message if one of the above isn't fulfilled, or an empty string
     * if it's valid.
     */
    private String checkConditionalExpression(Table tbl, String cond) {
        String[] conditionalParts = cond.split("\\s+");
        String column = conditionalParts[0];
        String condition = conditionalParts[1];
        String operand = conditionalParts[2];
        if (!tbl.containsColumn(column)) {
            return "ERROR: No such column with name: " + column;
        } else if (!condition.matches("<|>|<=|>=|==|!=")) {
            return "ERROR: Malformed conditional: " + cond;
        } else if (!tbl.containsColumn(operand) && !validLiteral(operand)) {
            return "ERROR: Malformed column expression: " + cond;
        } else if (tbl.containsColumn(operand)) {
            Class type1 = tbl.getColumnTypes().get(column);
            Class type2 = tbl.getColumnTypes().get(operand);
            if (!compatibleTypes(type1, type2)) {
                return "ERROR: Incompatible types: " +
                        typeToString(type1) + " and " + typeToString(type2);
            } else {
                return " ";
            }
        } else if (validLiteral(operand)) {
            Class type1 = tbl.getColumnTypes().get(column);
            Class type2 = getType(getValueType(operand));
            if (!compatibleTypes(type1, type2)) {
                return "ERROR: Incompatible types: " +
                        typeToString(type1) + " and " + typeToString(type2);
            } else {
                return " ";
            }
        } else {
            return " ";
        }
    }

    /* Checks to see if the given column expressions are valid. ColExpr is in
     * the format of either:
     *     - "<column name> <operation> <column name or literal> as <name>,....."
     *     - "<column name>, <column name>, ...."
     *     - or *, which means we are selecting all the columns from the table
     */
    private String checkColumnExpressions(String colExpr, Table t) {
        if (colExpr.equals("*")) {
            return " ";
        }
        String[] columnExpressions = colExpr.split("\\s*,+\\s+");
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
        String arithmetic = "\\w+\\s*([+-/*])+\\s*\\S+\\s+as+\\s+\\S+";
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
        } else if (!(result = validArithmeticParts(arithmetic, t)).equals(" ")) {
            return result;
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
        Pattern format = Pattern.compile("(\\w+)\\s*+([-+/*])\\s*(\\S+)");
        Matcher m = format.matcher(expr);
        if (!m.matches()) {
            return "ERROR: Malformed column expression: " + expr;
        }
        String column = m.group(1);
        String arithmetic = m.group(2);
        String operand = m.group(3);

        if (!arithmetic.matches("[-+/*]")) {
            return "ERROR: Malformed column expression :" + expr;
        } else if (!t.containsColumn(column)) {
            return "ERROR: No such column with name: " + column;
        } else if (!t.containsColumn(operand) && !validLiteral(operand)) {
            return "ERROR: Malformed no such column: " + operand;
        } else if (t.containsColumn(operand)) {
            Class type1 = t.getColumnTypes().get(column);
            Class type2 = t.getColumnTypes().get(operand);
            if (!compatibleTypes(type1, type2)) {
                return "ERROR: Incompatible types: " +
                        typeToString(type1) + " and " + typeToString(type2);
            } else if (type1 == String.class && !arithmetic.equals("+")) {
                return "ERROR: Malformed column expression: " + expr;
            } else {
                return " ";
            }
        } else if (validLiteral(operand)) {
            Class type1 = t.getColumnTypes().get(column);
            Class type2 = getType(getValueType(operand));
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
        String validFloat = "-?(\\.\\d+|\\d+\\.\\d+|\\d+\\.)";
        String validInt = "-?\\d+";
        String validString = "'+[^\\t\\n,'\"]+'";

        if (literal.matches(validFloat)) {
            return true;
        } else if (literal.matches(validInt)) {
            return true;
        } else if (literal.matches(validString)) {
            return true;
        } else if (literal.equals("NOVALUE") || literal.equals("NaN")) {
            return true;
        } else {
            return false;
        }
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
    public LinkedHashMap<String, Class> getColumns(String[] columns) {
        LinkedHashMap<String, Class> cols = new LinkedHashMap<>();
        for (String col : columns) {
            String[] c = col.split("\\s+");
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
            if (!ch.matches("[\\w\\d]+")) {
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
            String[] columnInfo = col.split("\\s+");
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

    public String storeTable(String name) {
        try (PrintWriter out = new PrintWriter(name + ".tbl")) {
            out.println(tables.get(name).toString());
        } catch (IOException e) {
            return "ERROR: Can't read file " + name;
        }
        return "";
    }

    /* Parses a command to drop the given table from the
     * database. Returns an error if the given table
     * doesn't exist.
     */
    public String dropTable(String name) {
        if (containsTable(name)) {
            return "";
        } else {
            return "ERROR: No such table: " + name;
        }
    }

    /* Parses a command to insert a row of values into a
     * given table. Breaks up each part of the insert clause
     * and analyzes each individually.
     */
    public String checkInsertRowCommand(String cmd) {
        Matcher m = INSERT_CLS.matcher(cmd);
        if (m.matches()) {
            String[] insertClauses = cmd.split("\\s+values\\s+");
            String tableName = insertClauses[0];
            String values = insertClauses[1];
            return checkInsertRow(tableName, values);
        } else {
            return "ERROR: Malformed insert: " + cmd;
        }
    }

    /* Parses insert command and carries it out. */
    public Row evaluateInsertRow(String tbl, String values) {
        ArrayList<Value> rowValues = new ArrayList<>();

        ArrayList<String> columnNames = tables.get(tbl).getColumnNames();
        LinkedHashMap<String, Class> columnTypes = tables.get(tbl).getColumnTypes();
        Iterator<String> names = columnNames.iterator();
        String[] listValues = values.split("\\s*,\\s*");
        for (String value : listValues) {
            String column = names.next();

            Value v;
            if (value.equals("NOVALUE")) {
                Class type = columnTypes.get(column);
                v = new Value(DataType.NOVALUE, type);
            } else {
                v = getValue(value);
            }
            rowValues.add(v);
        }

        return new Row(columnNames, rowValues);
    }

    /* Parses a command to insert a row of values into
     * the given table. Analyzes each part of the insert
     * clause.
     */
    private String checkInsertRow(String tbl, String values) {
        String result;
        if (!containsTable(tbl)) {
            return "ERROR: No such table: " + tbl;
        } else if (!(result = checkValues(tbl, values)).equals(" ")) {
            return result;
        } else {
            return "";
        }
    }

    /* Checks to see if the values to be inputted into the given table
     * are correct. Arguments are in the format:
     *     - tbl: name of table to insert values
     *     - value: <literal>, <literal>, <literal>,....
     * The values are correct if their order matches the order of the
     * column types and if they're all valid literals.
     */
    private String checkValues(String tbl, String values) {
        String[] listValues = values.split("\\s*,\\s*");
        for (String value : listValues) {
            if (!validLiteral(value)) {
                return "ERROR: Invalid data entry" + value;
            }
        }
        Table t = tables.get(tbl);
        ArrayList<String> columnNames = t.getColumnNames();
        if (listValues.length != columnNames.size()) {
            return "ERROR: Row does not match table";
        }

        Iterator<String> names = columnNames.iterator();
        LinkedHashMap<String, Class> columnTypes = t.getColumnTypes();
        for (String value : listValues) {
            if (!value.equals("NOVALUE")) {
                Class type = columnTypes.get(names.next());
                Value v = getValue(value);
                if (type != v.getItemClass()) {
                    return "ERROR: Row does not match table";
                }
            }
        }

        return " ";
    }


    /* Parses a command to print the given table. Returns
     * an error if the the given table isn't in the database.
     */
    public String printTable(String name) {
        if (containsTable(name)) {
            return "";
        } else {
            return "ERROR: No such table: " + name;
        }
    }
}
