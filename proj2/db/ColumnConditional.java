package db;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Thaniel on 3/2/2017.
 */

/* Interface defining common behavior shared among different
 * conditional operations applied onto columns
 */
public interface ColumnConditional {

    ArrayList<Value> apply(Column c, Value v);
    LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2);

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
     * the same rows are compared. Returns a Map of column name to the
     * values in that column that return true for the condition statement.
     */
    default LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2, ValueComparator cond) {
        LinkedHashMap<String, ArrayList<Value>> filteredValues = new LinkedHashMap<>();
        filteredValues.put(c1.getName(), new ArrayList<>());
        filteredValues.put(c2.getName(), new ArrayList<>());
        ArrayList<Value> vals1 = c1.getValues();
        ArrayList<Value> vals2 = c2.getValues();

        for (int i = 0; i < vals1.size(); i++) {
            Value v1 = vals1.get(i);
            Value v2 = vals2.get(i);
            ArrayList<Value> filteredVals1 = filteredValues.get(c1.getName());
            ArrayList<Value> filteredVals2 = filteredValues.get(c2.getName());
            if (cond.apply(v1, v2) || (!filteredVals1.contains(v1) && !filteredVals2.contains(v2))) {
                filteredVals1.add(v1);
                filteredVals2.add(v2);
            }
        }

        return filteredValues;
    }
}
