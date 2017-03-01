package examples.p2.codeSamples;

/**
 * Created by Thaniel on 2/27/2017.
 */
public enum Arithmetic implements Operator{
    ADD {
        @Override
        public int apply(int x, int y) {
            return x + y;
        }
    },

    SUBTRACT {
        @Override
        public int apply(int x, int y) {
            return x - y;
        }
    },

    MULTIPLY {
        @Override
        public int apply(int x, int y) {
            return  x * y;
        }
    },

    DIVIDE {
        @Override
        public int apply(int x, int y) {
            return x / y;
        }
    }
}
