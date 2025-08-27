package tools.vitruv.stoex.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive demonstration of the StoexEvaluator integration class.
 * 
 * This class shows how to use the complete Stoex interpreter with the
 * high-level StoexEvaluator API, which combines parsing, type inference,
 * and expression evaluation in a unified interface.
 */
public class StoexEvaluatorDemo {

    public static void main(String[] args) {
        StoexEvaluator evaluator = new StoexEvaluator();

        System.out.println("=== StoexEvaluator Integration Demo ===\n");

        // 1. Basic expressions without variables
        System.out.println("1. Basic Expressions:");
        demonstrateBasicExpressions(evaluator);

        // 2. Expressions with variables
        System.out.println("\n2. Variable Expressions:");
        demonstrateVariableExpressions(evaluator);

        // 3. Persistent variables
        System.out.println("\n3. Persistent Variables:");
        demonstratePersistentVariables(evaluator);

        // 4. Mathematical functions
        System.out.println("\n4. Mathematical Functions:");
        demonstrateMathematicalFunctions(evaluator);

        // 5. Probability distributions
        System.out.println("\n5. Probability Distributions:");
        demonstrateProbabilityDistributions(evaluator);

        // 6. Type inference
        System.out.println("\n6. Type Inference:");
        demonstrateTypeInference(evaluator);

        // 7. Complex practical scenarios
        System.out.println("\n7. Practical Scenarios:");
        demonstratePracticalScenarios(evaluator);

        System.out.println("\n=== Demo Complete ===");
    }

    private static void demonstrateBasicExpressions(StoexEvaluator evaluator) {
        System.out.println("  2 + 3 * 4 = " + evaluator.evaluate("2 + 3 * 4"));
        System.out.println("  (2 + 3) * 4 = " + evaluator.evaluate("(2 + 3) * 4"));
        System.out.println("  2.5 + 1.5 = " + evaluator.evaluate("2.5 + 1.5"));
        System.out.println("  true AND false = " + evaluator.evaluate("true AND false"));
        System.out.println("  5 > 3 = " + evaluator.evaluate("5 > 3"));
    }

    private static void demonstrateVariableExpressions(StoexEvaluator evaluator) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("x", 10.0);
        vars.put("y", 5.0);
        vars.put("flag", true);

        System.out.println("  With x=10.0, y=5.0:");
        System.out.println("    x + y = " + evaluator.evaluate("x + y", vars));
        System.out.println("    x * y = " + evaluator.evaluate("x * y", vars));
        System.out.println("    x > y = " + evaluator.evaluate("x > y", vars));
        System.out.println("    x + y * 2 = " + evaluator.evaluate("x + y * 2", vars));

