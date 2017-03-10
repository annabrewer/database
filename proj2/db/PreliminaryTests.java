package db;

import java.io.*;
import java.util.*;

/**
 * Created by Anna on 3/8/17.
 */
public class PreliminaryTests {

    public static HashMap<String, Table> tbls = new HashMap<String, Table>();

    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add(0, "Lastname");
        columnNames.add(1, "Firstname");
        columnNames.add(2, "TeamName");

        LinkedHashMap<String, Class> columnTypes = new LinkedHashMap<String, Class>();
        columnTypes.put("Lastname", String.class);
        columnTypes.put("Firstname", String.class);
        columnTypes.put("TeamName", String.class);

        Table t = new Table("fans", columnTypes);

        String[][] fans = {{"Lee", "Maurice", "Mets"}, {"Lee", "Maurice", "Steelers"}, {"Ray", "Mitas", "Patriots"}, {"Hwang", "Alex", "Cloud9"},
                {"Rulison", "Jared", "EnVyUs"}, {"Fang", "Vivian", "Golden Bears"}};

        for (String[] s : fans) {
            ArrayList<Value> vals = new ArrayList<Value>();
            for (String st : s) {
                vals.add(new Value(st));
            }
            Row r = new Row(columnNames, vals);
            t.insertValues(r);
        }

        tbls.put("fans", t);
        t.print();

        String x = storeTable("fans");
        x = dropTable("fans");
        x = loadTable("fans");

        System.out.print(tbls.get("fans").toString());

        insertRow("fans", "Brewer,Anna,Memes");

        System.out.print(tbls.get("fans").toString());

    }


    public static String loadTable(String name) {
        try {
            FileReader fileReader = new FileReader(name + ".tbl"); //im assuming we dont need file path because it's in same directory??

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String colsString = bufferedReader.readLine(); //column names and types
            String[] colsArray = colsString.split(COMMA); //array of name-type pairs as strings
            ArrayList<String[]> cols = new ArrayList<>(); //arraylist of name-type pairs as arrays of strings
            ArrayList<String> colNames = new ArrayList<>(); //arraylist of column names

            for (int i = 0; i < colsArray.length; i++) {
                cols.add(colsArray[i].split("\\s"));
            }

            LinkedHashMap<String, Class> colsInput = new LinkedHashMap<String, Class>();

            for (int i = 0; i < colsArray.length; i++) {
                String type = cols.get(i)[1];
                String colName = cols.get(i)[0];
                colNames.add(colName); //will be useful later

                Class c;

                if (type.toLowerCase().equals("int")) {
                    c = Integer.class;
                } else if (type.toLowerCase().equals("float")) {
                    c = Float.class;
                } else if (type.toLowerCase().equals("string")) {
                    c = String.class;
                }
                else {
                    return "Error";
                }

                colsInput.put(colName, c);
            }

            Table t = new Table(name, colsInput);

            String line;
            //line = bufferedReader.readLine();
            line = bufferedReader.readLine();

            while (!(line.equals(""))) {
                String[] stringRow = line.split(COMMA);
                ArrayList<Value> valuesList = new ArrayList<Value>();
                for (String s : stringRow) {
                    valuesList.add(toValue(s));
                }

                Row r = new Row(colNames, valuesList);
                t.insertValues(r);
                line = bufferedReader.readLine();
            }

            tbls.put(name, t);

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            name + ".tbl" + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + name + ".tbl" + "'");
        }

        return "";
    }

    public static String storeTable(String name) {
        try (PrintWriter out = new PrintWriter(name + ".tbl")) {
            out.println(tbls.get(name).toString());
        } catch (IOException e) {
            return "Error";
        }
        return "";
    }

    public static String dropTable(String name) {
        tbls.remove(name);
        return "";
    }

    public static String insertRow(String name, String expr) {
        if (tbls.containsKey(name)) {
            String[] valuesStrings = expr.split(COMMA);
            Table t = tbls.get(name);
            ArrayList<Value> values = new ArrayList<Value>();

            for (String s : valuesStrings) {
                values.add(toValue(s));
            }

            ArrayList<String> colNames = t.getColumnNames();

            if (values.size() == colNames.size()) {

                LinkedHashMap<String, Class> colTypes = t.getColumnTypes();
                Iterator<Value> iterateValues = values.iterator();

                for (String col : colNames) {
                    Class classOfValue = iterateValues.next().getItemClass();
                    Class classInColumn = colTypes.get(col);
                    if (classOfValue != classInColumn) {
                        return "Type of value does not correspond to type of column";
                    }
                }

                Row r = new Row(colNames);
                r.insertValues(values);
                t.insertValues(r);
                return "";
            } else {
                return "Wrong number of values in row";
            }
        } else {
            return "Error:table does not exist";
        }
    }

    public static String printTable(String name) {
        if (tbls.containsKey(name)) {
            return tbls.get(name).toString();
        } else {
            return "Error:table does not exist";
        }
    }

    //helper

    public static Value toValue(String s) {
        if (s.matches("[0-9]+")) {
            if (s.contains(".")) {
                float f = Float.parseFloat(s);
                Value v = new Value(f);
                return v; // .parseFloat(s); //use parseFloat and parseInt to get primitive types
            }
            int i = Integer.parseInt(s);
            Value v = new Value(i);
            return v;  //.parseInt(s);
        } else {
            Value v = new Value(s);
            return v;
        }
    }

}

    /*ArrayList<String> columnNames = new ArrayList<String>();
    Object[][] cat = {{"TeamName", String.class}, {"City", String.class}, {"Sport", String.class}, {"YearEstablished", int.class}, {"Mascot", String.class}, {"Stadium", String.class}};
        for (int i = 0; i < cat.length; i++) {
        String s = (String)cat[i][0];
        columnNames.add(i, s);
    }

    LinkedHashMap<String, Class> columnTypes = new LinkedHashMap<String, Class>();
        for (int i = 0; i < cat.length; i++) {
        String s = (String)cat[i][0];
        Class c = (Class)cat[i][1];
        columnTypes.put(s, c);
    }

    Object[][] fans = {{"Mets","New York","MLB Baseball",1962,"Mr. Met","Citi Field"}, {"Steelers","Pittsburgh","NFL Football",1933,"Steely McBeam","Heinz Field"},
            {"Patriots","New England","NFL Football",1960,"Pat Patriot","Gillette Stadium"}, {"Cloud9","Los Angeles","eSports",2012, NOVALUE, NOVALUE"},
            {"EnVyUs","Charlotte","eSports",2007, NOVALUE, NOVALUE}, {"Golden Bears","Berkeley","NCAA Football",1886,"Oski","Memorial Stadium"}};
        
        for (Object[] s : fans) {
        ArrayList<Value> vals = new ArrayList<Value>();
        for (Object st : s) {
            Class c = st.getClass();
            vals.add(new Value((c)st));
        }
        Row r = new Row(columnNames, vals);
        t.insertValues(r);
    }
    Table t = new Table("fans", columnTypes); */
