package db;

import java.util.ArrayList;
import java.io.*;

public class Database {
    ArrayList<Table> tables;

    public Database() {
        tables = new ArrayList<Table>();
    }

    public String transact(String query) {
        Parser p = new Parser();
        Object[] input = p.eval(query);
        String cmd = input[0]

        if (cmd.equals("create new")) {
            String tableName = (String)input[1];
            String[] colsList = (String[])input[2];
            tables.add(new Table(tableName, colsList));
            return "";
        } else if (cmd.equals("create selected")) {
            //index 1: name, index 2: expressions(cols), index 3: tables, index 4: conds
            String tableName = (String)input[1];
            String[] colsList = (String[])input[2];
            String[] tablesList = (String[])input[3];
            String[] condsList = (String[])input[4];

            Table temp = Table.join(expressionsList, tablesList, condsList);

            tables.add(temp);

            return "";
        } else if (cmd.equals("load")) {

            //handle case where table exists already - remove it and replace it
            try {
                FileReader fileReader = new FileReader(input[1]+".tbl"); //im assuming we dont need file path because it's in same directory??

                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);

                ArrayList<ArrayList<String>> rowsList = new ArrayList<ArrayList<String>>();
                while ((line = bufferedReader.readLine()) != null) {
                    ArrayList<String> row = new ArrayList<String>(Arrays.asList(line.split"\\s")); //one backslash or two?
                    intsAndFloats(row);
                    rowsList.add(row);
                }

                tables.add(new Table((String)input[1],rowsList))

                bufferedReader.close();
            }
            catch(FileNotFoundException ex) {
                System.out.println(
                        "Unable to open file '" +
                                fileName + "'");
            }
            catch(IOException ex) {
                System.out.println(
                        "Error reading file '"
                                + fileName + "'");
            }
            return "";
        } else if (cmd.equals("store")) {
            try {

                Table temp = tableWithName((String)input[1]); //might return null if name DNE - deal with this!!

                fw = new FileWriter(temp.name+".tbl");
                bw = new BufferedWriter(fw);
                bw.write(temp.stringRep);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return "";
        } else if (cmd.equals("drop")) {
            Table temp = tableWithName((String)input[1]);
            tables.remove(temp);
            return "";
        } else if (cmd.equals("insert")) {
            Table temp = tableWithName((String)input[1]);
            temp = temp.insert(input[2]);
            return "";
        } else if (cmd.equals("print")) {
            Table temp = tableWithName((String)input[1]);
            return(temp.stringRep);

        } else if (cmd.equals("select")) {
            //index 1 is expressions, index 2 is tables to be joined, index 3 is conditions to filter
            String[] colsList = (String[])input[2];
            String[] tablesList = (String[])input[3];
            String[] condsList = (String[])input[4];

            Table temp = Table.join(expressionsList, tablesList, condsList);
            return (temp.stringRep);
        }
    }

    //helper method to convert strings to ints and floats
    public void intsAndFloats(ArrayList<String> rowInput) {
        ArrayList<String> result = new ArrayList<String>(); //is this the right constructor?
        for (String s: rowInput) {
            if (s.matches("%[a-zA-Z]%")) {
                result.add(s);
            }
            else if s.contains("."){
                result.add(Float.valueOf(s)); //returns Float object - use parseFloat and parseInt to get primitive types
            }
            else {
                result.add(Integer.valueOf(s)); //returns Integer object - can use Object in Table class instead of custom generic type
            }
        }
    }

    //helper method to select table from tables arraylist based on name
    public Table tableWithName(String nameInput) {
        for (Table t : tables) {
            if t.name.isEqual(nameInput) {
                return t;
            }
        }
        return null;
    }

}
