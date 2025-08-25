package tools.vitruv.stoex.interpreter;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.tests.StoexInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(StoexInjectorProvider.class)
@DisplayName("Stoex Interpreter Integration Tests")
class StoexInterpreterIntegrationTest {

    @Inject
    private ParseHelper<Expression> parseHelper;

    private StoexInterpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new StoexInterpreter();
    }

    @Test
    @DisplayName("Should evaluate simple arithmetic")
    void testSimpleArithmetic() throws Exception {
        Expression expr = parseHelper.parse("2 + 3");
        Object result = interpreter.evaluate(expr);
        assertEquals(5.0, result);
    }

    @Test
    @DisplayName("Should evaluate boolean expressions")
    void testBooleanExpressions() throws Exception {
        Expression expr = parseHelper.parse("true AND false");
        Object result = interpreter.evaluate(expr);
        assertEquals(false, result);
    }

    @Test
    @DisplayName("Should evaluate comparisons")
    void testComparisons() throws Exception {
        Expression expr = parseHelper.parse("5 > 3");
        Object result = interpreter.evaluate(expr);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Should evaluate if-else")
    void testIfElse() throws Exception {
        Expression expr = parseHelper.parse("5 > 3 ? 42 : 24");
        Object result = interpreter.evaluate(expr);
        assertEquals(42, result);
    }

    @Test
    @DisplayName("Should evaluate variables")
    void testVariables() throws Exception {
        interpreter.setVariable("x", 10);
        Expression expr = parseHelper.parse("x + 5");
        Object result = interpreter.evaluate(expr);
        assertEquals(15.0, result);
    }

    @Test
    @DisplayName("Should evaluate functions")
    void testFunctions() throws Exception {
        Expression expr = parseHelper.parse("sqrt(16)");
        Object result = interpreter.evaluate(expr);
        assertEquals(4.0, result);
    }

    @Test
    @DisplayName("Should handle null input")
    void testNullInput() {
        Object result = interpreter.evaluate(null);
        assertNull(result);
    }
}
