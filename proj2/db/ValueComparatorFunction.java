package db;

/**
 * Created by Thaniel on 3/6/2017.
 */
public enum ValueComparatorFunction implements ValueComparator {

    LESS_THAN {
        @Override
        public boolean apply(Value v1, Value v2) {
            return v1.lessThan(v2);
        }
    },

    LESS_OR_EQUAL_TO {
        @Override
        public boolean apply(Value v1, Value v2) {
            return v1.lessThan(v2) || v1.equals(v2);
        }
    },

    EQUALS {
        @Override
        public boolean apply(Value v1, Value v2) {
            return v1.equals(v2);
        }
    },

    GREATER_THAN {
        @Override
        public boolean apply(Value v1, Value v2) {
            return v1.greaterThan(v2);
        }
    },

    GREATER_OR_EQUAL_TO {
        @Override
        public boolean apply(Value v1, Value v2) {
            return v1.greaterThan(v2) || v1.equals(v2);
        }
    }
}
