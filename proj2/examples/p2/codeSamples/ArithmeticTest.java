package examples.p2.codeSamples;

/**
 * Created by Thaniel on 2/27/2017.
 */
public class ArithmeticTest {
    public static void main(String[] args) {
        Arithmetic add = Arithmetic.ADD;
        int[] numbers = new int[]{1, 2, 3, 4, 5, 6};
        for (Integer i : numbers) {
            i = add.apply(i, 69);
            System.out.println(i);
        }

    }
}
