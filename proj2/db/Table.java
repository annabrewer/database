package db;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anna on 2/28/17.
 */
public class Table {

    String stringRep;
    HashMap<String, Column> cols = new HashMap<String, db.Column>(); //are Column and Row the class names??
    ArrayList<Row> rows = new ArrayList<Row>(); //need to instantiate here?

    //new table: takes in list of column names
    public Table (String[] columnNames) {

    }

    //load: takes in list of rows
    public Table (ArrayList<Row> rowsInput) {

    }

    public void insert(Row r) {

    }

    public static void join() {

    }

    //helper
    public void updateStringRep() {

    }

}
