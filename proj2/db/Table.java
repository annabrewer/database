package db;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Anna on 2/28/17.
 */
public class Table {
    public String name;
    public String stringRep;

    public Object[][] cols;
    //an array of arrays,
    //each containing the column name at index 0
    //and an arraylist of values at index 1

    public ArrayList<ArrayList<Object>> rows;

    //create new
    public Table(String nameInput, String[] colsInput) {
        //now both are pointing to columns object
        name = nameInput;
        cols = new Object[colsInput.length][2];
        for (Object[] col : cols) {
            col[0] = colsInput[Arrays.asList(cols).indexOf(col)]; //if this is being shitty just use a regular for loop
        }
    }
    //load
    public Table(String nameInput, ArrayList<ArrayList<Object>> rowsInput) {
        //now both are pointing to columns object
        name = nameInput;
        cols = new Object[colsInput.length][2];
        for (Object[] col : cols) {
            col[0] = colsInput[Arrays.asList(cols).indexOf(col)]; //if this is being shitty just use a regular for loop
        }
    }

    public static Table join(expressionsInput, tablesInput, conditionsInput) {

    }

    public Table insert() {

    }
}
