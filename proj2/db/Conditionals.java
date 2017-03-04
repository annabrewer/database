package db;

import java.util.ArrayList;

/**
 * Created by Thaniel on 3/3/2017.
 */
public enum Conditionals implements ColumnConditional {

    // If a value isn't less than the one given, it isn't returned
    LESS_THAN {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            ArrayList<Value> filteredValues = new ArrayList<>();

            for (Value val : c.getValues()) {
                if (val.lessThan(v)) {
                    filteredValues.add(val);
                }
            }

            return filteredValues;
        }
    },

    // Returns a list of values from column c that are <= v
    LESS_OR_EQUAL_TO {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            ArrayList<Value> filteredValues = new ArrayList<>();

            for (Value val : c.getValues()) {
                if (val.lessThan(v) || val.equals(v)) {
                    filteredValues.add(val);
                }
            }

            return filteredValues;
        }
    },

    // Returns a list of values from column c that are == v
    EQUALS {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            ArrayList<Value> filteredValues = new ArrayList<>();

            for (Value val : c.getValues()) {
                if (val.equals(v)) {
                    filteredValues.add(val);
                }
            }

            return filteredValues;
        }
    },

    // Applies > some value to a column
    GREATER_THAN {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            ArrayList<Value> filteredValues = new ArrayList<>();

            for (Value val : c.getValues()) {
                if (val.greaterThan(v)) {
                    filteredValues.add(val);
                }
            }

            return filteredValues;
        }
    },

    // Returns a list of values in column c that are >= v
    GREATER_OR_EQUAL_TO {
        @Override
        public ArrayList<Value> apply(Column c, Value v) {
            ArrayList<Value> filteredValues = new ArrayList<>();

            for (Value val : c.getValues()) {
                if (val.greaterThan(v) || val.equals(v)) {
                    filteredValues.add(val);
                }
            }

            return filteredValues;
        }
    }
}