        vars.put("flag", false);
        System.out.println("  With flag=false:");
        System.out.println("    flag ? x : y = " + evaluator.evaluate("flag ? x : y", vars));
    }

    private static void demonstratePersistentVariables(StoexEvaluator evaluator) {
        // Set persistent variables
        evaluator.setVariable("a", 7.0);
        evaluator.setVariable("b", 3.0);

        System.out.println("  Set persistent: a=7.0, b=3.0");
        System.out.println("    a + b = " + evaluator.evaluate("a + b"));
        System.out.println("    a * b = " + evaluator.evaluate("a * b"));
        System.out.println("    a > b = " + evaluator.evaluate("a > b"));

        // Override with temporary variables
        Map<String, Object> tempVars = new HashMap<>();
        tempVars.put("b", 10.0);
        System.out.println("  Override b=10.0 temporarily:");
        System.out.println("    a + b = " + evaluator.evaluate("a + b", tempVars));

        // Persistent variables unchanged
        System.out.println("  Back to persistent:");
        System.out.println("    a + b = " + evaluator.evaluate("a + b"));
    }

    private static void demonstrateMathematicalFunctions(StoexEvaluator evaluator) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("angle", Math.PI / 4); // 45 degrees
        vars.put("value", -5.0);

        System.out.println("  With angle=π/4, value=-5.0:");
        System.out.println("    sin(angle) = " + evaluator.evaluate("sin(angle)", vars));
        System.out.println("    cos(angle) = " + evaluator.evaluate("cos(angle)", vars));
        System.out.println("    abs(value) = " + evaluator.evaluate("abs(value)", vars));
        System.out.println("    sqrt(25.0) = " + evaluator.evaluate("sqrt(25.0)", vars));
        System.out.println("    max(value, 0) = " + evaluator.evaluate("max(value, 0)", vars));
        System.out.println("    PI = " + evaluator.evaluate("PI"));
        System.out.println("    E = " + evaluator.evaluate("E"));
    }

    private static void demonstrateProbabilityDistributions(StoexEvaluator evaluator) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("p", 0.3);
        vars.put("n", 10);
        vars.put("mu", 0.0);
        vars.put("sigma", 1.0);

        System.out.println("  With p=0.3, n=10, mu=0.0, sigma=1.0:");

        // Sample from distributions multiple times to show randomness
        for (int i = 0; i < 3; i++) {
            System.out.println("    Sample " + (i + 1) + ":");
            System.out.println("      Bernoulli(p) = " + evaluator.evaluate("BernoulliDistribution(p)", vars));
            System.out.println("      Binomial(n,p) = " + evaluator.evaluate("BinomialDistribution(n, p)", vars));
            System.out.println("      Normal(mu,sigma) = " +
                    String.format("%.3f", (Double) evaluator.evaluate("NormalDistribution(mu, sigma)", vars)));
        }
    }

    private static void demonstrateTypeInference(StoexEvaluator evaluator) {
        System.out.println("  Type inference examples:");
        System.out.println("    42 -> " + evaluator.inferType("42"));
        System.out.println("    3.14 -> " + evaluator.inferType("3.14"));
        System.out.println("    true -> " + evaluator.inferType("true"));
        System.out.println("    \"hello\" -> " + evaluator.inferType("\"hello\""));
        System.out.println("    2 + 3 -> " + evaluator.inferType("2 + 3"));
        System.out.println("    2.5 + 1.5 -> " + evaluator.inferType("2.5 + 1.5"));
        System.out.println("    BernoulliDistribution(0.5) -> " + evaluator.inferType("BernoulliDistribution(0.5)"));
        System.out.println("    NormalDistribution(0, 1) -> " + evaluator.inferType("NormalDistribution(0, 1)"));
    }

    private static void demonstratePracticalScenarios(StoexEvaluator evaluator) {
        // Performance evaluation scenario
        Map<String, Object> perfVars = new HashMap<>();
        perfVars.put("cpuLoad", 0.75);
        perfVars.put("memoryUsage", 0.60);
        perfVars.put("threshold", 0.80);

        System.out.println("  Performance monitoring:");
        System.out.println("    CPU overloaded? " +
                evaluator.evaluate("cpuLoad > threshold", perfVars));
        System.out.println("    System healthy? " +
                evaluator.evaluate("cpuLoad < threshold AND memoryUsage < threshold", perfVars));
        System.out.println("    Performance score = " +
                String.format("%.2f", (Double) evaluator.evaluate("(1.0 - cpuLoad) * (1.0 - memoryUsage)", perfVars)));

        // Reliability calculation
        Map<String, Object> reliabilityVars = new HashMap<>();
        reliabilityVars.put("componentA", 0.99);
        reliabilityVars.put("componentB", 0.95);
        reliabilityVars.put("componentC", 0.98);

        System.out.println("\n  Reliability analysis:");
        System.out.println("    Series system: " +
                String.format("%.4f",
                        (Double) evaluator.evaluate("componentA * componentB * componentC", reliabilityVars)));
        System.out.println("    Parallel redundancy: " +
                String.format("%.4f",
                        (Double) evaluator.evaluate("1.0 - (1.0 - componentA) * (1.0 - componentB)", reliabilityVars)));

        // Probabilistic modeling
        Map<String, Object> probVars = new HashMap<>();
        probVars.put("failureRate", 0.01);
        probVars.put("repairTime", 2.0);

        System.out.println("\n  Probabilistic modeling:");
        System.out.println("    Exponential failure sample = " +
                String.format("%.2f", (Double) evaluator.evaluate("ExponentialDistribution(failureRate)", probVars)));
        System.out.println("    Availability estimate = " +
                String.format("%.4f", (Double) evaluator.evaluate("1.0 / (1.0 + failureRate * repairTime)", probVars)));
    }
}
