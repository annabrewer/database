package db;

/**
 * Created by Thaniel on 2/27/2017.
 */

// Interface that applies operations onto Value objects
public abstract class ValueOperation extends NumberFunction {

    private DataType Int = DataType.INT;
    private DataType Float = DataType.FLOAT;
    private DataType String = DataType.STRING;
    private DataType NaN = DataType.NaN;
    private DataType NoValue = DataType.NOVALUE;

    // Apply some arbitrary operation on two values
    public Value apply(Value v1, Value v2) {
        DataType t1 = v1.getType();
        DataType t2 = v2.getType();
        DataType result = getResultingType(t1, t2);

        if (result == Float) {
            float item = (float) func(v1.getNum(), v2.getNum());
            return new Value(item);
        } else if (result == Int) {
            int item;
            if (t1 == NoValue) {
                item = (int) func(0, v2.getNum());
            } else if (t2 == NoValue) {
                item = (int) func(v1.getNum(), 0);
            } else {
                item = (int) func(v1.getNum(), v2.getNum());
            }
            return new Value(item);
        } else if (result == String) {
            String item = v1.getString() + v2.getString();
            return new Value(item);
        } else {
            return specialValues(v1, v2);
        }
    }

    // Special handling for values that are NaN or NOVALUE
    Value specialValues(Value v1, Value v2) {
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
        if (t1 == NaN || t2 == NaN) {
            // Operations with NaN result in NaN
            return NaN;
        } else if (t1 == Float || t2 == Float) {
            // Operations with at least one float results in a float
            return Float;
        } else if (t1 == Int && t2 == Int) {
            // Operations with two ints result in an int
            return Int;
        } else if (t1 == String && t2 == String) {
            return String;
        } else if (t1 == NoValue && t2 != NoValue) {
            return t2;
        } else if (t1 != NoValue && t2 == NoValue){
            return t1;
        } else {
            return NoValue;
        }
    }
}
