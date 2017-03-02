package db;

/**
 * Created by Thaniel on 3/1/2017.
 */
public class Divide extends ValueOperation {

    @Override
    public Value apply(Value v1, Value v2) {
        /*try {
            return super.apply(v1, v2);
        } catch (ArithmeticException zeroDivision) {
            return specialValues(v1, v2);
        }*/
        if (v2.getType() == DataType.NOVALUE) {
            Class type;
            if (v1.getItemClass() == Float.class || v2.getItemClass() == Float.class) {
                type = Float.class;
            } else {
                type = Integer.class;
            }
            return new Value(DataType.NaN, type);
        } else {
            return super.apply(v1, v2);
        }
    }
    @Override
    public int twoInts(int x, int y) {
        return x / y;
    }

    @Override
    public float twoFloats(float x, float y) {
        return x / y;
    }

    @Override
    public float intAndFloat(int x, float y) {
        return x / y;
    }

    @Override
    public float floatAndInt(float x, int y) {
        return x / y;
    }
}
