package db;

/**
 * Created by Anna on 3/2/17.
 */
public class RandomTests {
    public static void main(String[] args) {
        String[] parseArgs = {"select testerino from tablerino, table2 where cond"};
        db.Parser.main(parseArgs);
    }
}
