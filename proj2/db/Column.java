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

    // Create a column from a list of values
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
        ArrayList<Value> numbers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            numbers.add(new Value(i));
        }
        Column c = new Column("numbers", numbers);
        ArrayList<Value> filtered = new LessThan().apply(c, new Value(1.5f));
        System.out.println(filtered);
    }
}
