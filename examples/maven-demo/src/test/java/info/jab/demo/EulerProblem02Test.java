package info.jab.demo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import info.jab.demo.utils.EulerAnswersLoader;

public class EulerProblem02Test {

    @Test
    void given_JavaSolution_when_executeMethod_then_expectedResultsTest() {

        EulerAnswersLoader eulerAnswers = new EulerAnswersLoader();


        EulerProblem02 problem = new EulerProblem02();

        assertThat(problem.solution(100L)).isEqualTo(2 + 8 + 34);
        assertThat(problem.solution(4_000_000L)).isEqualTo(eulerAnswers.getAnswerToLong(2));
    }
}
