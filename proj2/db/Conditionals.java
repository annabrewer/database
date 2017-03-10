package db;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Thaniel on 3/3/2017.
 */
public enum Conditionals implements ColumnConditional {

    // If a value isn't less than the one given, it isn't returned
    LESS_THAN {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            return apply(c, v, ValueComparatorFunction.LESS_THAN);
        }

        @Override
        public LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2) {
            return applyTwoColumns(c1, c2, ValueComparatorFunction.LESS_THAN);
        }
    },

    // Returns a list of values from column c that are <= v
    LESS_OR_EQUAL_TO {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            return apply(c, v, ValueComparatorFunction.LESS_OR_EQUAL_TO);
        }

        @Override
        public LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2) {
            return applyTwoColumns(c1, c2, ValueComparatorFunction.LESS_OR_EQUAL_TO);
        }
    },

    // Returns a list of values from column c that are == v
    EQUALS {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            return apply(c, v, ValueComparatorFunction.EQUALS);
        }

        @Override
        public LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2) {
            return applyTwoColumns(c1, c2, ValueComparatorFunction.EQUALS);
        }
    },

    // Applies > some value to a column
    GREATER_THAN {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            return apply(c, v, ValueComparatorFunction.GREATER_THAN);
        }

        @Override
        public LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2) {
            return applyTwoColumns(c1, c2, ValueComparatorFunction.GREATER_THAN);
        }
    },

    // Returns a list of values in column c that are >= v
    GREATER_OR_EQUAL_TO {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            return apply(c, v, ValueComparatorFunction.GREATER_OR_EQUAL_TO);
        }

        @Override
        public LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2) {
            return applyTwoColumns(c1, c2, ValueComparatorFunction.GREATER_OR_EQUAL_TO);
        }
    },

    NOT_EQUAL_TO {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            return apply(c, v, ValueComparatorFunction.NOT_EQUAL_TO);
        }

        @Override
        public LinkedHashMap<String, ArrayList<Value>> applyTwoColumns(Column c1, Column c2) {
            return applyTwoColumns(c1, c2, ValueComparatorFunction.NOT_EQUAL_TO);
        }
    }
}
