package tools.vitruv.stoex.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.tests.StoexInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(StoexInjectorProvider.class)
@DisplayName("Stoex Interpreter Tests")
class StoexInterpreterTest {

    @Inject
    private ParseHelper<Expression> parseHelper;

    private StoexInterpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new StoexInterpreter();
    }

    // Basic Literal Tests

    @Test
    @DisplayName("Should evaluate integer literals")
    void testIntegerLiterals() throws Exception {
        Expression expr = parseHelper.parse("42");
        Object result = interpreter.evaluate(expr);
        assertEquals(42, result);
    }

    @Test
    @DisplayName("Should evaluate double literals")
    void testDoubleLiterals() throws Exception {
        Expression expr = parseHelper.parse("3.14");
        Object result = interpreter.evaluate(expr);
        assertEquals(3.14, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should evaluate boolean literals")
    void testBooleanLiterals() throws Exception {
        Expression expr1 = parseHelper.parse("true");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("false");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate string literals")
    void testStringLiterals() throws Exception {
        Expression expr = parseHelper.parse("\"hello world\"");
        Object result = interpreter.evaluate(expr);
        assertEquals("hello world", result);
    }

    // Arithmetic Operations Tests

    @Test
    @DisplayName("Should evaluate addition")
    void testAddition() throws Exception {
        Expression expr = parseHelper.parse("5 + 3");
        Object result = interpreter.evaluate(expr);
        assertEquals(8.0, (Double) result, 0.001);
    }

    @Test
    @DisplayName("Should evaluate subtraction")
    void testSubtraction() throws Exception {
        Expression expr = parseHelper.parse("10 - 4");
        Object result = interpreter.evaluate(expr);
        assertEquals(6.0, (Double) result, 0.001);
    }

    @Test
    @DisplayName("Should evaluate multiplication")
    void testMultiplication() throws Exception {
        Expression expr = parseHelper.parse("6 * 7");
        Object result = interpreter.evaluate(expr);
        assertEquals(42.0, (Double) result, 0.001);
    }

    @Test
    @DisplayName("Should evaluate division")
    void testDivision() throws Exception {
        Expression expr = parseHelper.parse("15 / 3");
        Object result = interpreter.evaluate(expr);
        assertEquals(5.0, (Double) result, 0.001);
    }

    @Test
    @DisplayName("Should handle division by zero")
    void testDivisionByZero() throws Exception {
        Expression expr = parseHelper.parse("10 / 0");
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> interpreter.evaluate(expr));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should evaluate modulo")
    void testModulo() throws Exception {
        Expression expr = parseHelper.parse("17 % 5");
        Object result = interpreter.evaluate(expr);
        assertEquals(2.0, (Double) result, 0.001);
    }

    @Test
    @DisplayName("Should evaluate power operations")
    void testPowerOperations() throws Exception {
        Expression expr = parseHelper.parse("2 ^ 3");
        Object result = interpreter.evaluate(expr);
        assertEquals(8.0, (Double) result, 0.001);
    }

    // Complex Arithmetic Tests

    @Test
    @DisplayName("Should respect operator precedence")
    void testOperatorPrecedence() throws Exception {
        Expression expr = parseHelper.parse("2 + 3 * 4");
        Object result = interpreter.evaluate(expr);
        assertEquals(14.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should handle parentheses")
    void testParentheses() throws Exception {
        Expression expr = parseHelper.parse("(2 + 3) * 4");
        Object result = interpreter.evaluate(expr);
        assertEquals(20.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should handle negative expressions")
    void testNegativeExpressions() throws Exception {
        Expression expr = parseHelper.parse("-5");
        Object result = interpreter.evaluate(expr);
        assertEquals(-5.0, ((Double) result).doubleValue(), 0.001);
    }

    // Boolean Operations Tests

    @Test
    @DisplayName("Should evaluate AND operations")
    void testAndOperations() throws Exception {
        Expression expr1 = parseHelper.parse("true AND true");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("true AND false");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate OR operations")
    void testOrOperations() throws Exception {
        Expression expr1 = parseHelper.parse("true OR false");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("false OR false");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate XOR operations")
    void testXorOperations() throws Exception {
        Expression expr1 = parseHelper.parse("true XOR false");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("true XOR true");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate NOT operations")
    void testNotOperations() throws Exception {
        Expression expr1 = parseHelper.parse("NOT true");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(false, result1);

        Expression expr2 = parseHelper.parse("NOT false");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(true, result2);
    }

    // Comparison Operations Tests

    @Test
    @DisplayName("Should evaluate greater than")
    void testGreaterThan() throws Exception {
        Expression expr1 = parseHelper.parse("5 > 3");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("3 > 5");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate less than")
    void testLessThan() throws Exception {
        Expression expr1 = parseHelper.parse("3 < 5");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("5 < 3");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate equality")
    void testEquality() throws Exception {
        Expression expr1 = parseHelper.parse("5 == 5");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("5 == 3");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("Should evaluate inequality")
    void testInequality() throws Exception {
        Expression expr1 = parseHelper.parse("5 <> 3");
        Object result1 = interpreter.evaluate(expr1);
        assertEquals(true, result1);

        Expression expr2 = parseHelper.parse("5 <> 5");
        Object result2 = interpreter.evaluate(expr2);
        assertEquals(false, result2);
    }

    // If-Else Tests

    @Test
    @DisplayName("Should evaluate if-else expressions (true condition)")
    void testIfElseTrue() throws Exception {
        Expression expr = parseHelper.parse("true ? 42 : 24");
        Object result = interpreter.evaluate(expr);
        assertEquals(42, result);
    }

    @Test
    @DisplayName("Should evaluate if-else expressions (false condition)")
    void testIfElseFalse() throws Exception {
        Expression expr = parseHelper.parse("false ? 42 : 24");
        Object result = interpreter.evaluate(expr);
        assertEquals(24, result);
    }

    @Test
    @DisplayName("Should evaluate complex if-else conditions")
    void testComplexIfElse() throws Exception {
        Expression expr = parseHelper.parse("5 > 3 ? 10 + 5 : 20 - 5");
        Object result = interpreter.evaluate(expr);
        assertEquals(15.0, ((Double) result).doubleValue(), 0.001);
    }

    // Variable Tests

    @Test
    @DisplayName("Should evaluate variables")
    void testVariables() throws Exception {
        interpreter.setVariable("x", 42);
        Expression expr = parseHelper.parse("x");
        Object result = interpreter.evaluate(expr);
        assertEquals(42, result);
    }

    @Test
    @DisplayName("Should handle undefined variables")
    void testUndefinedVariables() throws Exception {
        Expression expr = parseHelper.parse("unknownVar");
        assertThrows(RuntimeException.class, () -> interpreter.evaluate(expr));
    }

    @Test
    @DisplayName("Should evaluate expressions with variables")
    void testExpressionsWithVariables() throws Exception {
        interpreter.setVariable("a", 10);
        interpreter.setVariable("b", 5);
        Expression expr = parseHelper.parse("a + b * 2");
        Object result = interpreter.evaluate(expr);
        assertEquals(20.0, ((Double) result).doubleValue(), 0.001);
    }

    // Built-in Functions Tests

    @Test
    @DisplayName("Should evaluate sin function")
    void testSinFunction() throws Exception {
        Expression expr = parseHelper.parse("sin(0)");
        Object result = interpreter.evaluate(expr);
        assertEquals(0.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should evaluate cos function")
    void testCosFunction() throws Exception {
        Expression expr = parseHelper.parse("cos(0)");
        Object result = interpreter.evaluate(expr);
        assertEquals(1.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should evaluate sqrt function")
    void testSqrtFunction() throws Exception {
        Expression expr = parseHelper.parse("sqrt(25)");
        Object result = interpreter.evaluate(expr);
        assertEquals(5.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should evaluate abs function")
    void testAbsFunction() throws Exception {
        Expression expr = parseHelper.parse("abs(-5)");
        Object result = interpreter.evaluate(expr);
        assertEquals(5.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should evaluate max function")
    void testMaxFunction() throws Exception {
        Expression expr = parseHelper.parse("max(10, 5)");
        Object result = interpreter.evaluate(expr);
        assertEquals(10.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should evaluate min function")
    void testMinFunction() throws Exception {
        Expression expr = parseHelper.parse("min(10, 5)");
        Object result = interpreter.evaluate(expr);
        assertEquals(5.0, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should handle unknown functions")
    void testUnknownFunction() throws Exception {
        Expression expr = parseHelper.parse("unknownFunc(42)");
        assertThrows(IllegalArgumentException.class, () -> interpreter.evaluate(expr));
    }

    // Built-in Constants Tests

    @Test
    @DisplayName("Should provide PI constant")
    void testPiConstant() throws Exception {
        Expression expr = parseHelper.parse("PI");
        Object result = interpreter.evaluate(expr);
        assertEquals(Math.PI, ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should provide E constant")
    void testEConstant() throws Exception {
        Expression expr = parseHelper.parse("E");
        Object result = interpreter.evaluate(expr);
        assertEquals(Math.E, ((Double) result).doubleValue(), 0.001);
    }

    // Constructor Tests

    @Test
    @DisplayName("Should initialize with custom variables")
    void testCustomInitialization() throws Exception {
        Map<String, Object> customVars = new HashMap<>();
        customVars.put("myVar", 100);

        StoexInterpreter customInterpreter = new StoexInterpreter(customVars);
        Expression expr = parseHelper.parse("myVar + 50");
        Object result = customInterpreter.evaluate(expr);
        assertEquals(150.0, ((Double) result).doubleValue(), 0.001);
    }

    // Complex Expression Tests

    @Test
    @DisplayName("Should evaluate complex nested expressions")
    void testComplexNestedExpressions() throws Exception {
        interpreter.setVariable("x", 2);
        interpreter.setVariable("y", 3);

        // (x^2 + y^2) > 10 ? sqrt(x^2 + y^2) : x + y
        Expression expr = parseHelper.parse("(x ^ 2 + y ^ 2) > 10 ? sqrt(x ^ 2 + y ^ 2) : x + y");
        Object result = interpreter.evaluate(expr);

        // x^2 + y^2 = 4 + 9 = 13, which is > 10, so result should be sqrt(13) ≈ 3.606
        assertEquals(Math.sqrt(13), ((Double) result).doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Should handle null expressions")
    void testNullExpression() {
        Object result = interpreter.evaluate(null);
        assertNull(result);
    }
}
