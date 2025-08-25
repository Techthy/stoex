package tools.vitruv.stoex.interpreter;

import java.io.ByteArrayInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import tools.vitruv.stoex.StoexStandaloneSetup;
import tools.vitruv.stoex.stoex.Expression;

/**
 * Demo class showing how to use the Stoex interpreter.
 */
public class StoexInterpreterDemo {

    public static void main(String[] args) {
        // Setup Xtext
        Injector injector = new StoexStandaloneSetup().createInjectorAndDoEMFRegistration();
        ResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

        // Create interpreter
        StoexInterpreter interpreter = new StoexInterpreter();

        try {
            // Demo 1: Simple arithmetic
            System.out.println("=== Demo 1: Simple Arithmetic ===");
            Expression expr1 = parseExpression(resourceSet, "2 + 3 * 4");
            Object result1 = interpreter.evaluate(expr1);
            System.out.println("2 + 3 * 4 = " + result1);

            // Demo 2: Boolean operations
            System.out.println("\n=== Demo 2: Boolean Operations ===");
            Expression expr2 = parseExpression(resourceSet, "true AND false OR true");
            Object result2 = interpreter.evaluate(expr2);
            System.out.println("true AND false OR true = " + result2);

            // Demo 3: Comparisons
            System.out.println("\n=== Demo 3: Comparisons ===");
            Expression expr3 = parseExpression(resourceSet, "5 > 3 AND 2 < 4");
            Object result3 = interpreter.evaluate(expr3);
            System.out.println("5 > 3 AND 2 < 4 = " + result3);

            // Demo 4: If-else expressions
            System.out.println("\n=== Demo 4: If-Else Expressions ===");
            Expression expr4 = parseExpression(resourceSet, "10 > 5 ? \"greater\" : \"less or equal\"");
            Object result4 = interpreter.evaluate(expr4);
            System.out.println("10 > 5 ? \"greater\" : \"less or equal\" = " + result4);

            // Demo 5: Variables
            System.out.println("\n=== Demo 5: Variables ===");
            interpreter.setVariable("x", 10);
            interpreter.setVariable("y", 20);
            Expression expr5 = parseExpression(resourceSet, "x + y * 2");
            Object result5 = interpreter.evaluate(expr5);
            System.out.println("x + y * 2 (where x=10, y=20) = " + result5);

            // Demo 6: Built-in functions
            System.out.println("\n=== Demo 6: Built-in Functions ===");
            Expression expr6 = parseExpression(resourceSet, "sqrt(25) + sin(0) + cos(0)");
            Object result6 = interpreter.evaluate(expr6);
            System.out.println("sqrt(25) + sin(0) + cos(0) = " + result6);

            // Demo 7: Power operations
            System.out.println("\n=== Demo 7: Power Operations ===");
            Expression expr7 = parseExpression(resourceSet, "2 ^ 3 + 3 ^ 2");
            Object result7 = interpreter.evaluate(expr7);
            System.out.println("2 ^ 3 + 3 ^ 2 = " + result7);

            // Demo 8: Complex expression
            System.out.println("\n=== Demo 8: Complex Expression ===");
            interpreter.setVariable("radius", 5);
            Expression expr8 = parseExpression(resourceSet, "PI * radius ^ 2");
            Object result8 = interpreter.evaluate(expr8);
            System.out.println("PI * radius ^ 2 (where radius=5) = " + result8);

            // Demo 9: Conditional with variables
            System.out.println("\n=== Demo 9: Conditional with Variables ===");
            interpreter.setVariable("temperature", 25);
            Expression expr9 = parseExpression(resourceSet, "temperature > 20 ? \"warm\" : \"cold\"");
            Object result9 = interpreter.evaluate(expr9);
            System.out.println("temperature > 20 ? \"warm\" : \"cold\" (where temperature=25) = " + result9);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Expression parseExpression(ResourceSet resourceSet, String input) throws Exception {
        URI uri = URI.createURI("dummy.stoex");
        Resource resource = resourceSet.createResource(uri);
        resource.load(new ByteArrayInputStream(input.getBytes()), null);

        if (!resource.getErrors().isEmpty()) {
            throw new RuntimeException("Parse errors: " + resource.getErrors());
        }

        return (Expression) resource.getContents().get(0);
    }
}
