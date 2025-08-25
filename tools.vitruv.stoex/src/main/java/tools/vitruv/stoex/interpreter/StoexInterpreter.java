package tools.vitruv.stoex.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tools.vitruv.stoex.stoex.AbstractNamedReference;
import tools.vitruv.stoex.stoex.BoolLiteral;
import tools.vitruv.stoex.stoex.BooleanOperatorExpression;
import tools.vitruv.stoex.stoex.BoxedPDF;
import tools.vitruv.stoex.stoex.CompareExpression;
import tools.vitruv.stoex.stoex.ContinuousSample;
import tools.vitruv.stoex.stoex.DoubleLiteral;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.stoex.FunctionLiteral;
import tools.vitruv.stoex.stoex.IfElseExpression;
import tools.vitruv.stoex.stoex.IntLiteral;
import tools.vitruv.stoex.stoex.NamespaceReference;
import tools.vitruv.stoex.stoex.NegativeExpression;
import tools.vitruv.stoex.stoex.NotExpression;
import tools.vitruv.stoex.stoex.Parenthesis;
import tools.vitruv.stoex.stoex.PowerExpression;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.ProbabilityFunction;
import tools.vitruv.stoex.stoex.ProbabilityFunctionLiteral;
import tools.vitruv.stoex.stoex.ProbabilityMassFunction;
import tools.vitruv.stoex.stoex.ProductExpression;
import tools.vitruv.stoex.stoex.StringLiteral;
import tools.vitruv.stoex.stoex.TermExpression;
import tools.vitruv.stoex.stoex.Variable;
import tools.vitruv.stoex.stoex.VariableReference;

/**
 * Interpreter for Stoex expressions.
 * Supports arithmetic, boolean operations, and probability functions.
 */
public class StoexInterpreter {

    private final Map<String, Object> variables = new HashMap<>();
    private final Random random = new Random();

    public StoexInterpreter() {
        // Initialize with some default variables
        variables.put("PI", Math.PI);
        variables.put("E", Math.E);
    }

