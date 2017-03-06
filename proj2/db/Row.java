package db;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by Thaniel on 3/4/2017.
 */
// Rows of the values in our table
public class Row {

    /* List of column names in the order they
     * are in the table.
     */
    private ArrayList<String> columns;

    /* LinkedHashMap that maps column name to the
     * corresponding value in the row
     */
    private LinkedHashMap<String, Value> values = new LinkedHashMap<>();

    /* Initialize a row with a list of column
     * names and a list of values. These lists
     * will always be the same size since
     * improper arguments are handled during parsing.
     */
    public Row(ArrayList<String> columnNames, ArrayList<Value> vals) {
        columns = columnNames;

        int index = 0;
        for (String name : columnNames) {
            values.put(name, vals.get(index));
            index++;
        }
    }

    /* Initialize an empty row with a list of
     * column names.
     */
    public Row(ArrayList<String> columnNames) {
        columns = columnNames;
    }

    /* Create a row using values from a list of columns at
     * a particular index.
     */
    public Row(ArrayList<Column> cols, int index) {
        for (Column c : cols) {
            columns.add(c.getNameWithType());
            values.put(c.getNameWithType(), c.getValueInRow(index));
        }
    }

    // Various getter methods

    public ArrayList<String> getColumns() {
        return columns;
    }

    public LinkedHashMap<String, Value> getValues() {
        return values;
    }

    public ArrayList<Value> getRowValues() {
        ArrayList<Value> entries = new ArrayList<>();
        for (Value v : values.values()) {
            entries.add(v);
        }
        return entries;
    }

    // Gets the value in the row that's in the inputted column
     public Value getValueIn(String columnName) {
        return values.get(columnName);
    }

    /* Returns a new row containing values from the inputted columns
     * Assumes this row has the columns provided.
     */
    public Row withColumns(ArrayList<String> columnNames) {
        Row newRow = new Row(columnNames);
        ArrayList<Value> vals = new ArrayList<>();

        for (String column : columnNames) {
            Value v = getValueIn(column);
            vals.add(v);
        }

        newRow.insertValues(vals);
        return newRow;
    }

    // Insert a list of values into an empty row.
    public void insertValues(ArrayList<Value> vals) {
        Iterator<String> i = columns.iterator();
        for (Value v : vals) {
            values.put(i.next(), v);
        }
    }

    /* Represent this row as a comma separated list of values.
     * Used as a helper method for Table's string method
     */
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        int i = values.values().size();

        for (Value v : values.values()) {
            returnString.append(v.toString());
            i--;
            if (i > 0) {
                returnString.append(",");
            }
        }

        return returnString.toString();
    }

    boolean isEmpty() {
        return values.values().isEmpty();
    }

    /* Merges r1 with r2. Rows that share columns but don't share any
     * values in them are above the scope of this function and should be
     * dealt with in the table class.
     */
    public static Row merge(Row r1, Row r2, ArrayList<String> sharedCols) {
        ArrayList<String> cols = new ArrayList<>();
        ArrayList<Value> vals = new ArrayList<>();

        LinkedHashMap<String, Value> shared = sharedValues(r1, r2, sharedCols);

        cols.addAll(sharedCols);
        vals.addAll(shared.values());
        for (String c : r1.values.keySet()) {
            if (!cols.contains(c)) {
                cols.add(c);
                vals.add(r1.values.get(c));
            }
        }
        for (String c : r2.values.keySet()) {
            if(!cols.contains(c)) {
                cols.add(c);
                vals.add(r2.values.get(c));
            }
        }
        if (shared.size() != sharedCols.size()) {
            return new Row(cols);
        }

        return new Row(cols, vals);
    }

    /* Joins r1 with r2. This should only be called
     * if the two rows don't share any columns.
     */
    public static Row joinRow(Row r1, Row r2) {
        ArrayList<String> cols = new ArrayList<>();
        ArrayList<Value> vals = new ArrayList<>();
        cols.addAll(r1.columns);
        cols.addAll(r2.columns);
        vals.addAll(r1.getRowValues());
        vals.addAll(r2.getRowValues());

        return new Row(cols, vals);
    }

    /* If a row shares columns with another row, call this function
     * to find shared values within the shared columns. Returns a
     * map of shared column name to shared value within that column.
     */
    private static LinkedHashMap<String, Value> sharedValues(Row r1, Row r2, ArrayList<String> sharedCols) {
        LinkedHashMap<String, Value> shared = new LinkedHashMap<>();

        for (String col : sharedCols) {
            Value v = r1.getValueIn(col);
            Value other = r2.getValueIn(col);

            if (v.equals(other)) {
                shared.put(col, v);
            }
        }
        return shared;
    }

    /* Returns the names of columns this row and the given row
     * share.
     */
    private static ArrayList<String> sharedColumns(Row r1, Row r2) {
        ArrayList<String> other = r2.columns;
        ArrayList<String> shared = new ArrayList<>();

        for (String col : r1.columns) {
            if (other.contains(col)) {
                shared.add(col);
            }
        }

        return shared;
    }

    public static void main(String[] args) {

    }

}
