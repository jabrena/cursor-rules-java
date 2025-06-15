package info.jab.demo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import info.jab.demo.utils.EulerAnswersLoader;

class EulerProblem01Test {

    @Test
    void given_JavaSolution_when_executeMethod_then_expectedResultsTest() {
        EulerAnswersLoader eulerAnswers = new EulerAnswersLoader();

        EulerProblem01 problem = new EulerProblem01();

        assertThat(problem.solution(10)).isEqualTo(3 + 5 + 6 + 9);
        assertThat(problem.solution(1000)).isEqualTo(eulerAnswers.getAnswerToInt(1));
    }
}
