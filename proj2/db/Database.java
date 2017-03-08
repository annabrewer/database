package db;

import com.sun.rowset.internal.Row;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Database {

    private HashMap<String, Table> tables;
    private Parser parser;

    public Database() {
        tables = new HashMap<>();
        parser = new Parser(tables);
    }

    public String transact(String query) {
<<<<<<< HEAD
        return "";
    }

    private String createTable(String cmd) {
        return parser.createTable(cmd);
=======
        /*Parser p = new Parser();
        Object[] input = p.eval(query);
        String cmd = input[0]
=======
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
>>>>>>> f43fce77af83751084f15bdd2c4b2d4bcca31a04

        return "";
>>>>>>> 892e721a97f6bcf0e6764c2addb8f8ce522d0759
    }

    private String select(String cmd) {
        return parser.select(cmd);
    }

    private String printTable(String cmd) {
        return parser.printTable(cmd);
    }

    private String loadTable(String cmd) {
        return parser.loadTable(cmd);
    }

    private String dropTable(String cmd) {
        return parser.dropTable(cmd);
    }

<<<<<<< HEAD
    private String insertValues(String cmd) {
        return parser.insertRow(cmd);
    }

=======
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

<<<<<<< HEAD
    public Table[] tablesToArray (String tablesInput) {
        String[] tablesStrings = tablesInput.split(COMMA);
        for (String s: tablesStrings) {
            if(tables.containsKey(s)); {
                tablesInput.add(tables.get(s)); //look up tables in db & add them to list
            }
            else {
                return null;
            }
=======
    //old stuff - i wrote a really long selecthelper method and idk what happened to it???

<<<<<<< HEAD
        }*/
        return "";
=======
    /*public static Table selectHelper(String colsInput, String tablesInput, String condsInput) {
        String[] colsNames = colsInput.split(COMMA);
        String[] tablesToJoinStrings = tablesInput.split(COMMA);
        ArrayList<Table> tablesToJoin = new ArrayList<Table>();
        for (String s: tablesToJoinStrings) {
            tablesToJoin.add(tables.get(s)); //look up tables in db & add them to list
>>>>>>> 70456d60f7169cb2e1164ce8cc4c82670adf607f
        }
    }

<<<<<<< HEAD
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
=======
        Table result = Table.join(tablesToJoin, exprs, conds);
        tables.put(m.group(1), result);
>>>>>>> f43fce77af83751084f15bdd2c4b2d4bcca31a04
>>>>>>> 70456d60f7169cb2e1164ce8cc4c82670adf607f
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

>>>>>>> 892e721a97f6bcf0e6764c2addb8f8ce522d0759
}
