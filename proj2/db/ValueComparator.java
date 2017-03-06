package db;

/**
 * Created by Thaniel on 3/6/2017.
 */
public interface ValueComparator {

    boolean apply(Value v1, Value v2);
}
