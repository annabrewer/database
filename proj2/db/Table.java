package db;
import org.junit.Test;

import java.util.*;

/**
 * Created by Anna on 2/28/17.
 */
public class Table {

    // This table's name
    private String name;

    // Maps each column name to it's corresponding column object
    private LinkedHashMap<String, Column> columns = new LinkedHashMap<>();

    // Maps each column name to it's corresponding type
    private LinkedHashMap<String, Class> columnTypes;

    // List of the column names in the order they appear
    private ArrayList<String> columnNames = new ArrayList<>();

    // ArrayList of rows
    private ArrayList<Row> rows = new ArrayList<>();

    /* Only empty tables are initialized
     * Incorrect constructor arguments are handled during parsing.
     */
    public Table(String nameInput, LinkedHashMap<String, Class> columnsInput) {
        columnTypes = columnsInput;
        name = nameInput;
        for (String col : columnsInput.keySet()) {
            columns.put(col, new Column(col, columnsInput.get(col)));
            columnNames.add(col);
        }

    }

    /* Various getter methods. */

    public String getName() {
        return name;
    }

    public LinkedHashMap<String, Column> getColumns() {
        return columns;
    }

    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    public LinkedHashMap<String, Class> getColumnTypes() {
        return columnTypes;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    /* Insert a row into the table. The table is built
     * row-wise.
     */
    public void insertValues(Row r) {
        rows.add(r);

        for (String name : columnNames) {
            Column col = columns.get(name);
            col.addValue(r.getValueIn(name));
        }
    }


    /* Joins the inputted tables by looking at their columns.
     * If no columns are shared, the rows are merged as the
     * result of a cartesian product. If a column is shared
     * but contains no shared values, an empty table is returned.
     */
    public static Table join(Table t1, Table t2, String n) {
        ArrayList<String> sharedCols = sharedColumns(t1, t2);
        if (sharedCols.size() == 0) {
            return cartesianProduct(t1, t2);
        }

        LinkedHashMap<String, Class> newColumns = newColumnHelper(t1, t2);
        Table joinedTable = new Table(n, newColumns);
        for (Row r1 : t1.rows) {
            for (Row r2 : t2.rows) {
                Row mergedRow = Row.merge(r1, r2, sharedCols);
                if (!mergedRow.isEmpty()) {
                    joinedTable.insertValues(mergedRow);
                }
            }
        }

        return joinedTable;
    }

    /* Finds the names of the shared columns in the two inputted tables.
     * Returns an empty list if there are no shared columns.
     */
    private static ArrayList<String> sharedColumns(Table t1, Table t2) {
        ArrayList<String> otherCols = t2.columnNames;
        ArrayList<String> shared = new ArrayList<>();

        for (String col1 : t1.columnNames) {
            Class c1 = t1.columnTypes.get(col1);

            for (String col2 : otherCols) {
                Class c2 = t2.columnTypes.get(col2);

                if (col1.equals(col2) && c1.equals(c2)) {
                    shared.add(col1);
                }
            }
        }

        return shared;
    }

    /* Merges the rows of the two tables through a cartesian product
     * if they have no shared columns. This method is only called when
     * this is the case.
     */
    private static Table cartesianProduct(Table t1, Table t2) {
        LinkedHashMap<String, Class> newColumnsInfo = newColumnHelper(t1, t2);

        Table newTable = new Table("Temp", newColumnsInfo);
        for (Row r1 : t1.rows) {
            for (Row r2 : t2.rows) {
                Row joined = Row.joinRow(r1, r2);
                newTable.insertValues(joined);
            }
        }

        return newTable;

    }

    private static LinkedHashMap<String, Class> newColumnHelper(Table t1, Table t2) {
        ArrayList<String> shared = sharedColumns(t1, t2);
        LinkedHashMap<String, Class> columnsInfo = new LinkedHashMap<>();

        if (shared.size() > 0) {
            for (String col : shared) {
                columnsInfo.put(col, t1.columnTypes.get(col));
            }
        }

        for (String col : t1.columnNames) {
            if (!shared.contains(col)) {
                columnsInfo.put(col, t1.columnTypes.get(col));
            }
        }
        for (String col : t2.columnNames) {
            if (!shared.contains(col)) {
                columnsInfo.put(col, t2.columnTypes.get(col));
            }
        }

        return columnsInfo;
    }

    @Override
    public String toString() {
        StringBuilder table = new StringBuilder();

        Iterator<String> iterateNames = columnNames.iterator();
        while (iterateNames.hasNext()) {
            String name = iterateNames.next();
            Column col = columns.get(name);
            table.append(col.getColumnName());

            if (iterateNames.hasNext()) {
                table.append(",");
            }
        }
        table.append("\r\n");

        for (Row r : rows) {
            table.append(r.toString());
            table.append("\r\n");
        }

        return table.toString();
    }

    public void print() {
        System.out.println(toString());
    }

    /*private ArrayList<String> sharedColumns(Table t) {

    }*/
    public static void main(String[] args) {
        ArrayList<String> n1 = new ArrayList<>();
        Collections.addAll(n1, "x", "y");
        LinkedHashMap<String, Class> colInfo1 = new LinkedHashMap<>();
        for (String n : n1) {
            colInfo1.put(n, Integer.class);
        }
        ArrayList<Value> v1 = new ArrayList<>();
        ArrayList<Value> v2 = new ArrayList<>();
        ArrayList<Value> v3 = new ArrayList<>();
        Collections.addAll(v1, new Value(1), new Value(7));
        Collections.addAll(v2, new Value(7), new Value(7));
        Collections.addAll(v3, new Value(1), new Value(9));

        Table t1 = new Table("test1", colInfo1);
        t1.insertValues(new Row(n1, v1));
        t1.insertValues(new Row(n1, v2));
        t1.insertValues(new Row(n1, v3));
        t1.print();

        ArrayList<String> n2 = new ArrayList<>();
        Collections.addAll(n2, "a", "b");
        LinkedHashMap<String, Class> colInfo2 = new LinkedHashMap<>();
        for (String n : n2) {
            colInfo2.put(n, Integer.class);
        }
        ArrayList<Value> val1 = new ArrayList<>();
        ArrayList<Value> val2 = new ArrayList<>();
        ArrayList<Value> val3 = new ArrayList<>();
        //ArrayList<Value> val4 = new ArrayList<>();
        Collections.addAll(val1, new Value(3), new Value(8));
        Collections.addAll(val2, new Value(4), new Value(9));
        Collections.addAll(val3, new Value(5), new Value(10));
        //Collections.addAll(val4, new Value(1), new Value(11), new Value(9));
        Table t2 = new Table("test2", colInfo2);
        t2.insertValues(new Row(n2, val1));
        t2.insertValues(new Row(n2, val2));
        t2.insertValues(new Row(n2, val3));
        //t2.insertValues(new Row(n2, val4));
        t2.print();

        Table t3 = Table.join(t1, t2, "result");
        t3.print();
    }


}
