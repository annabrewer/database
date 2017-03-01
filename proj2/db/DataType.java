package db;

/**
 * Created by Thaniel on 2/27/2017.
 */
// The data types we can put into our database
public enum DataType {

    INT, FLOAT, STRING, NaN, NOVALUE;

    public boolean equals(DataType t) {
        if (t == NOVALUE || this == NOVALUE) {
            return false;
        }
        return t == this;
    }
}
