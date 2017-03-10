package db;

import java.math.BigDecimal;

/**
 * Created by Thaniel on 2/27/2017.
 */

// The values we can put in as data in our table
public class Value implements Comparable<Value>{

    /* The type of data stored in this Value:
       INT, FLOAT, STRING, NaN, or NOVALUE
     */
    private DataType type;
    /* The class the item stored in this Value
       belongs to: Integer, Float, or String
     */
    private Class itemClass;
    /* The various values that can be stored in
       this Value instance.
     */
    private int integer;
    private float aFloat;
    private String string;

    // Create a value that stores an integer
    public Value(int i) {
        integer = i;
        itemClass = Integer.class;
        type = DataType.INT;
    }

    // Create a value that stores a float
    public Value(float f) {
        aFloat = f;
        itemClass = Float.class;
        type = DataType.FLOAT;
    }

    // Create a value that stores a String
    public Value(String s) {
        string = s;
        itemClass = String.class;
        type = DataType.STRING;
    }

    /* Constructor used to create special values.
     * If NOVALUE, the data is treated as a zero value:
     * String -> '' (empty string)
     * int -> 0
     * float -> 0.0
     * The class of data the value can hold is still
     * maintained.
     */
    public Value(DataType t, Class c) {
        type = t;
        itemClass = c;
        if (itemClass == String.class) {
            string = "";
        }
    }

    // Change this value to the one given
    /*public void changeValue(Value v) {
        type = v.getType();
        itemClass = v.getItemClass();
        if (itemClass == Integer.class) {
            integer = v.getInteger();
        } else if (itemClass == Float.class) {
            aFloat = v.getFloat();
        } else {
            string = v.getString();
        }
    }*/

    private int getInteger() {
        return integer;
    }

    private float getFloat() {
        return aFloat;
    }

    public Number getNum() {
        if (itemClass == Integer.class) {
            return integer;
        } else {
            return aFloat;
        }
    }

    public String getString() {
        return string;
    }

    public Class getItemClass() {
        return itemClass;
    }

    public DataType getType() {
        return type;
    }

    // Helper method to format floats for printing to 3 decimal places
    private static BigDecimal round(float f) {
        BigDecimal rounded = new BigDecimal(Float.toString(f));
        rounded = rounded.setScale(3, BigDecimal.ROUND_HALF_UP);
        return rounded;
    }


    @Override
    public String toString() {
        if (type == DataType.FLOAT) {
            return String.format("%.3f", aFloat);
        } else if (type == DataType.INT) {
            return Integer.toString(integer);
        } else if (type == DataType.STRING) {
            return string;
        } else if (type == DataType.NaN) {
            return "NaN";
        } else {
            return "NOVALUE";
        }
    }

    /* if compared to a NOVALUE, returns a random negative int
     * only values of the same type can be compared
     * anything compared to a NaN that isn't a NaN is always
     * smaller than the NaN
     * incorrect comparisons across numbers and strings is
     * handled during parsing.
     */

    @Override
    public int compareTo(Value v) {
        DataType t = v.getType();

        if (t == DataType.NOVALUE || type == DataType.NOVALUE) {
            return -69;
        } else if (type == DataType.NaN) {
            if (t == type) {
                return 0;
            } else {
                return 1;
            }
        } else if (t == DataType.NaN && type != DataType.STRING) {
            return -1;
        } else if (type == DataType.FLOAT && t == DataType.INT) {
            return compareFloatAndInt(aFloat, v.getInteger());
        } else if (type == DataType.INT && t == DataType.FLOAT) {
            return compareIntAndFloat(integer, v.getFloat());
        } else if (type == DataType.INT && t == type) {
            return Integer.compare(integer, v.getInteger());
        } else if (type == DataType.FLOAT && t == type) {
            return Float.compare(aFloat, v.getFloat());
        } else {
            return string.compareTo(v.getString());
        }
    }

    // Directly compare ints and floats
    private int compareIntAndFloat(int x, float y) {
        if (x < y) {
            return -1;
        } else if (x == y) {
            return 0;
        } else {
            return 1;
        }
    }

    private int compareFloatAndInt(float x, int y) {
        if (x < y) {
            return -1;
        } else if (x == y) {
            return 0;
        } else {
            return 1;
        }
    }

    /* Various comparing methods */

    public boolean equals(Value v) {
        int result = compareTo(v);
        return result == 0;
    }

    public boolean greaterThan(Value v) {
        int result = compareTo(v);
        return result > 0;
    }

    public boolean lessThan(Value v) {
        int result = compareTo(v);
        return result < 0 && result != -69;
    }

    public static void main(String[] args) {
        Value v1 = new Value(DataType.NOVALUE, Integer.class);
        Value v2 = new Value(94.258f);
        System.out.println(v1.equals(v2));
        System.out.println(new Value(1.4555435f));

    }
}
