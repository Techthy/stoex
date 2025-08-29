package tools.vitruv.stoex.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test to demonstrate the complete StoexEvaluator functionality
 */
public class StoexEvaluatorShowcaseTest {

    @Test
    @Disabled
    public void showcaseCompleteIntegration() {
        StoexEvaluator evaluator = new StoexEvaluator();

        System.out.println("=== StoexEvaluator Complete Integration Showcase ===\n");

        // 1. Basic expressions - no variables needed
        System.out.println("1. Basic Arithmetic & Logic:");
        System.out.println("   2 + 3 * 4 = " + evaluator.evaluate("2 + 3 * 4"));
        System.out.println("   (2 + 3) * 4 = " + evaluator.evaluate("(2 + 3) * 4"));
        System.out.println("   5 > 3 = " + evaluator.evaluate("5 > 3"));
        System.out.println("   true AND false = " + evaluator.evaluate("true AND false"));

        // 2. Variable expressions
        System.out.println("\n2. Variables & Complex Expressions:");
        Map<String, Object> vars = new HashMap<>();
        vars.put("x", 10.0);
        vars.put("y", 5.0);
        vars.put("threshold", 7.5);
        vars.put("active", true);

        System.out.println("   With x=10.0, y=5.0, threshold=7.5, active=true:");
        System.out.println("   x + y = " + evaluator.evaluate("x + y", vars));
        System.out.println("   x * y / 2 = " + evaluator.evaluate("x * y / 2", vars));
        System.out.println("   x > threshold = " + evaluator.evaluate("x > threshold", vars));
        System.out.println("   active ? x : y = " + evaluator.evaluate("active ? x : y", vars));
        System.out.println("   (x + y) > threshold = " + evaluator.evaluate("(x + y) > threshold", vars));

        // 3. Mathematical functions
        System.out.println("\n3. Mathematical Functions:");
        vars.put("angle", Math.PI / 4);
        vars.put("value", 25.0);
        System.out.println("   With angle=π/4, value=25.0:");
        System.out.println("   sin(angle) = "
                + String.format("%.3f", (Double) evaluator.evaluate("sin(angle)", vars)));
        System.out.println("   cos(angle) = "
                + String.format("%.3f", (Double) evaluator.evaluate("cos(angle)", vars)));
        System.out.println("   sqrt(value) = " + evaluator.evaluate("sqrt(value)", vars));
        System.out.println("   abs(-10) = " + evaluator.evaluate("abs(-10)"));
        System.out.println("   max(x, y) = " + evaluator.evaluate("max(x, y)", vars));
        System.out.println("   PI = " + String.format("%.6f", (Double) evaluator.evaluate("PI")));

        // 4. Probability distributions
        System.out.println("\n4. Probability Distributions:");
        vars.put("p", 0.3);
        vars.put("n", 10);
        vars.put("mu", 0.0);
        vars.put("sigma", 1.0);
        System.out.println("   With p=0.3, n=10, mu=0.0, sigma=1.0:");

        for (int i = 1; i <= 3; i++) {
            System.out.println("   Sample " + i + ":");
            System.out.println("     Bernoulli(p) = " + evaluator.evaluate("Bernoulli(p)", vars));
            System.out.println("     Binomial(n,p) = " + evaluator.evaluate("Binomial(n, p)", vars));
            System.out.println("     Normal(mu,sigma) = " +
                    String.format("%.3f", (Double) evaluator.evaluate("Normal(mu, sigma)", vars)));
        }

        // 5. Type inference
        System.out.println("\n5. Type Inference:");
        System.out.println("   42 has type: " + evaluator.inferType("42"));
        System.out.println("   3.14 has type: " + evaluator.inferType("3.14"));
        System.out.println("   true has type: " + evaluator.inferType("true"));
        System.out.println("   2 + 3 has type: " + evaluator.inferType("2 + 3"));
        System.out.println("   2.5 + 1.5 has type: " + evaluator.inferType("2.5 + 1.5"));
        System.out.println("   BernoulliDistribution(0.5) has type: " + evaluator.inferType("Bernoulli(0.5)"));

        // 6. Persistent variables
        System.out.println("\n6. Persistent Variables:");
        evaluator.setVariable("a", 100.0);
        evaluator.setVariable("b", 50.0);
        System.out.println("   Set persistent: a=100.0, b=50.0");
        System.out.println("   a + b = " + evaluator.evaluate("a + b"));
        System.out.println("   a / b = " + evaluator.evaluate("a / b"));

        // 7. Practical scenarios
        System.out.println("\n7. Practical Application - Performance Monitoring:");
        Map<String, Object> perfVars = new HashMap<>();
        perfVars.put("cpuUsage", 0.75);
        perfVars.put("memUsage", 0.60);
        perfVars.put("diskUsage", 0.40);
        perfVars.put("alertThreshold", 0.80);

        System.out.println("   System metrics: CPU=75%, Memory=60%, Disk=40%, Threshold=80%");
        System.out.println("   CPU Alert: " + evaluator.evaluate("cpuUsage > alertThreshold", perfVars));
        System.out.println("   Any Alert: " + evaluator.evaluate(
                "cpuUsage > alertThreshold OR memUsage > alertThreshold OR diskUsage > alertThreshold",
                perfVars));
        System.out.println("   Health Score: " + String.format("%.1f%%",
                (Double) evaluator.evaluate("(1.0 - (cpuUsage + memUsage + diskUsage) / 3.0) * 100",
                        perfVars)));

        System.out.println("\n8. Reliability Analysis:");
        Map<String, Object> relVars = new HashMap<>();
        relVars.put("componentA", 0.99);
        relVars.put("componentB", 0.95);
        relVars.put("componentC", 0.98);

        System.out.println("   Component reliabilities: A=99%, B=95%, C=98%");
        System.out.println("   Series reliability: " + String.format("%.4f",
                (Double) evaluator.evaluate("componentA * componentB * componentC", relVars)));
        System.out.println("   Parallel (A||B) reliability: " + String.format("%.4f",
                (Double) evaluator.evaluate("1.0 - (1.0 - componentA) * (1.0 - componentB)", relVars)));

        System.out.println("\n=== Showcase Complete - All functionality working! ===");
    }
}
