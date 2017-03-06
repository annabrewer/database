package db;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Thaniel on 2/28/2017.
 */

/* Defines the specific behavior of performing operations on columns
 * with a given value, or performing operations using two columns.
 */

public enum Arithmetic implements ColumnFunction {

    ADD {
        @Override
        public Column apply(Column c1, Column c2, String n) {
            return applyTwoColumns(new Add(), c1, c2, n);
        }

        @Override
        public Column apply(Column c, Value v, String n) {
            return apply(new Add(), c, v, n);
        }


    },
    SUBTRACT {
        @Override
        public Column apply(Column c1, Column c2, String n) {
            return applyTwoColumns(new Subtract(), c1, c2, n);
        }

        @Override
        public Column apply(Column c, Value v, String n) {
            return apply(new Subtract(), c, v, n);
        }
    },
    MULTIPLY {
        @Override
        public Column apply(Column c1, Column c2, String n) {
            return applyTwoColumns(new Multiply(), c1, c2, n);
        }

        @Override
        public Column apply(Column c, Value v, String n) {
            return apply(new Multiply(), c, v, n);
        }
    },
    DIVIDE {
        @Override
        public Column apply(Column c1, Column c2, String n) {
            return applyTwoColumns(new Divide(), c1, c2, n);
        }

        @Override
        public Column apply(Column c, Value v, String n) {
            return apply(new Divide(), c, v, n);
        }
    };

    public static void main(String[] args) {
        ArrayList<Value> v1 = new ArrayList<>();
        Collections.addAll(v1, new Value(DataType.NaN, Float.class), new Value(7.802f), new Value(4.956f));
        ArrayList<Value> v2 = new ArrayList<>();
        Collections.addAll(v2, new Value(DataType.NOVALUE, Float.class), new Value(6.741f), new Value(DataType.NOVALUE, Float.class));

        Column c1 = new Column("t1", v1);
        Column c2 = new Column("t2", v2);

        Column c3 = Arithmetic.SUBTRACT.apply(c1, c2, "t3");
        for (Value v : c3.getValues()) {
            System.out.print(v.toString() + " ");
        }
    }

}
