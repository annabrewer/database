package db;

/**
 * Created by Thaniel on 2/27/2017.
 */

// Interface for doing operations on Value objects
public interface ValueOperator<T> {

    // Apply some arbitrary operation on two values
    Value apply(Value<T> v1, Value<T> v2);

    /* Determines what the type of a new value will be based on what
       is supplied into the methods:
       both ints -> int
       both floats -> float
       one is a float and one is an int -> float
       both are strings -> string
       Incorrect arguments like providing a string and a float are handled
       during parsing.
     */
    default DataType getResultingType(DataType t1, DataType t2) {
        DataType newType;

        DataType Int = DataType.INT;
        DataType Float = DataType.FLOAT;

        if ((t1.equals(Int) && t2.equals(Float)) ||
                (t1.equals(Float) && t2.equals(Int)) ||
                (t1.equals(Float) && t2.equals(Float))) {
            newType = DataType.FLOAT;
        } else if (t1.equals(Int) && t2.equals(Int)) {
            newType = DataType.INT;
        } else {
            newType = DataType.STRING;
        }
        return newType;
    }
}
