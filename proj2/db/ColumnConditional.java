package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/2/2017.
 */

/* Interface defining common behavior shared among different
 * conditional operations applied onto columns
 */
public interface ColumnConditional {

    ArrayList<Value> apply(Column c, Value v);
    ArrayList<Value> applyTwoColumns(Column c1, Column c2);

    /* Applies some condition statement to a column involving some value.
     * Should return an ArrayList of values that return true for the
     * condition statement.
     */
    default ArrayList<Value> apply(Column c, Value v, ValueComparator cond) {
        ArrayList<Value> filteredValues = new ArrayList<>();

        for (Value val : c.getValues()) {
            if (cond.apply(val, v)) {
                filteredValues.add(val);
            }
        }

        return filteredValues;
    }

    /* Applies some condition statement to two columns. The two values in
     * the same rows are compared. Returns an ArrayList of values that return
     * true for the condition statement.
     */
    default ArrayList<Value> applyTwoColumns(Column c1, Column c2, ValueComparator cond) {
        ArrayList<Value> filteredValues = new ArrayList<>();
        ArrayList<Value> vals1 = c1.getValues();
        ArrayList<Value> vals2 = c2.getValues();

        for (int i = 0; i < vals1.size(); i++) {
            Value v1 = vals1.get(i);
            Value v2 = vals2.get(i);

            if (cond.apply(v1, v2)) {
                filteredValues.add(v1);
            }
        }

        return filteredValues;
    }
}
