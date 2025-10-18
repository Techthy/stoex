package tools.vitruv.stoex.interpreter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.BoolLiteral;
import tools.vitruv.stoex.stoex.DoubleLiteral;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.IntLiteral;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;
import tools.vitruv.stoex.stoex.StringLiteral;

@DisplayName("Stoex Evaluator Integration Tests")
class StoexEvaluatorTest {

    private StoexEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new StoexEvaluator();
    }

    @Test
    @DisplayName("Should evaluate simple expressions")
    void testSimpleExpressions() {

        assertEquals(42, ((IntLiteral) evaluator.evaluate("42")).getValue());
        assertEquals(3.14, ((DoubleLiteral) evaluator.evaluate("3.14")).getValue());
        assertEquals(true, ((BoolLiteral) evaluator.evaluate("true")).isValue());
        assertEquals("hello", ((StringLiteral) evaluator.evaluate("\"hello\"")).getValue());
    }

    @Test
    @DisplayName("Should evaluate arithmetic expressions")
    void testArithmeticExpressions() {
        assertEquals(7, ((IntLiteral) evaluator.evaluate("3 + 4")).getValue());
        assertEquals(14, ((IntLiteral) evaluator.evaluate("2 + 3 * 4")).getValue());
        assertEquals(20, ((IntLiteral) evaluator.evaluate("(2 + 3) * 4")).getValue());
        assertEquals(8, ((IntLiteral) evaluator.evaluate("2 ^ 3")).getValue());
    }

    @Test
    @DisplayName("Should evaluate boolean expressions")
    void testBooleanExpressions() {
        assertEquals(false, ((BoolLiteral) evaluator.evaluate("true AND false")).isValue());
        assertEquals(true, ((BoolLiteral) evaluator.evaluate("true OR false")).isValue());
        assertEquals(true, ((BoolLiteral) evaluator.evaluate("5 > 3")).isValue());
        assertEquals(false, ((BoolLiteral) evaluator.evaluate("NOT true")).isValue());
    }

    @Test
    @DisplayName("Should evaluate if-else expressions")
    void testIfElseExpressions() {
        assertEquals(42, ((IntLiteral) evaluator.evaluate("true ? 42 : 24")).getValue());
        assertEquals(24, ((IntLiteral) evaluator.evaluate("false ? 42 : 24")).getValue());
        assertEquals(42, ((IntLiteral) evaluator.evaluate("5 > 3 ? 42 : 24")).getValue());
    }

    @Test
    @DisplayName("Should evaluate with variables using map")
    void testVariablesWithMap() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("x", 10);
        variables.put("y", 20);

        assertEquals(30, ((IntLiteral) evaluator.evaluate("x + y", variables)).getValue());
        assertEquals(50, ((IntLiteral) evaluator.evaluate("x + y * 2", variables)).getValue());
    }

    @Test
    @DisplayName("Should evaluate with persistent variables")
    void testPersistentVariables() {
        evaluator.setVariable("radius", 5);
        evaluator.setVariable("height", 10);

        // Variables persist across evaluations
        Expression area = evaluator.evaluate("PI * radius ^ 2");
        Expression volume = evaluator.evaluate("PI * radius ^ 2 * height");

        assertTrue(area instanceof DoubleLiteral);
        assertTrue(volume instanceof DoubleLiteral);

        double areaValue = ((DoubleLiteral) area).getValue();
        double volumeValue = ((DoubleLiteral) volume).getValue();
        assertEquals(Math.PI * 25, areaValue, 1e-10);
        assertEquals(Math.PI * 25 * 10, volumeValue, 1e-10);
    }

    @Test
    @DisplayName("Should evaluate built-in functions")
    void testBuiltInFunctions() {
        assertEquals(4.0, ((DoubleLiteral) evaluator.evaluate("sqrt(16)")).getValue());
        assertEquals(5.0, ((DoubleLiteral) evaluator.evaluate("abs(-5)")).getValue());
        assertEquals(10.0, ((DoubleLiteral) evaluator.evaluate("max(5, 10)")).getValue());
        assertEquals(5.0, ((DoubleLiteral) evaluator.evaluate("min(5, 10)")).getValue());
    }

    @Test
    @DisplayName("Should evaluate built-in constants")
    void testBuiltInConstants() {
        Expression piResult = evaluator.evaluate("PI");
        Expression eResult = evaluator.evaluate("E");

        assertTrue(piResult instanceof DoubleLiteral);
        assertTrue(eResult instanceof DoubleLiteral);
        double piValue = ((DoubleLiteral) piResult).getValue();
        double eValue = ((DoubleLiteral) eResult).getValue();

        assertEquals(Math.PI, piValue, 1e-10);
        assertEquals(Math.E, eValue, 1e-10);
    }

    @Test
    @DisplayName("Should handle complex expressions")
    void testComplexExpressions() {
        evaluator.setVariable("x", 2);
        evaluator.setVariable("y", 3);

        // Complex mathematical expression
        Expression result = evaluator.evaluate("(x ^ 2 + y ^ 2) > 10 ? sqrt(x ^ 2 + y ^ 2) : x + y");
        assertEquals(Math.sqrt(13), ((DoubleLiteral) result).getValue(), 0.001);

        // Complex conditional with functions
        evaluator.setVariable("temperature", 25);
        Expression weatherResult = evaluator.evaluate("temperature > 20 ? \"warm\" : \"cold\"");
        assertEquals("warm", ((StringLiteral) weatherResult).getValue());
    }

    @Test
    @DisplayName("Should get and set variables")
    void testVariableAccess() {
        evaluator.setVariable("testVar", 123);
        assertEquals(123, evaluator.getVariable("testVar"));

        evaluator.setVariable("pi_approx", 3.14159);
        assertEquals(3.14159, evaluator.getVariable("pi_approx"));
    }

    @Test
    @DisplayName("Should handle parse errors gracefully")
    void testParseErrors() {
        assertThrows(RuntimeException.class, () -> {
            evaluator.evaluate("invalid syntax +++");
        });

        assertThrows(RuntimeException.class, () -> {
            evaluator.evaluate("2 + ");
        });
    }

    @Test
    @DisplayName("Should handle evaluation errors gracefully")
    void testEvaluationErrors() {
        assertThrows(RuntimeException.class, () -> {
            evaluator.evaluate("undefinedVariable + 5");
        });

        assertThrows(RuntimeException.class, () -> {
            evaluator.evaluate("5 / 0");
        });

        assertThrows(RuntimeException.class, () -> {
            evaluator.evaluate("unknownFunction(5)");
        });
    }

    @Test
    @DisplayName("Should demonstrate practical usage scenarios")
    void testPracticalScenarios() {
        // Scenario 1: Mathematical calculations
        evaluator.setVariable("a", 3);
        evaluator.setVariable("b", 4);
        double hypotenuse = ((DoubleLiteral) evaluator.evaluate("sqrt(a ^ 2 + b ^ 2)")).getValue();
        assertEquals(5.0, hypotenuse, 1e-10);

        // Scenario 2: Business logic
        evaluator.setVariable("price", 100);
        evaluator.setVariable("discount", 0.2);
        double finalPrice = ((DoubleLiteral) evaluator.evaluate("price * (1 - discount)")).getValue();
        assertEquals(80.0, finalPrice, 1e-10);

        // Scenario 3: Conditional logic
        evaluator.setVariable("age", 25);
        String category = ((StringLiteral) evaluator.evaluate("age >= 18 ? \"adult\" : \"minor\"")).getValue();
        assertEquals("adult", category);

        // Scenario 4: Complex mathematical formula
        evaluator.setVariable("r", 5.0);
        double sphereVolume = ((DoubleLiteral) evaluator.evaluate("(4.0 / 3.0) * PI * r ^ 3")).getValue();
        assertEquals((4.0 / 3.0) * Math.PI * 125, sphereVolume, 1e-8);
    }

    // Probability Distributions

    @Test
    @DisplayName("Should add Normal distribution")
    void testAddNormalDistribution() {
        Expression result = evaluator.evaluate("Normal(196.0, 15.0) + Normal(0.0, 1.0)");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(((NormalDistribution) result).getMu(), 196.0, 0.001);
        assertEquals(((NormalDistribution) result).getSigma(), Math.sqrt(15.0 * 15.0 + 1.0), 0.001);
    }

    @Test
    @DisplayName("Should add Normal distribution, one as Variable")
    void testAddNormalDistributionWithVariable() {
        evaluator.setVariable("var1", "Normal(196.0, 15.0)");
        Expression result = evaluator.evaluate("var1 + Normal(0.0, 1.0)");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(((NormalDistribution) result).getMu(), 196.0, 0.001);
        assertEquals(((NormalDistribution) result).getSigma(), Math.sqrt(15.0 * 15.0 + 1.0), 0.001);
    }

    @Test
    @DisplayName("Should add Normal distribution, both as Variables")
    void testAddNormalDistributionWithVariables() {
        evaluator.setVariable("var1", "Normal(196.0, 15.0)");
        evaluator.setVariable("var2", "Normal(0.0, 1.0)");
        Expression result = evaluator.evaluate("var1 + var2");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(((NormalDistribution) result).getMu(), 196.0, 0.001);
        assertEquals(((NormalDistribution) result).getSigma(), Math.sqrt(15.0 * 15.0 + 1.0), 0.001);
    }

    @Test
    @DisplayName("Should Serialize a Normal Distribution")
    void testSerializeNormalDistribution() {
        NormalDistribution distribution = StoexFactory.eINSTANCE.createNormalDistribution();
        distribution.setMu(196.0);
        distribution.setSigma(15.0);
        String serialized;
        serialized = evaluator.serialize(distribution);

        assertNotNull(serialized);
        // Remove whitespace for comparison (this is ignored by the grammar anyway)
        assertEquals("Normal(196.0,15.0)", serialized.replace(" ", ""));
    }

    @Test
    @DisplayName("Mixed Variables")
    void testMixedVariables() {

        NormalDistribution newValue = StoexFactory.eINSTANCE.createNormalDistribution();
        newValue.setMu(20);
        newValue.setSigma(0.667);

        evaluator.setVariable("oldValue", 10);
        evaluator.setVariable("throatWidth", 42);
        evaluator.setVariable("newValue", newValue);

        Expression result = evaluator.evaluate("throatWidth + newValue - oldValue");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(52.0, ((NormalDistribution) result).getMu(), 0.001);
        assertEquals(0.667, ((NormalDistribution) result).getSigma(), 0.001);
    }

    @Test
    @DisplayName("Should add two Exponential distributions with same lambda")
    void testAddExponentialDistributionsSameLambda() {
        Expression result = evaluator.evaluate("Exponential(0.5) + Exponential(0.5)"); // Both with lambda = 0.5
        assertTrue(result instanceof GammaDistribution);
        GammaDistribution gammaResult = (GammaDistribution) result;
        assertEquals(2, gammaResult.getAlpha(), 0.001); // Alpha should
        assertEquals(1 / 0.5, gammaResult.getTheta(), 0.001); // Theta should be 1/lambda
    }

    // Test mean calculation for Normal Distribution
    @Test
    @DisplayName("Should calculate mean of Normal Distribution")
    void testMeanNormalDistribution() {
        NormalDistribution distribution = StoexFactory.eINSTANCE.createNormalDistribution();
        distribution.setMu(100.0);
        distribution.setSigma(15.0);
        double mean = evaluator.getMean(distribution).doubleValue();
        assertEquals(100.0, mean, 0.001);
    }

    @Test
    @DisplayName("Should calculate mean of Gamma Distribution")
    void testMeanGammaDistribution() {
        GammaDistribution distribution = StoexFactory.eINSTANCE.createGammaDistribution();
        distribution.setAlpha(3.0);
        distribution.setTheta(2.0);
        double mean = evaluator.getMean(distribution).doubleValue();
        assertEquals(6.0, mean, 0.001);
    }

    @Test
    @DisplayName("Should calculate mean of Exponential Distribution")
    void testMeanExponentialDistribution() {
        ExponentialDistribution distribution = StoexFactory.eINSTANCE.createExponentialDistribution();
        distribution.setLambda(0.5);
        double mean = evaluator.getMean(distribution).doubleValue();
        assertEquals(2.0, mean, 0.001); // Mean should be 1/lambda = theta
    }

    @Test
    @DisplayName("Should calculate mean of simple numeric value")
    void testMeanSimpleNumericValue() {
        DoubleLiteral doubleLiteral = StoexFactory.eINSTANCE.createDoubleLiteral();
        doubleLiteral.setValue(42.0);
        double mean = evaluator.getMean(doubleLiteral).doubleValue();
        assertEquals(42.0, mean, 0.001);

        IntLiteral intLiteral = StoexFactory.eINSTANCE.createIntLiteral();
        intLiteral.setValue(7);
        int meanInt = evaluator.getMean(intLiteral).intValue();
        assertEquals(7, meanInt);
    }

}
