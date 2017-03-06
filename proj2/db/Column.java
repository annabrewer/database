package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/2/2017.
 */
// Column of values we can input into our table
public class Column {

    private ArrayList<Value> values = new ArrayList<>();
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
        columnType = vals.get(0).getItemClass();
        name = n;
    }

    /* Add a value to a column. This is never called directly,
     *  since tables are constructed row-wise.
     */
    public void addValue(Value v) {
        values.add(v);
    }

    /* Add values into this column from a list of rows.
     * Assumes the list of rows has this column's name.
     */
    public void addFromRows(ArrayList<Row> rows) {
        values = new ArrayList<>();

        for (Row r : rows) {
            values.add(r.getValueIn(name));
        }
    }

    /* Various getter methods */

    public ArrayList<Value> getValues() {
        return values;
    }

    // Returns the value in a column that's in the specified row number
    public Value getValueInRow(int row) {
        return values.get(row);
    }

    public Class getColumnType() {
        return columnType;
    }

    public String getColumnName() {
        if (columnType == Integer.class) {
            name = name + " " + "int";
        } else if (columnType == Float.class) {
            name = name + " " + "float";
        } else {
            name = name + " " + "string";
        }
        return name;
    }

    public static void main(String[] args) {
        Column c = new Column("x", Integer.class);
        System.out.print(c.getColumnName());
    }
}
