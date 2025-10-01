package tools.vitruv.stoex.interpreter;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Injector;

import tools.vitruv.stoex.StoexStandaloneSetup;
import tools.vitruv.stoex.interpreter.visitors.ExpressionEvaluationVisitor;
import tools.vitruv.stoex.interpreter.visitors.ExpressionMeanVisitor;
import tools.vitruv.stoex.stoex.Expression;

/**
 * High-level integration class for evaluating Stoex expressions.
 * Combines parsing and evaluation in a simple API.
 */
public class StoexEvaluator {

    private final ExpressionEvaluationVisitor evaluator;
    private final ResourceSet resourceSet;
    private final ISerializer serializer;
    private int expressionCounter = 0;

    public StoexEvaluator() {
        // Initialize Xtext for parsing
        Injector injector = new StoexStandaloneSetup().createInjectorAndDoEMFRegistration();
        this.resourceSet = injector.getInstance(XtextResourceSet.class);
        this.serializer = injector.getInstance(ISerializer.class);

        // Initialize visitors
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
     * TODO always return Expression if possible (for serialization)
     */
    public Object evaluate(String expressionString, Map<String, Object> variables) {
        try {
            // 1. Parse the expression
            Expression expr = parseExpression(expressionString);

            // 2. Set variables in the evaluator and type context
            for (Map.Entry<String, Object> var : variables.entrySet()) {
                evaluator.setVariable(var.getKey(), var.getValue());
            }

            // 3. Evaluate the expression
            Object result = evaluator.doSwitch(expr);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate expression: " + expressionString, e);
        }
    }

    public String serialize(Object expression) {
        if (expression instanceof Expression expr) {
            // Use Xtext's serializer to convert the Expression AST back to a string
            return serializer.serialize(expr);
        } else {
            throw new IllegalArgumentException("Can only serialize Stoex Expressions.");
        }
    }

    /**
     * Set a variable that will be available for all future evaluations.
     */
    public void setVariable(String name, Object value) {
        evaluator.setVariable(name, value);
    }

    /**
     * Get a variable value.
     */
    public Object getVariable(String name) {
        return evaluator.getVariable(name);
    }

    public Number getMean(Expression expression) {
        ExpressionMeanVisitor meanVisitor = new ExpressionMeanVisitor();
        try {
            return (Number) meanVisitor.doSwitch(expression);
        } catch (ClassCastException e) {
            throw new RuntimeException("Failed to convert mean result to Number.", e);
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