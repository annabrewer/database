package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/2/2017.
 */

/* Interface defining common behavior shared among different
 * conditional operations applied onto columns
 */
public interface ColumnConditional {

    /* Applies some condition statement to a column involving some value.
     * Should return an ArrayList of values that return true for the
     * condition statement.
     */
    ArrayList<Value> apply(Column c, Value v);


}
