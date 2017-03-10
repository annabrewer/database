package db;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Thaniel on 3/2/2017.
 */

/* Interface that represents the general behavior of functions
 * applied onto column objects with an inputted value or column.
 */
public interface ColumnFunction {

    Column apply(Column c1, Column c2, String n);
    Column apply(Column c, Value v, String n);

    /* Applies some value function onto each value in a column.
     * Returns a new column with a new name.
     */
    default Column apply(ValueOperation op, Column c, Value v, String n) {
        ArrayList<Value> values = c.getValues();
        ArrayList<Value> newValues = new ArrayList<>();
        Class type = getResultingType(c, v);

        for (Value val : values) {
            Value newValue = op.apply(val, v);
            newValues.add(newValue);
        }

        Column newColumn = new Column(n, type);
        newColumn.addValues(newValues);
        return newColumn;
    }

    /* Applies a value operation using values from two columns.
     * Each resulting value is inputted into a new column.
     * This assumes both columns are the same length.
     */
    default Column applyTwoColumns(ValueOperation op, Column c1, Column c2, String n) {
        Column newColumn = new  Column(n, getResultingType(c1, c2));
        ArrayList<Value> values1 = c1.getValues();
        ArrayList<Value> values2 = c2.getValues();

        Iterator<Value> i1 = values1.iterator();
        Iterator<Value> i2 = values2.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            Value newValue = op.apply(i1.next(), i2.next());
            newColumn.addValue(newValue);
        }

        return newColumn;
    }

    /* Operations involving:
     *   At least one float -> column of float values
     *   Two ints -> column of ints
     *   Two Strings -> column of strings
     */
    default Class getResultingType(Column c1, Column c2) {
        Class t1 = c1.getColumnType();
        Class t2 = c2.getColumnType();

        if (t1 == Float.class || t2 == Float.class) {
            return Float.class;
        } else if (t1 == Integer.class && t2 == Integer.class) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

    default Class getResultingType(Column c, Value v) {
        Class t1 = c.getColumnType();
        Class t2 = v.getItemClass();

        if (t1 == Float.class || t2 == Float.class) {
            return Float.class;
        } else if (t1 == Integer.class && t2 == Integer.class) {
            return Integer.class;
        } else {
            return String.class;
        }
    }


}
