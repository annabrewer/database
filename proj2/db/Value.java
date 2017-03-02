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
            return round(aFloat).toString();
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

    /* if compared to a NOVALUE, returns a random positive int
     * only values of the same type can be compared
     * anything compared to a NaN that isn't a NaN is always
     * smaller than the NaN
     * incorrect comparisons across different data types is
     * handled during parsing
     */
    @Override
    public int compareTo(Value v) {
        DataType t = v.type;

        if (t == DataType.FLOAT) {
            return Float.compare(aFloat, v.getFloat());
        } else if (t == DataType.INT) {
            return Integer.compare(integer, v.getInteger());
        } else if (t == DataType.STRING) {
            return string.compareTo(v.getString());
        } else if (t == DataType.NaN) {
            if (type == t) {
                return 0;
            }
            return 1;
        } else {
            return 69; // :^)
        }
    }

    // Various comparing methods
    public boolean equals(Value v) {
        return v.compareTo(v) == 0;
    }

    public boolean greaterThan(Value v) {
        if (v.type == DataType.NOVALUE || type == DataType.NOVALUE) {
            return false;
        } else {
            return compareTo(v) > 0;
        }
    }

    public boolean lessThan(Value v) {
        if (v.type == DataType.NOVALUE || type == DataType.NOVALUE) {
            return false;
        } else {
            return compareTo(v) < 0;
         }
    }

    public static void main(String[] args) {
        Value x = new Value(1.0f);
        Value y = new Value(69);
        ValueOperation add = new Add();
        Value z = add.apply(x, y);
        System.out.println(z);
    }
}
