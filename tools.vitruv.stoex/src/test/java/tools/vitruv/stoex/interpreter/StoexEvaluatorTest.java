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

import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

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
        assertEquals(42, evaluator.evaluate("42"));
        assertEquals(3.14, evaluator.evaluate("3.14"));
        assertEquals(true, evaluator.evaluate("true"));
        assertEquals("hello", evaluator.evaluate("\"hello\""));
    }

    @Test
    @DisplayName("Should evaluate arithmetic expressions")
    void testArithmeticExpressions() {
        assertEquals(7.0, evaluator.evaluate("3 + 4"));
        assertEquals(14.0, evaluator.evaluate("2 + 3 * 4"));
        assertEquals(20.0, evaluator.evaluate("(2 + 3) * 4"));
        assertEquals(8.0, evaluator.evaluate("2 ^ 3"));
    }

    @Test
    @DisplayName("Should evaluate boolean expressions")
    void testBooleanExpressions() {
        assertEquals(false, evaluator.evaluate("true AND false"));
        assertEquals(true, evaluator.evaluate("true OR false"));
        assertEquals(true, evaluator.evaluate("5 > 3"));
        assertEquals(false, evaluator.evaluate("NOT true"));
    }

    @Test
    @DisplayName("Should evaluate if-else expressions")
    void testIfElseExpressions() {
        assertEquals(42, evaluator.evaluate("true ? 42 : 24"));
        assertEquals(24, evaluator.evaluate("false ? 42 : 24"));
        assertEquals(42, evaluator.evaluate("5 > 3 ? 42 : 24"));
    }

    @Test
    @DisplayName("Should evaluate with variables using map")
    void testVariablesWithMap() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("x", 10);
        variables.put("y", 20);

        assertEquals(30.0, evaluator.evaluate("x + y", variables));
        assertEquals(50.0, evaluator.evaluate("x + y * 2", variables));
    }

    @Test
    @DisplayName("Should evaluate with persistent variables")
    void testPersistentVariables() {
        evaluator.setVariable("radius", 5);
        evaluator.setVariable("height", 10);

        // Variables persist across evaluations
        double area = (Double) evaluator.evaluate("PI * radius ^ 2");
        double volume = (Double) evaluator.evaluate("PI * radius ^ 2 * height");

        assertEquals(Math.PI * 25, area, 1e-10);
        assertEquals(Math.PI * 25 * 10, volume, 1e-10);
    }

    @Test
    @DisplayName("Should evaluate built-in functions")
    void testBuiltInFunctions() {
        assertEquals(4.0, evaluator.evaluate("sqrt(16)"));
        assertEquals(5.0, evaluator.evaluate("abs(-5)"));
        assertEquals(10.0, evaluator.evaluate("max(5, 10)"));
        assertEquals(5.0, evaluator.evaluate("min(5, 10)"));
    }

    @Test
    @DisplayName("Should evaluate built-in constants")
    void testBuiltInConstants() {
        double piResult = (Double) evaluator.evaluate("PI");
        double eResult = (Double) evaluator.evaluate("E");

        assertEquals(Math.PI, piResult, 1e-10);
        assertEquals(Math.E, eResult, 1e-10);
    }

    @Test
    @DisplayName("Should handle complex expressions")
    void testComplexExpressions() {
        evaluator.setVariable("x", 2);
        evaluator.setVariable("y", 3);

        // Complex mathematical expression
        Object result = evaluator.evaluate("(x ^ 2 + y ^ 2) > 10 ? sqrt(x ^ 2 + y ^ 2) : x + y");
        assertEquals(Math.sqrt(13), (Double) result, 0.001);

        // Complex conditional with functions
        evaluator.setVariable("temperature", 25);
        Object weatherResult = evaluator.evaluate("temperature > 20 ? \"warm\" : \"cold\"");
        assertEquals("warm", weatherResult);
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
        double hypotenuse = (Double) evaluator.evaluate("sqrt(a ^ 2 + b ^ 2)");
        assertEquals(5.0, hypotenuse, 1e-10);

        // Scenario 2: Business logic
        evaluator.setVariable("price", 100);
        evaluator.setVariable("discount", 0.2);
        double finalPrice = (Double) evaluator.evaluate("price * (1 - discount)");
        assertEquals(80.0, finalPrice, 1e-10);

        // Scenario 3: Conditional logic
        evaluator.setVariable("age", 25);
        String category = (String) evaluator.evaluate("age >= 18 ? \"adult\" : \"minor\"");
        assertEquals("adult", category);

        // Scenario 4: Complex mathematical formula
        evaluator.setVariable("r", 5.0);
        double sphereVolume = (Double) evaluator.evaluate("(4.0 / 3.0) * PI * r ^ 3");
        assertEquals((4.0 / 3.0) * Math.PI * 125, sphereVolume, 1e-8);
    }

    // Probability Distributions

    @Test
    @DisplayName("Should add Normal distribution")
    void testAddNormalDistribution() {
        Object result = evaluator.evaluate("Normal(196.0, 15.0) + Normal(0.0, 1.0)");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(((NormalDistribution) result).getMu(), 196.0, 0.001);
        assertEquals(((NormalDistribution) result).getSigma(), Math.sqrt(15.0 * 15.0 + 1.0), 0.001);
    }

    @Test
    @DisplayName("Should add Normal distribution, one as Variable")
    void testAddNormalDistributionWithVariable() {
        evaluator.setVariable("var1", "Normal(196.0, 15.0)");
        Object result = evaluator.evaluate("var1 + Normal(0.0, 1.0)");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(((NormalDistribution) result).getMu(), 196.0, 0.001);
        assertEquals(((NormalDistribution) result).getSigma(), Math.sqrt(15.0 * 15.0 + 1.0), 0.001);
    }

    @Test
    @DisplayName("Should add Normal distribution, both as Variables")
    void testAddNormalDistributionWithVariables() {
        evaluator.setVariable("var1", "Normal(196.0, 15.0)");
        evaluator.setVariable("var2", "Normal(0.0, 1.0)");
        Object result = evaluator.evaluate("var1 + var2");
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

        Object result = evaluator.evaluate("throatWidth + newValue - oldValue");
        assertTrue(result instanceof NormalDistribution);
        assertEquals(52.0, ((NormalDistribution) result).getMu(), 0.001);
        assertEquals(0.667, ((NormalDistribution) result).getSigma(), 0.001);
    }

    @Test
    @DisplayName("Should add two Exponential distributions with same lambda")
    void testAddExponentialDistributionsSameLambda() {
        Object result = evaluator.evaluate("Exponential(0.5) + Exponential(0.5)"); // Both with lambda = 0.5
        assertTrue(result instanceof GammaDistribution);
        GammaDistribution gammaResult = (GammaDistribution) result;
        assertEquals(2, gammaResult.getAlpha(), 0.001); // Alpha should
        assertEquals(1 / 0.5, gammaResult.getTheta(), 0.001); // Theta should be 1/lambda
    }

}
