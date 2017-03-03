package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/3/2017.
 */

// Applies <= some value to a column
public class LessOrEqualTo implements ColumnConditional {

    // Returns a list of values from column c that are <= v
    @Override
    public ArrayList<Value> apply(Column c, Value v) {
        ArrayList<Value> filteredValues = new ArrayList<>();

        for (Value val : c.getValues()) {
            if (val.lessThan(v) || val.equals(v)) {
                filteredValues.add(val);
            }
        }

        return filteredValues;
    }
}
