package db;

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
    }

}
