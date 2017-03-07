package db;

import com.sun.rowset.internal.Row;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Database {

    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // \s is whitespace, \S is anything but whitespace

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    public static HashMap<String, Table> tables = new HashMap<String, Table>();

    public Database() {
        //init tables here or above - does it make a diff?
    }

    public String transact(String query){
        //return "";
        return eval(query);
    }

    public static String eval(String query) {
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
            return "Malformed query";
        }
    }

    public String createTable(String expr) {
        Matcher m;
        String tableName = m.group(1);
        String columns = m.group(2); //the cols as 1 long string of comma separated name-type pairs

        LinkedHashMap<String, Class> colsLHM = columnsToLinkedHash(columns);
        if (colsLHM == null;) {
            return "Incorrect column type"
        }

        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            tables.put(tableName, new Table(tableName, colsLHM));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            //index 1: name, index 2: expressions(cols), index 3: tables, index 4: conds
            Matcher m;
            String tables = m.group(3);
            String conds = m.group(4);

            selectHelper(columns, tables, conds);



            String[] columnsStrings = cols.split(COMMA);

            Table temp = new Table(tableName, )
            Table temp2 = tableSelect(temp, cols, tables, conds);
            tables.put(tableName, temp);

        } else {
            return "Malformed create";
        }
        return "";
    }

    public static String loadTable(String name) {
        try {
            FileReader fileReader = new FileReader(name+".tbl"); //im assuming we dont need file path because it's in same directory??

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            ArrayList<Row> rowsList = new ArrayList<Row>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] stringRow = line.split("\\s"); //two slashes or one??
                ArrayList<Value> valuesList = new ArrayList<Value>();
                for (String s : stringRow) {
                    valuesList.add(new Value(intOrFloat(s)));
                }
                Row r = new Row(valuesList);
                rowsList.add(r);
            }

            tables.put(new Table(rowsList));

            bufferedReader.close();
        }
       catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            name+".tbl" + "'");
        }
       catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + name+".tbl" + "'");
        }

        return "";
    }

    public static String  storeTable(String name) {
        Object[] result = {"store", name};
        return "";
    }

    public static String dropTable(String name) {
        tables.remove(name);
        return "";
    }

    public static String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return "Malformed insert";
        }
        String[] stringRow = expr.split("\\s");
        ArrayList<Value> valuesList = new ArrayList<Value>();
        for (String s : stringRow) {
            valuesList.add(new Value(intOrFloat(s)));
        }
        Row r = new Row(valuesList);
        //index 1 is table, index 2 is row to be inserted
        tables.get(m.group(1)).insert(r);
        return "";
    }

    public static String printTable(String name) {
        return tables.get(name).stringRep;
    }

    public static String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return new Object[1];
        }
        //repeated code but whatever
        String columns = m.group(3);
        String tables = m.group(3);
        String cond = m.group(4);

        LinkedHashMap<String, Class> colsLHM = columnsToLinkedHash(columns);
        Table[] tablesAL = tablesToArray(tables);
        Conditionals cond = condToConditionals(cond);

        if (colsLHM == null) {

        }

        //check if null
        //test length of tablesarray

        return result;
    }

    //helpers
    //parses strings to cols, tables, conds
    //calls another method which chooses correct method from Table class
    public Table selectHelper(String columns, String tables, String cond) {

    }

    public Table[] tablesToArray (String tablesInput) {
        String[] tablesStrings = tablesInput.split(COMMA);
        for (String s: tablesStrings) {
            if(tables.containsKey(s)); {
                tablesInput.add(tables.get(s)); //look up tables in db & add them to list
            }
            else {
                return null;
            }
        }
    }

    public Conditionals condToConditionals (String condInput) {

    }

    public LinkedHashMap<String, Class> columnsToLinkedHash(String colsInput) {
        LinkedHashMap<String, Class> cols = new LinkedHashMap<String, Class>();
        String[] columnsStrings = colsInput.split(COMMA); //the cols as a list of strings, each containing name & type
        for (String s : columnsStrings) {
            String[] nameAndType = s.split("\\s"); //name and type of a single column
            String name = s[0];
            String type = s[1];
            if (type.equals("int")) {
                cols.put(name, Integer.class);
            }
            if (type.equals("float")) {
                cols.put(name, Float.class);
            }
            if (type.equals("string")) {
                cols.put(name, String.class);
            }
            else {
                return null;
            }
        }
        return cols;
    }

    public LinkedHashMap<String, Table> tablesToLinkedHash(String tablesInput)

    public void intOrFloat(String s) {

        if (s.matches("[0-9]+")) {
            return s;
            if (s.contains(".")){
                return Float.parseFloat(s); //returns Float object - use parseFloat and parseInt to get primitive types
            }
            return Integer.parseInt(s);
        }
        else {
            return s;
        }
    }


    //old stuff - i wrote a really long selecthelper method and idk what happened to it???

    /*public static Table selectHelper(String colsInput, String tablesInput, String condsInput) {
        String[] colsNames = colsInput.split(COMMA);
        String[] tablesToJoinStrings = tablesInput.split(COMMA);
        ArrayList<Table> tablesToJoin = new ArrayList<Table>();
        for (String s: tablesToJoinStrings) {
            tablesToJoin.add(tables.get(s)); //look up tables in db & add them to list
        }
        String[] conds = m.group(4).split(COMMA);

        Table result = Table.join(tablesToJoin, exprs, conds);
        tables.put(m.group(1), result);
    }

    */

}
