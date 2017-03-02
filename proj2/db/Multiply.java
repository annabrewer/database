package db;

/**
 * Created by Thaniel on 3/1/2017.
 */
public class Multiply extends ValueOperation {
    @Override
    public int twoInts(int x, int y) {
        return x * y;
    }

    @Override
    public float twoFloats(float x, float y) {
        return x * y;
    }

    @Override
    public float intAndFloat(int x, float y) {
        return x * y;
    }

    @Override
    public float floatAndInt(float x, int y) {
        return x * y;
    }
}
