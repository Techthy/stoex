package tools.vitruv.stoex.interpreter.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import tools.vitruv.stoex.interpreter.TypeEnum;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.tests.StoexInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(StoexInjectorProvider.class)
@DisplayName("Debug Complex Expression Tests")
class DebugComplexExpressionsTest {

    @Inject
    private ParseHelper<Expression> parseHelper;

    private TypeInferenceVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new TypeInferenceVisitor();
    }

    @Test
    @DisplayName("Debug parenthesized expressions")
    void testParenthesizedExpressions() throws Exception {
        // Test basic parentheses
        Expression expr = parseHelper.parse("(Bernoulli(0.5))");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        TypeEnum result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
        System.out.println();

        // Test addition in parentheses
        expr = parseHelper.parse("(Bernoulli(0.5) + 1)");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
        System.out.println();

        // Test multiplication of parenthesized expressions
        expr = parseHelper.parse("(Bernoulli(0.5) + 1) * (Normal(0.0, 1.0) + 2.0)");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
    }

    @Test
    @DisplayName("Debug if-else with distributions")
    void testIfElseDistributions() throws Exception {
        // Simple case first
        Expression expr = parseHelper.parse("Bernoulli(0.7) ? 1 : 2");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        TypeEnum result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
        System.out.println();

        // More complex case
        expr = parseHelper.parse("Bernoulli(0.7) ? Normal(0.0, 1.0) : Exponential(1.0)");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
    }

    @Test
    @DisplayName("Debug power expressions")
    void testPowerExpressions() throws Exception {
        // Simple power
        Expression expr = parseHelper.parse("Normal(0.0, 1.0) ^ 2");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        TypeEnum result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
        System.out.println();

        // Power in parentheses
        expr = parseHelper.parse("(Normal(0.0, 1.0) ^ 2)");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
        System.out.println();

        // Comparison of power
        expr = parseHelper.parse("(Normal(0.0, 1.0) ^ 2) > 0.5");
        System.out.println("Parsed: " + expr);
        System.out.println("Class: " + expr.getClass().getSimpleName());
        result = visitor.doSwitch(expr);
        System.out.println("Type: " + result);
    }
}
