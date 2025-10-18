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
import tools.vitruv.stoex.stoex.BoolLiteral;
import tools.vitruv.stoex.stoex.DoubleLiteral;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.stoex.IntLiteral;
import tools.vitruv.stoex.stoex.StoexFactory;
import tools.vitruv.stoex.stoex.StringLiteral;

/**
 * This class serves as an interface to evaluate Stoex expressions.
 * 
 * There are two ways to pass an expression to the evaluator:
 * 1. As a string, which will be parsed into an AST.
 * 2. As an already parsed Expression AST. (instantiated java object of type
 * Expression)
 * 
 * The evaluator then always returns the result of the evaluation as an
 * Expression.
 * Besides a simple evaluation, the evaluator also supports computing the mean
 * value of an expression.
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

    public void setVariable(String name, Object value) {
        evaluator.setVariable(name, value);
    }

    public Object getVariable(String name) {
        return evaluator.getVariable(name);
    }

    /**
     * Main interface to evaluate a Stoex expression given as a string with
     * variables.
     * 
     * @param expressionString The Stoex expression as a string.
     * @param variables        A map of variable names to their values.
     * @return The evaluated expression as an Expression object.
     */
    public Expression evaluate(String expressionString, Map<String, Object> variables) {
        try {
            // 1. Parse the expression
            Expression expr = parseExpression(expressionString);

            // 2. Set variables in the evaluator and type context
            for (Map.Entry<String, Object> var : variables.entrySet()) {
                evaluator.setVariable(var.getKey(), var.getValue());
            }

            // 3. Evaluate the expression
            Object result = evaluator.doSwitch(expr);

            // 4. Return the result as an Expression
            return wrapResult(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate expression: " + expressionString, e);
        }
    }

    /**
     * Evaluate option without variables.
     * 
     * @param expressionString The Stoex expression as a string.
     * @return The evaluated expression as an Expression object.
     */
    public Expression evaluate(String expressionString) {
        return evaluate(expressionString, new HashMap<>());
    }

    /**
     * Main interface to evaluate a Stoex expression given as an Expression AST with
     * variables.
     * 
     * @param expression The Stoex expression as an Expression object.
     * @param variables  A map of variable names to their values.
     * @return The evaluated expression as an Expression object.
     */
    public Expression evaluate(Expression expression, Map<String, Object> variables) {

        try {
            // 1. Set variables in the evaluator
            for (Map.Entry<String, Object> var : variables.entrySet()) {
                evaluator.setVariable(var.getKey(), var.getValue());
            }

            // 2. Evaluate the expression
            Object result = evaluator.doSwitch(expression);

            // 3. Return the result as an Expression
            return wrapResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate expression.", e);
        }

    }

    /**
     * Evaluate option without variables.
     * 
     * @param expression The Stoex expression as an Expression object.
     * @return The evaluated expression as an Expression object.
     */
    public Expression evaluate(Expression expression) {
        return evaluate(expression, new HashMap<>());
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
     * Utility to serialize an Expression AST back to a string.
     * (the string will then be a valid expression in the Stoex language)
     */
    public String serialize(Object expression) {
        if (expression instanceof Expression expr) {
            // Use Xtext's serializer to convert the Expression AST back to a string
            return serializer.serialize(expr);
        } else {
            throw new IllegalArgumentException("Can only serialize Stoex Expressions.");
        }
    }

    private Expression wrapResult(Object result) {
        if (result instanceof Expression expr) {
            return expr;
        } else if (result instanceof Integer integer) {
            IntLiteral intLiteral = StoexFactory.eINSTANCE.createIntLiteral();
            intLiteral.setValue(integer);
            return intLiteral;
        } else if (result instanceof Number number) {
            DoubleLiteral doubleLiteral = StoexFactory.eINSTANCE.createDoubleLiteral();
            doubleLiteral.setValue(number.doubleValue());
            return doubleLiteral;
        } else if (result instanceof Boolean bool) {
            BoolLiteral booleanLiteral = StoexFactory.eINSTANCE.createBoolLiteral();
            booleanLiteral.setValue(bool);
            return booleanLiteral;
        } else if (result instanceof String str) {
            StringLiteral stringLiteral = StoexFactory.eINSTANCE.createStringLiteral();
            stringLiteral.setValue(str);
            return stringLiteral;
        } else {
            throw new RuntimeException("Unsupported evaluation result type: " + result.getClass());
        }
    }

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