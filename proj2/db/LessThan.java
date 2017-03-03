package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/3/2017.
 */

// Class for applying < some value to a column of values
public class LessThan implements ColumnConditional {

    // If a value isn't less than the one given, it isn't returned
    @Override
    public ArrayList<Value> apply(Column c, Value v) {
        ArrayList<Value> filteredValues = new ArrayList<>();

        for (Value val : c.getValues()) {
            if (val.lessThan(v)) {
                filteredValues.add(val);
            }
        }

        return filteredValues;
    }
}
