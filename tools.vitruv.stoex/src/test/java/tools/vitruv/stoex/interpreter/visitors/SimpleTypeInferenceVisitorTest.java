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
@DisplayName("Simple Type Inference Visitor Tests")
class SimpleTypeInferenceVisitorTest {

    @Inject
    private ParseHelper<Expression> parseHelper;

    private TypeInferenceVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new TypeInferenceVisitor();
    }

    @Test
    @DisplayName("Should parse and infer type for integer literal")
    void testSimpleIntegerLiteral() throws Exception {
        Expression expr = parseHelper.parse("42");
        System.out.println("Parsed expression: " + expr);
        System.out.println("Expression class: " + expr.getClass().getSimpleName());

        TypeEnum result = visitor.doSwitch(expr);
        System.out.println("Inferred type: " + result);

        assertNotNull(expr);
        assertEquals(TypeEnum.INT, result);
    }

    @Test
    @DisplayName("Should parse and infer type for double literal")
    void testSimpleDoubleLiteral() throws Exception {
        Expression expr = parseHelper.parse("3.14");
        System.out.println("Parsed expression: " + expr);
        System.out.println("Expression class: " + expr.getClass().getSimpleName());

        TypeEnum result = visitor.doSwitch(expr);
        System.out.println("Inferred type: " + result);

        assertNotNull(expr);
        assertEquals(TypeEnum.DOUBLE, result);
    }

    @Test
    @DisplayName("Should parse and diagnose distribution expression")
    void testDistributionParsing() throws Exception {
        Expression expr = parseHelper.parse("Bernoulli(0.5)");
        System.out.println("Parsed expression: " + expr);
        System.out.println("Expression class: " + expr.getClass().getSimpleName());

        if (expr != null) {
            TypeEnum result = visitor.doSwitch(expr);
            System.out.println("Inferred type: " + result);
        } else {
            System.out.println("Expression failed to parse");
        }
    }

    @Test
    @DisplayName("Should parse and diagnose normal distribution expression")
    void testNormalDistributionParsing() throws Exception {
        Expression expr = parseHelper.parse("Normal(0.0, 1.0)");
        System.out.println("Parsed expression: " + expr);
        System.out.println("Expression class: " + expr.getClass().getSimpleName());

        if (expr != null) {
            TypeEnum result = visitor.doSwitch(expr);
            System.out.println("Inferred type: " + result);
        } else {
            System.out.println("Expression failed to parse");
        }
    }

    @Test
    @DisplayName("Should show parsing errors for invalid syntax")
    void testParsingErrors() throws Exception {
        try {
            Expression expr = parseHelper.parse("InvalidSyntax(");
            if (expr != null && expr.eResource() != null) {
                System.out.println("Parsing errors: " + expr.eResource().getErrors());
            }
        } catch (Exception e) {
            System.out.println("Parse exception: " + e.getMessage());
        }
    }
}
