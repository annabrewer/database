package db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Database {

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
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*"
                                               + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
                                 SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                                               + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                                               + "([\\w\\s+\\-'<>=!.]+?(?:\\s+and\\s+"
                                               + "[\\w\\s+\\-'<>=!.]+?)*))?"),
                                 CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+"
                                                  + SELECT_CLS.pattern()),
                                 INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                                               + "\\s*(?:,\\s*.+?\\s*)*)");


    private HashMap<String, Table> tables;
    private Parser parser;

    public Database() {
        tables = new HashMap<>();
        parser = new Parser(tables);
    }

    public String transact(String query) {
        String result;
        if (!(result = eval(query)).equals("")) {
            return result;
        }
        return "";
    }

    public String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            return "ERROR: Malformed query: " + query;
        }
    }

    private String createTable(String cmd) {
        String result;
        if (!(result = parser.createTable(cmd)).equals("")) {
            return result;
        } else {
            Matcher m;
            if ((m = CREATE_NEW.matcher(cmd)).matches()) {
                String name = m.group(1);
                String columns = m.group(2);
                return createNewTable(name, columns);
            } else if ((m = CREATE_SEL.matcher(cmd)).matches()) {
                String name = m.group(1);
                String colExpr = m.group(2);
                String tbls = m.group(3);
                String conds = m.group(4);
                return createFromSelect(name, colExpr, tbls, conds);
            } else {
                return "ERROR: Malformed create: " + cmd;
            }
        }
    }

    private String createNewTable(String name, String columns) {
        String[] columnsList = columns.split(COMMA);
        LinkedHashMap<String, Class> columnInfo = parser.getColumns(columnsList);

        Table newTable = new Table(name, columnInfo);
        tables.put(name, newTable);

        return "";
    }

    private String createFromSelect(String name, String colExpr, String tbls, String conds) {
        Table newTable;
        if (conds == null) {
            newTable = parser.tableFromSelect(name, colExpr, tbls);
        } else {
            newTable = parser.tableFromSelect(name, colExpr, tbls, conds);
        }

        tables.put(name, newTable);
        return "";
    }

    private String select(String cmd) {
        String result;
        if (!(result = parser.select(cmd)).equals("")) {
            return result;
        } else {
            Matcher m = SELECT_CLS.matcher(cmd);
            m.matches();
            String colExpr = m.group(1);
            String tbls = m.group(2);
            String conds = m.group(3);
            Table t;
            if (conds == null) {
                t = parser.tableFromSelect("dummy", colExpr, tbls);
            } else {
                t = parser.tableFromSelect("dummy", colExpr, tbls, conds);
            }
            return t.toString();
        }
    }

    private String printTable(String cmd) {
        String result;
        if (!(result = parser.printTable(cmd)).equals("")) {
            return result;
        } else {
            Table t = tables.get(cmd);
            return t.toString();
        }
    }

    private String dropTable(String cmd) {
        String result;
        if (!(result = parser.dropTable(cmd)).equals("")) {
            return result;
        } else {
            tables.remove(cmd);
            return "";
        }
    }

    private String insertRow(String cmd) {
        String result;
        if (!(result = parser.checkInsertRowCommand(cmd)).equals("")) {
            return result;
        } else {
            String[] insertClauses = cmd.split("\\s+values\\s+");
            String tableName = insertClauses[0];
            String values = insertClauses[1];

            Row newRow = parser.evaluateInsertRow(tableName, values);
            Table tbl = tables.get(tableName);
            tbl.insertValues(newRow);

            return "";
        }
    }

    private String loadTable(String name) {
        if (!name.matches("\\D\\S*")) {
            return "ERROR: No such table: " + name;
        } else {
            try {
                FileReader reader = new FileReader(name + ".tbl");
                BufferedReader tableFileReader = new BufferedReader(reader);
                String result;
                String[] columns;
                if ((result = tableFileReader.readLine()) == null) {
                    tableFileReader.close();
                    return "ERROR: Cannot read empty TBL file.";
                } else {
                    columns = result.split(",");
                }
                if (!(result = parser.validateColumns(columns)).equals(" ")) {
                    tableFileReader.close();
                    return result;
                }
                LinkedHashMap<String, Class> columnInfo = parser.getColumns(columns);
                Table loadedTable = new Table(name, columnInfo);
                if (tables.containsKey(name)) {
                    tables.replace(name, loadedTable);
                } else {
                    tables.put(name, loadedTable);
                }

                String row;
                while ((row = tableFileReader.readLine()) != null) {
                    String r;
                    if (row.matches("\\n|\\s*")) {
                        tableFileReader.close();
                        return "";
                    }
                    if (!(r = parser.checkValues(name, row)).equals(" ")) {
                        tableFileReader.close();
                        return r;
                    }
                    ArrayList<Value> rowValues = new ArrayList<>();
                    String[] values = row.split(",");
                    for (String v : values) {
                        Value val = parser.getValue(v);
                        rowValues.add(val);
                    }
                    Row newRow = new Row(loadedTable.getColumnNames(), rowValues);
                    loadedTable.insertValues(newRow);
                }
                tableFileReader.close();
                return "";
            } catch (FileNotFoundException e) {
                return "ERROR: No such TBL file: " + name;
            } catch (IOException e) {
                return "ERROR: Could not read file: " + name;
            }
        }
    }

    private String storeTable(String name) {
        return parser.storeTable(name);
    }

    public static void main(String[] args) {
        Database db = new Database();

        System.out.println(db.transact("create table t (x int, y int)"));
        System.out.println(db.transact("insert into t values 1, 2"));
        System.out.println(db.transact("insert into t values 3, 4"));
        System.out.println(db.transact("insert into t values 5, 6"));
        System.out.println(db.transact("insert into t values NOVALUE, NOVALUE"));
        System.out.println(db.transact("create table s as select x / 0 as bob from t"));
    }

}

