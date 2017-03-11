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
        DataType result = getResultingType(v1, v2);

        if (result == typeNaN) {
            return new Value(DataType.NaN, getSpecialValueClass(v1, v2));
        }
        if (result == typeFloat) {
            if (t1 == typeNoValue && t2 == typeNoValue) {
                return new Value(typeNoValue, Float.class);
            }
            float item = (float) func(v1.getNum(), v2.getNum());
            return new Value(item);
        } else if (result == typeInt) {
            if (t1 == typeNoValue && t2 == typeNoValue) {
                return new Value(typeNoValue, Float.class);
            }
            int item = (int) func(v1.getNum(), v2.getNum());
            return new Value(item);
        } else {
            String s1 = v1.getString();
            String s2 = v2.getString();
            if (v1.getType() == typeNoValue && v2.getType() == typeNoValue) {
                return new Value(DataType.NOVALUE, String.class);
            }
            if (s1.length() == 0 && s2.length() == 0) {
                return new Value("''");
            } else if (s1.length() == 0) {
                return new Value(s2);
            } else if (s2.length() == 0) {
                return new Value(s1);
            } else {
                StringBuilder item = new StringBuilder(s1.substring(0, s1.length() - 1));
                item.append(s2.substring(1));
                return new Value(item.toString());
            }
        }
    }

    /* Gets the class of an operation between values that are special
     * values.
     */
    private Class getSpecialValueClass(Value v1, Value v2) {
        Class t1 = v1.getItemClass();
        Class t2 = v2.getItemClass();
        if (t1 == Integer.class && t2 == t1) {
            return Integer.class;
        } else {
            return Float.class;
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
    private DataType getResultingType(Value v1, Value v2) {
        DataType t1 = v1.getType();
        DataType t2 = v2.getType();
        if (t1 == typeNaN || t2 == typeNaN) {
            // Operations with NaN result in NaN
            return typeNaN;
        } else if (t1 == typeFloat || t2 == typeFloat) {
            // Operations with at least one float results in a float
            return typeFloat;
        } else if (t1 == typeInt && t2 == typeInt) {
            // Operations with two ints result in an int
            return typeInt;
        } else if (t1 == typeString && t2 == typeString) {
            return typeString;
        } else {
            // If at least one of the values is NOVALUE
            Class c1 = v1.getItemClass();
            Class c2 = v2.getItemClass();
            if (c1 == Integer.class && c1 == c2) {
                return typeInt;
            } else if (c1 == Float.class || c2 == Float.class) {
                return typeFloat;
            } else {
                return typeString;
            }
        }
    }
}