    public StoexInterpreter(Map<String, Object> initialVariables) {
        this();
        if (initialVariables != null) {
            variables.putAll(initialVariables);
        }
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    /**
     * Evaluates an expression and returns the result.
     */
    public Object evaluate(Expression expression) {
        if (expression == null) {
            return null;
        }

        return evaluateExpression(expression);
    }

    private Object evaluateExpression(Expression expr) {
        if (expr instanceof IntLiteral) {
            return ((IntLiteral) expr).getValue();
        }

        if (expr instanceof DoubleLiteral) {
            return ((DoubleLiteral) expr).getValue();
        }

        if (expr instanceof StringLiteral) {
            return ((StringLiteral) expr).getValue();
        }

        if (expr instanceof BoolLiteral) {
            return ((BoolLiteral) expr).isValue();
        }

        if (expr instanceof Variable) {
            return evaluateVariable((Variable) expr);
        }

        if (expr instanceof TermExpression) {
            return evaluateTermExpression((TermExpression) expr);
        }

        if (expr instanceof ProductExpression) {
            return evaluateProductExpression((ProductExpression) expr);
        }

        if (expr instanceof PowerExpression) {
            return evaluatePowerExpression((PowerExpression) expr);
        }

        if (expr instanceof CompareExpression) {
            return evaluateCompareExpression((CompareExpression) expr);
        }

        if (expr instanceof BooleanOperatorExpression) {
            return evaluateBooleanOperatorExpression((BooleanOperatorExpression) expr);
        }

        if (expr instanceof IfElseExpression) {
            return evaluateIfElseExpression((IfElseExpression) expr);
        }

        if (expr instanceof NegativeExpression) {
            return evaluateNegativeExpression((NegativeExpression) expr);
        }

        if (expr instanceof NotExpression) {
            return evaluateNotExpression((NotExpression) expr);
        }

        if (expr instanceof Parenthesis) {
            return evaluateExpression(((Parenthesis) expr).getInnerExpression());
        }

        if (expr instanceof FunctionLiteral) {
            return evaluateFunctionLiteral((FunctionLiteral) expr);
        }

        if (expr instanceof ProbabilityFunctionLiteral) {
            return evaluateProbabilityFunctionLiteral((ProbabilityFunctionLiteral) expr);
        }

        throw new IllegalArgumentException("Unsupported expression type: " + expr.getClass().getSimpleName());
    }

    private Object evaluateVariable(Variable var) {
        String varName = resolveVariableName(var.getId_Variable());
        Object value = variables.get(varName);
        if (value == null) {
            throw new RuntimeException("Undefined variable: " + varName);
        }
        return value;
    }

    private String resolveVariableName(AbstractNamedReference ref) {
        if (ref instanceof VariableReference) {
            return ((VariableReference) ref).getReferenceName();
        } else if (ref instanceof NamespaceReference) {
            NamespaceReference nsRef = (NamespaceReference) ref;
            return nsRef.getReferenceName() + "." + resolveVariableName(nsRef.getInnerReference_NamespaceReference());
        }
        throw new IllegalArgumentException("Unknown reference type: " + ref.getClass().getSimpleName());
    }

    private Object evaluateTermExpression(TermExpression expr) {
        Object left = evaluateExpression(expr.getLeft());
        Object right = evaluateExpression(expr.getRight());

        double leftVal = convertToDouble(left);
        double rightVal = convertToDouble(right);

        switch (expr.getOperation()) {
            case ADD:
                return leftVal + rightVal;
            case SUB:
                return leftVal - rightVal;
            default:
                throw new IllegalArgumentException("Unknown term operation: " + expr.getOperation());
        }
    }

    private Object evaluateProductExpression(ProductExpression expr) {
        Object left = evaluateExpression(expr.getLeft());
        Object right = evaluateExpression(expr.getRight());

        double leftVal = convertToDouble(left);
        double rightVal = convertToDouble(right);

        switch (expr.getOperation()) {
            case MULT:
                return leftVal * rightVal;
            case DIV:
                if (rightVal == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return leftVal / rightVal;
            case MOD:
                return leftVal % rightVal;
            default:
                throw new IllegalArgumentException("Unknown product operation: " + expr.getOperation());
        }
    }

    private Object evaluatePowerExpression(PowerExpression expr) {
        Object base = evaluateExpression(expr.getBase());
        Object exponent = evaluateExpression(expr.getExponent());

        double baseVal = convertToDouble(base);
        double expVal = convertToDouble(exponent);

        return Math.pow(baseVal, expVal);
    }

    private Object evaluateCompareExpression(CompareExpression expr) {
        Object left = evaluateExpression(expr.getLeft());
        Object right = evaluateExpression(expr.getRight());

        double leftVal = convertToDouble(left);
        double rightVal = convertToDouble(right);

        switch (expr.getOperation()) {
            case GREATER:
                return leftVal > rightVal;
            case LESS:
                return leftVal < rightVal;
            case EQUALS:
                return Math.abs(leftVal - rightVal) < 1e-10; // Handle floating point comparison
            case NOTEQUAL:
                return Math.abs(leftVal - rightVal) >= 1e-10;
            case GREATEREQUAL:
                return leftVal >= rightVal;
            case LESSEQUAL:
                return leftVal <= rightVal;
            default:
                throw new IllegalArgumentException("Unknown compare operation: " + expr.getOperation());
        }
    }

    private Object evaluateBooleanOperatorExpression(BooleanOperatorExpression expr) {
        Object left = evaluateExpression(expr.getLeft());
        Object right = evaluateExpression(expr.getRight());

        boolean leftVal = convertToBoolean(left);
        boolean rightVal = convertToBoolean(right);

        switch (expr.getOperation()) {
            case AND:
                return leftVal && rightVal;
            case OR:
                return leftVal || rightVal;
            case XOR:
                return leftVal ^ rightVal;
            default:
                throw new IllegalArgumentException("Unknown boolean operation: " + expr.getOperation());
        }
    }

    private Object evaluateIfElseExpression(IfElseExpression expr) {
        Object condition = evaluateExpression(expr.getConditionExpression());
        boolean conditionVal = convertToBoolean(condition);

        if (conditionVal) {
            return evaluateExpression(expr.getIfExpression());
        } else {
            return evaluateExpression(expr.getElseExpression());
        }
    }

    private Object evaluateNegativeExpression(NegativeExpression expr) {
        Object inner = evaluateExpression(expr.getInner());
        double innerVal = convertToDouble(inner);
        return -innerVal;
    }

    private Object evaluateNotExpression(NotExpression expr) {
        Object inner = evaluateExpression(expr.getInner());
        boolean innerVal = convertToBoolean(inner);
        return !innerVal;
    }

    private Object evaluateFunctionLiteral(FunctionLiteral func) {
        String functionName = func.getId();

        // Built-in mathematical functions
        switch (functionName.toLowerCase()) {
            case "sin":
                if (func.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("sin() requires exactly 1 parameter");
                }
                double sinArg = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(0)));
                return Math.sin(sinArg);

            case "cos":
                if (func.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("cos() requires exactly 1 parameter");
                }
                double cosArg = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(0)));
                return Math.cos(cosArg);

