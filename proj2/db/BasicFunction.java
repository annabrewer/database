package db;

/**
 * Created by Thaniel on 3/1/2017.
 */
/* Interface for functions that have specific behavior for different
 * combinations of floats and ints
 */
public interface BasicFunction {

    int twoInts(int x, int y);
    float twoFloats(float x, float y);
    float intAndFloat(int x, float y);
    float floatAndInt(float x, int y);

}
