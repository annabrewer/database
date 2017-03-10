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

    // Add a list of values to this column
    public void addValues(ArrayList<Value> vals) {
        for (Value v : vals) {
            addValue(v);
        }
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

    // Returns the number of rows this column has
    public int getNumRows() {
        return values.size();
    }

    // Returns true if the given value is in this column
    public boolean contains(Value v) {
        return values.contains(v);
    }

    public Class getColumnType() {
        return columnType;
    }

    public String getName() {
        return name;
    }

    public String getNameWithType() {
        StringBuilder withType = new StringBuilder(name);
        withType.append(" ");
        if (columnType == Integer.class) {
            withType.append("int");
        } else if (columnType == Float.class) {
            withType.append("float");
        } else {
            withType.append("string");
        }
        return withType.toString();
    }

    public static void main(String[] args) {
        Column c = new Column("x", Integer.class);
        System.out.print(c.getNameWithType());
    }
}
