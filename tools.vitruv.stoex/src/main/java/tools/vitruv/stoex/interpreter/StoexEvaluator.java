package tools.vitruv.stoex.interpreter;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import tools.vitruv.stoex.StoexStandaloneSetup;
import tools.vitruv.stoex.interpreter.TypeEnum;
import tools.vitruv.stoex.interpreter.visitors.ExpressionEvaluationVisitor;
import tools.vitruv.stoex.interpreter.visitors.TypeInferenceVisitor;
import tools.vitruv.stoex.stoex.Expression;

/**
 * High-level integration class for evaluating Stoex expressions.
 * Combines parsing, type inference, and evaluation in a simple API.
 */
public class StoexEvaluator {

    private final TypeInferenceVisitor typeInference;
    private final ExpressionEvaluationVisitor evaluator;
    private final ResourceSet resourceSet;
    private int expressionCounter = 0;

    public StoexEvaluator() {
        // Initialize Xtext for parsing
        Injector injector = new StoexStandaloneSetup().createInjectorAndDoEMFRegistration();
        this.resourceSet = injector.getInstance(XtextResourceSet.class);

        // Initialize visitors
        this.typeInference = new TypeInferenceVisitor();
        this.evaluator = new ExpressionEvaluationVisitor();
    }

    /**
     * Evaluate an expression with no variables.
     */
    public Object evaluate(String expressionString) {
        return evaluate(expressionString, new HashMap<>());
    }

    /**
     * Evaluate an expression with the given variables.
     */
    public Object evaluate(String expressionString, Map<String, Object> variables) {
        try {
            // 1. Parse the expression
            Expression expr = parseExpression(expressionString);

            // 2. Set variables in the evaluator and type context
            for (Map.Entry<String, Object> var : variables.entrySet()) {
                evaluator.setVariable(var.getKey(), var.getValue());
                // Set the variable type in the type inference visitor
                TypeEnum varType = inferTypeFromValue(var.getValue());
                typeInference.setVariableType(var.getKey(), varType);
            }

            // 3. Try type inference (will work now with variable context)
            try {
                typeInference.doSwitch(expr);
            } catch (Exception e) {
                // If type inference fails, we can still proceed with evaluation
                System.err.println("Type inference warning: " + e.getMessage());
            }

            // 4. Evaluate the expression
            Object result = evaluator.doSwitch(expr);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate expression: " + expressionString, e);
        }
    }

    /**
     * Infer the type of an expression without evaluating it.
     * Note: This may fail for expressions with undefined variables.
     */
    public TypeEnum inferType(String expressionString) {
        try {
            Expression expr = parseExpression(expressionString);
            return typeInference.doSwitch(expr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to infer type for expression: " + expressionString, e);
        }
    }

    /**
     * Set a variable that will be available for all future evaluations.
     */
    public void setVariable(String name, Object value) {
        evaluator.setVariable(name, value);
        // Also update the type inference context
        TypeEnum varType = inferTypeFromValue(value);
        typeInference.setVariableType(name, varType);
    }

    /**
     * Get a variable value.
     */
    public Object getVariable(String name) {
        return evaluator.getVariable(name);
    }

    /**
     * Helper method to infer type from runtime value.
     */
    private TypeEnum inferTypeFromValue(Object value) {
        if (value instanceof Integer) {
            return TypeEnum.INT;
        } else if (value instanceof Double || value instanceof Float) {
            return TypeEnum.DOUBLE;
        } else if (value instanceof Boolean) {
            return TypeEnum.BOOL;
        } else if (value instanceof String) {
            return TypeEnum.STRING;
        } else {
            return TypeEnum.DOUBLE; // Default fallback
        }
    }

    /**
     * Parse an expression string into an AST.
     */
    private Expression parseExpression(String input) throws Exception {
        // Create unique URI to avoid conflicts
        URI uri = URI.createURI("expression" + (++expressionCounter) + ".stoex");
        Resource resource = resourceSet.createResource(uri);
        resource.load(new ByteArrayInputStream(input.getBytes()), null);

        if (!resource.getErrors().isEmpty()) {
            throw new RuntimeException("Parse errors: " + resource.getErrors());
        }

        return (Expression) resource.getContents().get(0);
    }
}