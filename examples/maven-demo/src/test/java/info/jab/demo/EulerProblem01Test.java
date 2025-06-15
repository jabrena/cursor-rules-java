package info.jab.demo;

import info.jab.demo.utils.EulerAnswersLoader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EulerProblem01Test {

    @Test
    void given_JavaSolution_when_executeMethod_then_expectedResultsTest() {
        EulerAnswersLoader eulerAnswers = new EulerAnswersLoader();

        EulerProblem01 problem = new EulerProblem01();

        assertThat(problem.solution(10)).isEqualTo(3 + 5 + 6 + 9);
        assertThat(problem.solution(1000)).isEqualTo(eulerAnswers.getAnswerToInt(1));
    }
}
