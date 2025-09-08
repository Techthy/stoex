package tools.vitruv.stoex.interpreter.visitors;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import tools.vitruv.stoex.interpreter.operations.MonteCarloOperation;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.tests.StoexInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(StoexInjectorProvider.class)
@DisplayName("Expression Evaluation Visitor Tests")
class ExpressionEvaluationVisitorTest {

    @Inject
    private ParseHelper<Expression> parseHelper;

    private ExpressionEvaluationVisitor evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ExpressionEvaluationVisitor();
    }

    @Test
    @DisplayName("Should evaluate basic literals")
    void testBasicLiterals() throws Exception {
        assertEquals(42, evaluator.doSwitch(parseHelper.parse("42")));
        assertEquals(3.14, evaluator.doSwitch(parseHelper.parse("3.14")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("true")));
        assertEquals(false, evaluator.doSwitch(parseHelper.parse("false")));
        assertEquals("hello", evaluator.doSwitch(parseHelper.parse("\"hello\"")));
    }

    @Test
    @DisplayName("Should evaluate arithmetic operations")
    void testArithmeticOperations() throws Exception {
        assertEquals(7.0, evaluator.doSwitch(parseHelper.parse("3 + 4")));
        assertEquals(14.0, evaluator.doSwitch(parseHelper.parse("2 + 3 * 4")));
        assertEquals(20.0, evaluator.doSwitch(parseHelper.parse("(2 + 3) * 4")));
        assertEquals(2.0, evaluator.doSwitch(parseHelper.parse("10 / 2 - 3")));
        assertEquals(8.0, evaluator.doSwitch(parseHelper.parse("2 ^ 3")));
    }

    @Test
    @DisplayName("Should evaluate boolean operations")
    void testBooleanOperations() throws Exception {
        assertEquals(false, evaluator.doSwitch(parseHelper.parse("true AND false")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("true OR false")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("true XOR false")));
        assertEquals(false, evaluator.doSwitch(parseHelper.parse("NOT true")));
    }

    @Test
    @DisplayName("Should evaluate comparison operations")
    void testComparisonOperations() throws Exception {
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("5 > 3")));
        assertEquals(false, evaluator.doSwitch(parseHelper.parse("3 > 5")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("5 >= 5")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("3 < 5")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("5 == 5")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("5 <> 3")));
    }

    @Test
    @DisplayName("Should evaluate if-else expressions")
    void testIfElseExpressions() throws Exception {
        assertEquals(42, evaluator.doSwitch(parseHelper.parse("true ? 42 : 24")));
        assertEquals(24, evaluator.doSwitch(parseHelper.parse("false ? 42 : 24")));
        assertEquals(42, evaluator.doSwitch(parseHelper.parse("5 > 3 ? 42 : 24")));
    }

    @Test
    @DisplayName("Should evaluate variables")
    void testVariables() throws Exception {
        evaluator.setVariable("x", 10);
        evaluator.setVariable("y", 20);

        assertEquals(10, evaluator.doSwitch(parseHelper.parse("x")));
        assertEquals(30.0, evaluator.doSwitch(parseHelper.parse("x + y")));
        assertEquals(50.0, evaluator.doSwitch(parseHelper.parse("x + y * 2")));
    }

    @Test
    @DisplayName("Should evaluate built-in functions")
    void testBuiltInFunctions() throws Exception {
        assertEquals(4.0, evaluator.doSwitch(parseHelper.parse("sqrt(16)")));
        assertEquals(5.0, evaluator.doSwitch(parseHelper.parse("abs(-5)")));
        assertEquals(10.0, evaluator.doSwitch(parseHelper.parse("max(5, 10)")));
        assertEquals(5.0, evaluator.doSwitch(parseHelper.parse("min(5, 10)")));

        // Trigonometric functions
        assertEquals(1.0, (Double) evaluator.doSwitch(parseHelper.parse("sin(PI / 2)")), 1e-10);
        assertEquals(1.0, (Double) evaluator.doSwitch(parseHelper.parse("cos(0)")), 1e-10);
    }

    @Test
    @DisplayName("Should evaluate built-in constants")
    void testBuiltInConstants() throws Exception {
        double piResult = (Double) evaluator.doSwitch(parseHelper.parse("PI"));
        assertEquals(Math.PI, piResult, 1e-10);

        double eResult = (Double) evaluator.doSwitch(parseHelper.parse("E"));
        assertEquals(Math.E, eResult, 1e-10);
    }

    @Test
    @DisplayName("Should handle parentheses correctly")
    void testParentheses() throws Exception {
        assertEquals(20.0, evaluator.doSwitch(parseHelper.parse("(2 + 3) * 4")));
        assertEquals(14.0, evaluator.doSwitch(parseHelper.parse("2 + (3 * 4)")));
        assertEquals(10.0, evaluator.doSwitch(parseHelper.parse("((2 + 3) * 4) / 2")));
    }

    @Test
    @DisplayName("Should handle unary operations")
    void testUnaryOperations() throws Exception {
        assertEquals(-5.0, evaluator.doSwitch(parseHelper.parse("-5")));
        assertEquals(-10.0, evaluator.doSwitch(parseHelper.parse("-(5 + 5)")));
        assertEquals(false, evaluator.doSwitch(parseHelper.parse("NOT true")));
        assertEquals(true, evaluator.doSwitch(parseHelper.parse("NOT false")));
    }

    @Test
    @DisplayName("Should handle division by zero")
    void testDivisionByZero() throws Exception {
        assertThrows(ArithmeticException.class, () -> {
            evaluator.doSwitch(parseHelper.parse("5 / 0"));
        });
    }

    @Test
    @DisplayName("Should handle undefined variables")
    void testUndefinedVariables() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            evaluator.doSwitch(parseHelper.parse("undefinedVar"));
        });
    }

    @Test
    @DisplayName("Should handle unknown functions")
    void testUnknownFunctions() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            evaluator.doSwitch(parseHelper.parse("unknownFunction(5)"));
        });
    }

    @Test
    @DisplayName("Should evaluate complex expressions")
    void testComplexExpressions() throws Exception {
        evaluator.setVariable("x", 2);
        evaluator.setVariable("y", 3);

        // (x^2 + y^2) > 10 ? sqrt(x^2 + y^2) : x + y
        Expression expr = parseHelper.parse("(x ^ 2 + y ^ 2) > 10 ? sqrt(x ^ 2 + y ^ 2) : x + y");
        Object result = evaluator.doSwitch(expr);

        // x^2 + y^2 = 4 + 9 = 13, which is > 10, so result should be sqrt(13) ≈ 3.606
        assertEquals(Math.sqrt(13), (Double) result, 0.001);
    }

    @Test
    @DisplayName("Should evaluate addition of two normal distributions")
    void testAddNormalDistributions() throws Exception {
        Expression expr = parseHelper.parse("Normal(0.0, 1.0) + Normal(2.0, 3.0)");
        Object result = evaluator.doSwitch(expr);

        System.out.println(result);

        assertTrue(result instanceof NormalDistribution);
        NormalDistribution resultDist = (NormalDistribution) result;
        assertEquals(2, resultDist.getMu(), 1e-10);
        assertEquals(Math.sqrt(10), resultDist.getSigma(), 1e-10);
    }

    @Test
    @DisplayName("Should evaluate addition of two exponential distributions with same lambda")
    void testAddExponentialDistributionsSameLambda() throws Exception {
        // Arrange
        Expression expr = parseHelper.parse("Exponential(1.0) + Exponential(1.0)");

        // Act
        Object result = evaluator.doSwitch(expr);

        // Assert
        assertTrue(result instanceof GammaDistribution);
        GammaDistribution resultDist = (GammaDistribution) result;
        assertEquals(2, resultDist.getAlpha(), 1e-10);
        assertEquals(1.0, resultDist.getTheta(), 1e-10);
    }

    @Test
    @DisplayName("Should evaluate addition of two exponential distributions with different lambda")
    void testAddExponentialDistributionsDifferentLambda() throws Exception {
        Expression expr = parseHelper.parse("Exponential(1.0) + Exponential(2.0)");
        Object result = evaluator.doSwitch(expr);

        assertTrue(result instanceof SampledDistribution);
        SampledDistribution sampledResult = (SampledDistribution) result;
        MonteCarloOperation operation = new MonteCarloOperation();
        double[] valuesArray = sampledResult.getValues().stream().mapToDouble(Double::doubleValue).toArray();
        double[][] histogram = operation.histogram(valuesArray, 10);
        for (int i = 0; i < histogram[0].length - 1; i++) {
            assertTrue(histogram[0][i] + 10 >= histogram[0][i + 1]);
        }
    }

    @Test
    @DisplayName("Should evaluate addition of two gamma distributions with same theta")
    void testAddGammaDistributionsSameTheta() throws Exception {
        Expression expr = parseHelper.parse("Gamma(2.0, 1.0) + Gamma(3.0, 1.0)");
        Object result = evaluator.doSwitch(expr);
        assertTrue(result instanceof GammaDistribution);
        GammaDistribution resultDist = (GammaDistribution) result;
        assertEquals(5.0, resultDist.getAlpha(), 1e-10);
        assertEquals(1.0, resultDist.getTheta(), 1e-10);
    }

    @Test
    @DisplayName("Should evaluate subtraction of two normal distributions")
    void testSubNormalDistributions() throws Exception {
        Expression expr = parseHelper.parse("Normal(5.0, 2.0) - Normal(3.0, 1.0)");
        Object result = evaluator.doSwitch(expr);

        assertTrue(result instanceof NormalDistribution);
        NormalDistribution resultDist = (NormalDistribution) result;
        assertEquals(2.0, resultDist.getMu(), 1e-10);
        assertEquals(Math.sqrt(5), resultDist.getSigma(), 1e-10);
    }

    @Test
    @DisplayName("Should evaluate subtraction of two exponential distributions with different lambda")
    void testSubExponentialDistributionsDifferentLambda() throws Exception {
        Expression expr = parseHelper.parse("Exponential(1.0) - Exponential(2.0)");
        Object result = evaluator.doSwitch(expr);
        assertTrue(result instanceof SampledDistribution);
        // Result a more complex distribution (see plot)
    }

    @Test
    @DisplayName("Should add two bernoulli distributions (same p)")
    void testAddBernoulliDistributionsSameP() throws Exception {
        Expression expr = parseHelper.parse("Bernoulli(0.7) + Bernoulli(0.7)");
        Object result = evaluator.doSwitch(expr);
        assertTrue(result instanceof BinomialDistribution);
        BinomialDistribution resultDist = (BinomialDistribution) result;
        assertEquals(2, resultDist.getN());
        assertEquals(0.7, resultDist.getP(), 1e-10);
    }

    @Test
    @DisplayName("Should add two bernoulli distributions (different p)")
    void testAddBernoulliDistributionsDifferentP() throws Exception {
        Expression expr = parseHelper.parse("Bernoulli(0.7) + Bernoulli(0.4)");
        Object result = evaluator.doSwitch(expr);
        assertTrue(result instanceof IntProbabilityMassFunction);
        IntProbabilityMassFunction resultDist = (IntProbabilityMassFunction) result;
        assertEquals(3, resultDist.getSamples().size());
        for (var sample : resultDist.getSamples()) {
            if (sample.getValue() == 0) {
                assertEquals(0.18, sample.getProbability(), 1e-10);
            } else if (sample.getValue() == 1) {
                assertEquals(0.54, sample.getProbability(), 1e-10);
            } else if (sample.getValue() == 2) {
                assertEquals(0.28, sample.getProbability(), 1e-10);
            } else {
                throw new RuntimeException("Unexpected sample value: " + sample.getValue());
            }
        }
    }

    @Test
    @DisplayName("Test complex expression for clamping force uncertainty")
    void testComplexExpression() throws Exception {
        evaluator.setVariable("pistonDiameterInMM", "Normal(50, 2)"); // mm
        evaluator.setVariable("hydraulicPressureInBar", "Sampled[70.0, 90.0, 75.0, 85.0, 80.0]"); // bar
        Expression expr = parseHelper.parse("PI * (pistonDiameterInMM / 2) ^ 2 * hydraulicPressureInBar * 10 ^ 5");
        Object result = evaluator.doSwitch(expr);
        assertTrue(result instanceof SampledDistribution);
        SampledDistribution resultDist = (SampledDistribution) result;
        double mean = resultDist.getValues().stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        assertEquals(15707963270.0, mean, 1e9);
    }
}