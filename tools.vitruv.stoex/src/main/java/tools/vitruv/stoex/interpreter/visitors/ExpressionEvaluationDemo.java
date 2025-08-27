package tools.vitruv.stoex.interpreter.visitors;

import java.io.ByteArrayInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import tools.vitruv.stoex.StoexStandaloneSetup;
import tools.vitruv.stoex.stoex.Expression;

/**
 * Demo class showing how to use the ExpressionEvaluationVisitor.
 */
public class ExpressionEvaluationDemo {

    public static void main(String[] args) {
        try {
            // Setup Xtext for parsing
            Injector injector = new StoexStandaloneSetup().createInjectorAndDoEMFRegistration();
            ResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

            // Create evaluation visitor
            ExpressionEvaluationVisitor evaluator = new ExpressionEvaluationVisitor();

            // Demo 1: Basic arithmetic
            System.out.println("=== Demo 1: Basic Arithmetic ===");
            Expression expr1 = parseExpression(resourceSet, "2 + 3 * 4");
            Object result1 = evaluator.doSwitch(expr1);
            System.out.println("2 + 3 * 4 = " + result1);

            Expression expr2 = parseExpression(resourceSet, "10 / 2 - 3");
            Object result2 = evaluator.doSwitch(expr2);
            System.out.println("10 / 2 - 3 = " + result2);

            // Demo 2: Boolean operations
            System.out.println("\n=== Demo 2: Boolean Operations ===");
            Expression expr3 = parseExpression(resourceSet, "true AND false");
            Object result3 = evaluator.doSwitch(expr3);
            System.out.println("true AND false = " + result3);

            Expression expr4 = parseExpression(resourceSet, "5 > 3");
            Object result4 = evaluator.doSwitch(expr4);
            System.out.println("5 > 3 = " + result4);

            // Demo 3: If-else expressions
            System.out.println("\n=== Demo 3: If-Else Expressions ===");
            Expression expr5 = parseExpression(resourceSet, "10 > 5 ? 42 : 24");
            Object result5 = evaluator.doSwitch(expr5);
            System.out.println("10 > 5 ? 42 : 24 = " + result5);

            // Demo 4: Variables
            System.out.println("\n=== Demo 4: Variables ===");
            evaluator.setVariable("x", 10);
            evaluator.setVariable("y", 20);
            Expression expr6 = parseExpression(resourceSet, "x + y * 2");
            Object result6 = evaluator.doSwitch(expr6);
            System.out.println("x + y * 2 (where x=10, y=20) = " + result6);

            // Demo 5: Built-in functions
            System.out.println("\n=== Demo 5: Built-in Functions ===");
            Expression expr7 = parseExpression(resourceSet, "sqrt(16)");
            Object result7 = evaluator.doSwitch(expr7);
            System.out.println("sqrt(16) = " + result7);

            Expression expr8 = parseExpression(resourceSet, "max(5, 10)");
            Object result8 = evaluator.doSwitch(expr8);
            System.out.println("max(5, 10) = " + result8);

            // Demo 6: Constants
            System.out.println("\n=== Demo 6: Constants ===");
            Expression expr9 = parseExpression(resourceSet, "PI * 2");
            Object result9 = evaluator.doSwitch(expr9);
            System.out.println("PI * 2 = " + result9);

            // Demo 7: Complex expressions
            System.out.println("\n=== Demo 7: Complex Expressions ===");
            evaluator.setVariable("radius", 5);
            Expression expr10 = parseExpression(resourceSet, "PI * radius ^ 2");
            Object result10 = evaluator.doSwitch(expr10);
            System.out.println("PI * radius ^ 2 (radius=5) = " + result10);

            System.out.println("\n=== Demo completed successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int counter = 0;

    private static Expression parseExpression(ResourceSet resourceSet, String input) throws Exception {
        URI uri = URI.createURI("dummy" + (++counter) + ".stoex");
        Resource resource = resourceSet.createResource(uri);
        resource.load(new ByteArrayInputStream(input.getBytes()), null);

        if (!resource.getErrors().isEmpty()) {
            throw new RuntimeException("Parse errors: " + resource.getErrors());
        }

        return (Expression) resource.getContents().get(0);
    }
}
