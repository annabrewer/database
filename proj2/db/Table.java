package db;
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
    private LinkedHashMap<String, Class> columnTypes = new LinkedHashMap<>();

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

    /* Insert a list of column into a table. */
    public void insertValues(ArrayList<Column> cols) {
        int numRows = cols.get(0).getNumRows();
        for (int i = 0; i < numRows; i++) {
            Row r = new Row(cols, i);
            insertValues(r);
        }
    }

    // Checks to see if this table has the specified column
    public boolean containsColumn(String col) {
        return columnNames.contains(col);
    }

    /* Return a new table containing only the columns provided.
     * Assumes the provided columns are in this table. Should
     * handle incorrect table names during parsing.
     */
    Table select(ArrayList<String> selectedColumns, String n) {
        LinkedHashMap<String, Class> colInfo = new LinkedHashMap<>();
        for (String col : selectedColumns) {
            colInfo.put(col, columnTypes.get(col));
        }

        Table newTable = new Table(n, colInfo);
        for (Row r : rows) {
            newTable.insertValues(r.withColumns(selectedColumns));
        }
        return newTable;
    }

    /* Return a new table containing only the rows where the values in
     * the column return true for the conditional statement applied with
     * the inputted value.
     */
    Table select(String selectedColumn, Conditionals where, Value v, String n) {
        Table newTable = new Table(n, columnTypes);
        ArrayList<Value> filteredValues = where.apply(columns.get(selectedColumn), v);
        for (Row r : rows) {
            if (filteredValues.contains(r.getValueIn(selectedColumn))) {
                newTable.insertValues(r);
            }
        }
        return newTable;
    }

    /* Returns a new table containing only the rows where the values in
     * the same rows of the two provided columns return true for the
     * conditional statement given.
     */
    Table select(String col1, Conditionals where, String col2, String n) {
        Table newTable = new Table(n, columnTypes);

        Column c1 = columns.get(col1);
        Column c2 = columns.get(col2);
        LinkedHashMap<String, ArrayList<Value>> filteredColumns = where.applyTwoColumns(c1, c2);

        if (filteredColumns.size() == 0) {
            return newTable;
        }

        ArrayList<Value> filteredColumn1 = filteredColumns.get(col1);
        ArrayList<Value> filteredColumn2 = filteredColumns.get(col2);
        for (Row r : rows) {
            Value v1 = r.getValueIn(col1);
            Value v2 = r.getValueIn(col2);
            if (filteredColumn1.contains(v1) && filteredColumn2.contains(v2)) {
                newTable.insertValues(r);
            }
        }
        return newTable;
    }

    /* Return a new table containing the provided column with the given
     * arithmetic operation applied to each of its values with the given
     * value.
     */
    Table select(String col, Arithmetic op, Value v, String n, String colName) {
        Column c = op.apply(columns.get(col), v, colName);
        ArrayList<Column> newCol = new ArrayList<>();
        newCol.add(c);
        LinkedHashMap<String, Class> colInfo = new LinkedHashMap<>();
        colInfo.put(colName, c.getColumnType());

        Table newTable = new Table(n, colInfo);
        newTable.insertValues(newCol);
        return newTable;
    }

    /* Returns a new table containing a column with the values resulting from
     * applying the given arithmetic operation onto the values of col1 and col2
     * applied row-wise.
     */
    Table select(String col1, String col2, Arithmetic op, String n, String colName) {
        Column newCol = op.apply(columns.get(col1), columns.get(col2), colName);
        ArrayList<Column> col = new ArrayList<>();
        col.add(newCol);
        LinkedHashMap<String, Class> colInfo = new LinkedHashMap<>();
        colInfo.put(colName, newCol.getColumnType());

        Table newTable = new Table(n, colInfo);
        newTable.insertValues(col);
        return newTable;
    }

    static Table copyTable(Table tbl, String n) {
        Table newTable =  new Table(n, tbl.columnTypes);

        for (Row r : tbl.getRows()) {
            newTable.insertValues(r);
        }

        return newTable;
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
            table.append(col.getNameWithType());

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

    public static void main(String[] args) {
        LinkedHashMap<String, Class> cols = new LinkedHashMap<>();
        cols.put("x", Integer.class);
        cols.put("y", Integer.class);
        ArrayList<String> names = new ArrayList<>();
        names.add("x");
        names.add("y");
        ArrayList<Value> v1 = new ArrayList<>();
        ArrayList<Value> v2 = new ArrayList<>();
        v1.add(new Value(1));
        v1.add(new Value(2));
        v2.add(new Value(3));
        v2.add(new Value(4));
        ArrayList<Column> c = new ArrayList<>();
        Column c1 = new Column("x", v1);
        Column c2 = new Column("y", v2);
        c.add(c1);
        c.add(c2);

        Table t = new Table("test", cols);
        t.insertValues(c);
        t.print();

        LinkedHashMap<String, Class> other = new LinkedHashMap<>();
        other.put("z", Integer.class);
        other.put("y", Integer.class);
        ArrayList<String> othernames = new ArrayList<>();
        othernames.addAll(other.keySet());
        ArrayList<Value> othervalues1 = new ArrayList<>();
        ArrayList<Value> othervalues2 = new ArrayList<>();
        othervalues1.add(new Value(1));
        othervalues1.add(new Value(2));
        othervalues2.add(new Value(2));
        othervalues2.add(new Value(1));
        ArrayList<Column> othercolumns = new ArrayList<>();
        othercolumns.add(new Column("z", othervalues1));
        othercolumns.add(new Column("y", othervalues2));

        Table x = new Table("other", other);
        x.insertValues(othercolumns);
        x.print();

        Table m = Table.join(t, x, "merge");
        m.print();


    }
}
