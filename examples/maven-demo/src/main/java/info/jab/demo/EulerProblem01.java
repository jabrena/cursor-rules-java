package info.jab.demo;

public class EulerProblem01 implements IEuler<Integer, Integer> {

    /**
     * Multiples of 3 or 5
     * https://projecteuler.net/problem=1
     *
     * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3,5,6 and 9. The sum of these multiples is 23.
     * Find the sum of all the multiples of 3 or 5 below the provided parameter.
     *
     * @param limit
     * @return
     */
    @Override
    public Integer solution(Integer limit) {

        int sum = 0;

        for (int counter = 1; counter < limit; counter++) {
            if ((counter % 3 == 0) || (counter % 5 == 0)) {
                sum += counter;
            }
        }

        return sum;
    }
}
