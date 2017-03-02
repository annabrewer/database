package db;

/**
 * Created by Thaniel on 2/28/2017.
 */

/* Defines the different operations we can do with Value objects:
   add(supports concatenation for strings), subtract, multiply, and divide
 */

public enum ValueArithmetic implements ValueOperator{
    ADD {
        @Override
        public Value apply(Value v1, Value v2) {
            DataType t1 = v1.getType();
            DataType t2 = v2.getType();
            DataType newType = getResultingType(t1, t2);

            if (newType.equals(DataType.FLOAT)) {
                Float val = Float (v1.getVal() + v2.getVal());
                return new Value<>(val);
            } else if (newType.equals(DataType.INT)) {
                Integer val = (Integer) v1.getVal() + (Integer) v2.getVal();
                return new Value<>(val);
            } else {
                String val = v1.getVal() + (String) v2.getVal();
                return new Value<>(val);
            }
        }

    },

    SUBTRACT {
        @Override
        public Value apply(Value v1, Value v2) {
            DataType t1 = v1.getType();
            DataType t2 = v2.getType();
            DataType newType = getResultingType(t1, t2);

            if (newType.equals(DataType.FLOAT)) {
                Float val = (Float) v1.getVal() - (Float) v2.getVal();
                return new Value<>(val);
            } else {
                Integer val = (Integer) v1.getVal() - (Integer) v2.getVal();
                return new Value<>(val);
            }
        }
    },

    MULTIPLY {
        @Override
        public Value apply(Value v1, Value v2) {
            DataType t1 = v1.getType();
            DataType t2 = v2.getType();
            DataType newType = getResultingType(t1, t2);

            if (newType.equals(DataType.FLOAT)) {
                Float val = (Float) v1.getVal() * (Float) v2.getVal();
                return new Value<>(val);
            } else {
                Integer val = (Integer) v1.getVal() * (Integer) v2.getVal();
                return new Value<>(val);
            }
        }
    },

    DIVIDE {
        @Override
        public Value apply(Value v1, Value v2) {
            DataType t1 = v1.getType();
            DataType t2 = v2.getType();
            DataType newType = getResultingType(t1, t2);

            if (newType.equals(DataType.FLOAT)) {
                Float val;
                try {
                    val = (Float) v1.getVal() / (Float) v2.getVal();
                    return new Value<>(val);
                } catch (ArithmeticException e) {
                    return new Value<>(DataType.NaN);
                }
            } else {
                Integer val;
                try {
                    val = (Integer) v1.getVal() / (Integer) v2.getVal();
                    return new Value<>(val);
                } catch (ArithmeticException e) {
                    return new Value<>(DataType.NaN);
                }
            }
        }
    }

}
