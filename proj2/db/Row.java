package db;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thaniel on 3/4/2017.
 */
// Rows of the values in our table
public class Row {

    /* List of column names in the order they
     * are in the table.
     */
    private String[] columns;
    /* HashMap that maps column name to the
     * corresponding value in the row
     */
    private HashMap<String, Value> vals = new HashMap<>();

    /* Initialize a row with a list of column
     * names and a list of values. These lists
     * will always be the same size since
     * improper arguments are handled during parsing.
     */
    public Row(String[] columnNames, ArrayList<Value> values) {
        columns = columnNames;

        int index = 0;
        for (String name : columnNames) {
            vals.put(name, values.get(index));
            index++;
        }
    }

    /* Initialize an empty row with a list of
     * column names.
     */
    public Row(String[] columnNames) {
        columns = columnNames;
    }

    /* Insert a list of values into an empty row. */
    public void insertValues(ArrayList<Value> values) {
        int index = 0;
        for (Value v : values) {
            vals.put(columns[index], v);
        }
    }

    public static Row merge(Row r1, Row r2) {

    }
}