            case "sqrt":
                if (func.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("sqrt() requires exactly 1 parameter");
                }
                double sqrtArg = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(0)));
                return Math.sqrt(sqrtArg);

            case "abs":
                if (func.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("abs() requires exactly 1 parameter");
                }
                double absArg = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(0)));
                return Math.abs(absArg);

            case "max":
                if (func.getParameters_FunctionLiteral().size() != 2) {
                    throw new IllegalArgumentException("max() requires exactly 2 parameters");
                }
                double max1 = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(0)));
                double max2 = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(1)));
                return Math.max(max1, max2);

            case "min":
                if (func.getParameters_FunctionLiteral().size() != 2) {
                    throw new IllegalArgumentException("min() requires exactly 2 parameters");
                }
                double min1 = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(0)));
                double min2 = convertToDouble(evaluateExpression(func.getParameters_FunctionLiteral().get(1)));
                return Math.min(min1, min2);

            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    private Object evaluateProbabilityFunctionLiteral(ProbabilityFunctionLiteral probFunc) {
        ProbabilityFunction func = probFunc.getFunction_ProbabilityFunctionLiteral();

        if (func instanceof ProbabilityMassFunction) {
            return evaluatePMF((ProbabilityMassFunction) func);
        } else if (func instanceof ProbabilityDensityFunction) {
            return evaluatePDF((ProbabilityDensityFunction) func);
        }

        throw new IllegalArgumentException("Unknown probability function type");
    }

    private Object evaluatePMF(ProbabilityMassFunction pmf) {
        if (pmf instanceof BoxedPDF) {
            // Handle BoxedPDF case (though it should be PDF)
            BoxedPDF boxed = (BoxedPDF) pmf;
            return sampleFromContinuous(boxed.getSamples());
        }

        // For simplicity, return a random sample based on the probabilities
        // In a real implementation, you'd want to properly sample according to the
        // distribution
        return "PMF_SAMPLE_" + random.nextInt(100);
    }

    private Object evaluatePDF(ProbabilityDensityFunction pdf) {
        if (pdf instanceof BoxedPDF) {
            BoxedPDF boxed = (BoxedPDF) pdf;
            return sampleFromContinuous(boxed.getSamples());
        }
        return "PDF_SAMPLE_" + random.nextDouble();
    }

    private Object sampleFromContinuous(org.eclipse.emf.common.util.EList<ContinuousSample> samples) {
        if (samples.isEmpty()) {
            return 0.0;
        }
        // Simple uniform sampling - in reality you'd want proper probability sampling
        ContinuousSample sample = samples.get(random.nextInt(samples.size()));
        return sample.getValue();
    }

    private double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1.0 : 0.0;
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert string to number: " + value);
            }
        }
        throw new IllegalArgumentException("Cannot convert to double: " + value);
    }

    private boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0.0;
        }
        if (value instanceof String) {
            String str = (String) value;
            return "true".equalsIgnoreCase(str) || "1".equals(str);
        }
        throw new IllegalArgumentException("Cannot convert to boolean: " + value);
    }
}
