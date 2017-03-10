package db;

/**
 * Created by Thaniel on 2/27/2017.
 */

// Abstract class that applies operations onto Value objects
public abstract class ValueOperation extends NumberFunction {

    private DataType typeInt = DataType.INT;
    private DataType typeFloat = DataType.FLOAT;
    private DataType typeString = DataType.STRING;
    private DataType typeNaN = DataType.NaN;
    private DataType typeNoValue = DataType.NOVALUE;

    // Apply some arbitrary operation on two values
    public Value apply(Value v1, Value v2) {
        DataType t1 = v1.getType();
        DataType t2 = v2.getType();
        if (t1 == DataType.NOVALUE || t2 == DataType.NOVALUE) {
            return specialValues(v1, v2);
        }
        DataType result = getResultingType(t1, t2);

        if (result == typeFloat) {
            float item = (float) func(v1.getNum(), v2.getNum());
            return new Value(item);
        } else if (result == typeInt) {
            int item;
            if (t1 == typeNoValue) {
                item = (int) func(0, v2.getNum());
            } else if (t2 == typeNoValue) {
                item = (int) func(v1.getNum(), 0);
            } else {
                item = (int) func(v1.getNum(), v2.getNum());
            }
            return new Value(item);
        } else if (result == String) {
            StringBuilder item = new StringBuilder(v1.getString());
            item.append(v2.getString());
            return new Value(item.toString());
        } else {
            return specialValues(v1, v2);
        }
    }

    // Special handling for values that are NaN or NOVALUE
    private Value specialValues(Value v1, Value v2) {
        Class c1 = v1.getItemClass();
        Class c2 = v2.getItemClass();
        DataType result = getResultingType(v1.getType(), v2.getType());

        if (c1 == Integer.class && c2 == c1) {
            return new Value(result, Integer.class);
        } else if (c1 == Float.class || c2 == Float.class) {
            return new Value(result, Float.class);
        } else {
            return new Value(result, String.class);
        }
    }

    /* Determines what the type of a new value will be based on what
       is supplied into the methods:
       both ints -> int
       both floats -> float
       one is a float and one is an int -> float
       both are strings -> string
       Cannot perform operations on two NOVALUES
       Cannot add two NaNs together
       Incorrect arguments like providing a string and a float are handled
       during parsing, so assumes input is always correct.
     */
    private DataType getResultingType(DataType t1, DataType t2) {
        if (t1 == typeNaN || t2 == typeNaN) {
            // Operations with NaN result in NaN
            return typeNaN;
        } else if (t1 == typeFloat || t2 == typeFloat) {
            // Operations with at least one float results in a float
            return typeFloat;
        } else if (t1 == typeInt && t2 == typeInt) {
            // Operations with two ints result in an int
            return typeInt;
        } else if (t1 == String && t2 == String) {
            return String;
        } else if (t1 == typeNoValue && t2 != typeNoValue) {
            return t2;
        } else if (t1 != typeNoValue && t2 == typeNoValue) {
            return t1;
        } else {
            return typeNoValue;
        }
    }
}
