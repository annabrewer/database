package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/2/2017.
 */
// Column of values we can input into our table
public class Column {

    private ArrayList<Value> values;
    private String name;
    private Class columnType;

    // Create an empty column
    public Column(String n, Class t) {
        name = n;
        columnType = t;
    }

    // Create a column from a list of values. Inputted list shouldn't be empty.
    public Column(String n, ArrayList<Value> vals) {
        values = vals;
        name = n;
        columnType = vals.get(0).getItemClass();
    }

    /* Add a value to a column. This is never called directly,
     * it should always be called by the row class since tables
     * are constructed row-wise.
     */
    public void add(Value v) {
        values.add(v);
    }

    /* Various getter methods */

    public ArrayList<Value> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public Class getColumnType() {
        return columnType;
    }

    public static void main(String[] args) {

    }
}
