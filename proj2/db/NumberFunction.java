package db;

/**
 * Created by Thaniel on 3/1/2017.
 */
// A class representing functions for general number types
abstract class NumberFunction implements BasicFunction {

    Number func(Number x, Number y) {
        if (x.getClass() == Integer.class && y.getClass() == Float.class) {
            return intAndFloat((int) x, (float) y);
        } else if (x.getClass() == Float.class && y.getClass() == Integer.class) {
            return floatAndInt((float) x, (int) y);
        } else if (x.getClass() == Float.class && x.getClass() == y.getClass()) {
            return twoFloats((float) x, (int) y);
        } else {
            return twoInts((int) x, (int) y);
        }
    }

}
