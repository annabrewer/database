package db;

/**
 * Created by Thaniel on 2/27/2017.
 */

// The values we can put in as data in our table
public class Value<T> implements Comparable<Value>{

    private DataType type;
    private T val;

    public Value(int x) {
        val = x;
        type = DataType.INT;
    }

    public Value(float y) {
        val = y;
        type = DataType.FLOAT;
    }

    public Value(String s) {
        val = s;
        type = DataType.STRING;
    }

    /* Zero argument constructor used to create NOVALUEs
     * String -> '' (empty string)
     * int -> 0
     * float -> 0.0
     */
    public Value() {
        type = DataType.NOVALUE;
    }

    public Object getVal() {
        return val;
    }

    public DataType getType() {
        return type;
    }

    @Override
    public String toString() {
        return val.toString();
    }

    /* if compared to a NOVALUE, returns a random positive int
     * incorrect comparisons across different data types is
     * handled during parsing
     */
    @Override
    public int compareTo(Value v) {
        DataType t = v.type;

        if (t == DataType.FLOAT) {
            return Float.compare((float) val, (float) v.getVal());
        } else if (t == DataType.INT) {
            return Integer.compare((int) val, (int) v.getVal());
        } else if (t == DataType.STRING) {
            return ((String) val).compareTo((String) v.getVal());
        } else if ( t == DataType.NaN) {
            if (type == t) {
                return 0;
            }
            return 1;
        } else {
            return 69; // :^)
        }
    }

    // Checks equality by comparing the data stored in v
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
}
