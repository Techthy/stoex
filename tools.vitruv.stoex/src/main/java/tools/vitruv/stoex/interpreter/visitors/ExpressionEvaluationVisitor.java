package tools.vitruv.stoex.interpreter.visitors;

import java.util.HashMap;
import java.util.Map;

import tools.vitruv.stoex.interpreter.StoexEvaluator;
import tools.vitruv.stoex.interpreter.operations.AddOperation;
import tools.vitruv.stoex.interpreter.operations.Dispatcher;
import tools.vitruv.stoex.interpreter.operations.DivOperation;
import tools.vitruv.stoex.interpreter.operations.ModOperation;
import tools.vitruv.stoex.interpreter.operations.MultOperation;
import tools.vitruv.stoex.interpreter.operations.PowerOperator;
import tools.vitruv.stoex.interpreter.operations.SubOperation;
import tools.vitruv.stoex.stoex.AbstractNamedReference;
import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.BoolLiteral;
import tools.vitruv.stoex.stoex.BooleanOperatorExpression;
import tools.vitruv.stoex.stoex.CompareExpression;
import tools.vitruv.stoex.stoex.CompareOperations;
import tools.vitruv.stoex.stoex.DiscreteUniformDistribution;
import tools.vitruv.stoex.stoex.DoubleLiteral;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.stoex.FunctionLiteral;
import tools.vitruv.stoex.stoex.IfElseExpression;
import tools.vitruv.stoex.stoex.IntLiteral;
import tools.vitruv.stoex.stoex.NamespaceReference;
import tools.vitruv.stoex.stoex.NegativeExpression;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.NotExpression;
import tools.vitruv.stoex.stoex.Parenthesis;
import tools.vitruv.stoex.stoex.PoissonDistribution;
import tools.vitruv.stoex.stoex.PowerExpression;
import tools.vitruv.stoex.stoex.ProbabilityFunction;
import tools.vitruv.stoex.stoex.ProductExpression;
import tools.vitruv.stoex.stoex.ProductOperations;
import tools.vitruv.stoex.stoex.StringLiteral;
import tools.vitruv.stoex.stoex.TermExpression;
import tools.vitruv.stoex.stoex.TermOperations;
import tools.vitruv.stoex.stoex.Variable;
import tools.vitruv.stoex.stoex.VariableReference;
import tools.vitruv.stoex.stoex.util.StoexSwitch;

/**
 * Enhanced expression evaluator that computes actual results
 */
public class ExpressionEvaluationVisitor extends StoexSwitch<Object> {

    private final Map<String, Object> variableValues = new HashMap<>();

    public ExpressionEvaluationVisitor() {
        // Initialize with common constants
        variableValues.put("PI", Math.PI);
        variableValues.put("E", Math.E);
    }

    // Set variable values for evaluation
    public void setVariable(String name, Object value) {
        variableValues.put(name, value);
    }

    public Object getVariable(String name) {
        return variableValues.get(name);
    }

    // Basic literals - return their values
    @Override
    public Object caseIntLiteral(IntLiteral object) {
        return object.getValue();
    }

    @Override
    public Object caseDoubleLiteral(DoubleLiteral object) {
        return object.getValue();
    }

    @Override
    public Object caseBoolLiteral(BoolLiteral object) {
        return object.isValue();
    }

    @Override
    public Object caseStringLiteral(StringLiteral object) {
        return object.getValue();
    }

    // Variables - lookup their values
    @Override
    public Object caseVariable(Variable object) {
        String varName = resolveVariableName(object.getId_Variable());
        Object value = variableValues.get(varName);
        if (value == null) {
            throw new RuntimeException("Undefined variable: " + varName);
        }

        if (value instanceof Number || value instanceof Boolean) {
            return value;
        }

        // Check if Variable already is an Expression
        if (value instanceof Expression) {
            return value;
        }

        StoexEvaluator evaluator = new StoexEvaluator();
        return evaluator.evaluate((String) value);
    }

    private String resolveVariableName(AbstractNamedReference ref) {
        if (ref instanceof VariableReference variableReference) {
            return variableReference.getReferenceName();
        } else if (ref instanceof NamespaceReference nsRef) {
            return nsRef.getReferenceName() + "." + resolveVariableName(nsRef.getInnerReference_NamespaceReference());
        }
        throw new IllegalArgumentException("Unknown reference type: " + ref.getClass().getSimpleName());
    }

    @Override
    public Object caseParenthesis(Parenthesis object) {
        return doSwitch(object.getInnerExpression());
    }

    @Override
    public Object caseProbabilityFunction(ProbabilityFunction object) {
        return object;
    }

    @Override
    public Object caseBernoulliDistribution(BernoulliDistribution object) {
        return object;
    }

    @Override
    public Object caseBinomialDistribution(BinomialDistribution object) {
        return object;
    }

    @Override
    public Object casePoissonDistribution(PoissonDistribution object) {
        return object;
    }

    @Override
    public Object caseDiscreteUniformDistribution(DiscreteUniformDistribution object) {
        return object;
    }

    @Override
    public Object caseNormalDistribution(NormalDistribution object) {

        double sigma = object.getSigma();

        if (sigma <= 0)
            throw new IllegalArgumentException("Normal sigma must be positive");

        return object;
    }

    @Override
    public Object caseExponentialDistribution(ExponentialDistribution object) {
        double rate = object.getLambda();

        if (rate <= 0)
            throw new IllegalArgumentException("Exponential rate must be positive");

        return object;

    }

    // Arithmetic operations
    @Override
    public Object caseTermExpression(TermExpression object) {
        Object leftValue = doSwitch(object.getLeft());
        Object rightValue = doSwitch(object.getRight());
        TermOperations operation = object.getOperation();

        return evaluateTermOperation(leftValue, rightValue, operation);
    }

    @Override
    public Object caseProductExpression(ProductExpression object) {
        Object leftValue = doSwitch(object.getLeft());
        Object rightValue = doSwitch(object.getRight());
        ProductOperations operation = object.getOperation();

        return evaluateProductOperation(leftValue, rightValue, operation);
    }

    @Override
    public Object casePowerExpression(PowerExpression object) {
        Object baseValue = doSwitch(object.getBase());
        Object exponentValue = doSwitch(object.getExponent());

        PowerOperator powerOp = new PowerOperator();
        return powerOp.evaluate(baseValue, exponentValue);
    }

    @Override
    public Object caseCompareExpression(CompareExpression object) {
        Object leftValue = doSwitch(object.getLeft());
        Object rightValue = doSwitch(object.getRight());
        CompareOperations operation = object.getOperation();

        return evaluateCompareOperation(leftValue, rightValue, operation);
    }

    @Override
    public Object caseBooleanOperatorExpression(BooleanOperatorExpression object) {
        Object leftValue = doSwitch(object.getLeft());
        Object rightValue = doSwitch(object.getRight());

        boolean left = toBoolean(leftValue);
        boolean right = toBoolean(rightValue);

        switch (object.getOperation()) {
            case AND -> {
                return left && right;
            }
            case OR -> {
                return left || right;
            }
            case XOR -> {
                return left ^ right;
            }
            default -> throw new UnsupportedOperationException("Unknown boolean operation: " + object.getOperation());
        }
    }

    @Override
    public Object caseIfElseExpression(IfElseExpression object) {
        Object conditionValue = doSwitch(object.getConditionExpression());

        // Regular boolean condition
        boolean condition = toBoolean(conditionValue);
        if (condition) {
            return doSwitch(object.getIfExpression());
        } else {
            return doSwitch(object.getElseExpression());
        }
    }

    @Override
    public Object caseNegativeExpression(NegativeExpression object) {
        Object value = doSwitch(object.getInner());
        return -toDouble(value);
    }

    @Override
    public Object caseNotExpression(NotExpression object) {
        Object value = doSwitch(object.getInner());
        return !toBoolean(value);
    }

    @Override
    public Object caseFunctionLiteral(FunctionLiteral object) {
        String functionName = object.getId();

        switch (functionName.toLowerCase()) {
            case "sin":
                if (object.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("sin() requires exactly 1 parameter");
                }
                double sinArg = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(0)));
                return Math.sin(sinArg);

            case "cos":
                if (object.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("cos() requires exactly 1 parameter");
                }
                double cosArg = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(0)));
                return Math.cos(cosArg);

            case "sqrt":
                if (object.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("sqrt() requires exactly 1 parameter");
                }
                double sqrtArg = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(0)));
                return Math.sqrt(sqrtArg);

            case "abs":
                if (object.getParameters_FunctionLiteral().size() != 1) {
                    throw new IllegalArgumentException("abs() requires exactly 1 parameter");
                }
                double absArg = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(0)));
                return Math.abs(absArg);

            case "max":
                if (object.getParameters_FunctionLiteral().size() != 2) {
                    throw new IllegalArgumentException("max() requires exactly 2 parameters");
                }
                double max1 = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(0)));
                double max2 = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(1)));
                return Math.max(max1, max2);

            case "min":
                if (object.getParameters_FunctionLiteral().size() != 2) {
                    throw new IllegalArgumentException("min() requires exactly 2 parameters");
                }
                double min1 = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(0)));
                double min2 = toDouble(doSwitch(object.getParameters_FunctionLiteral().get(1)));
                return Math.min(min1, min2);

            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    // Operation evaluation methods
    private Object evaluateTermOperation(Object left, Object right, TermOperations operation) {
        switch (operation) {
            case ADD -> {
                Dispatcher dispatcher = new Dispatcher(new AddOperation());
                return dispatcher.dispatch(left, right);
            }
            case SUB -> {
                SubOperation subOp = new SubOperation();
                return subOp.evaluate(left, right);
            }
            default -> throw new UnsupportedOperationException("Unknown term operation: " + operation);
        }
    }

    private Object evaluateProductOperation(Object left, Object right, ProductOperations operation) {
        switch (operation) {
            case MULT -> {
                Dispatcher dispatcher = new Dispatcher(new MultOperation());
                return dispatcher.dispatch(left, right);
            }
            case DIV -> {
                Dispatcher dispatcher = new Dispatcher(new DivOperation());
                return dispatcher.dispatch(left, right);
            }
            case MOD -> {
                ModOperation modOp = new ModOperation();
                return modOp.evaluate(left, right);
            }
            default -> throw new UnsupportedOperationException("Unknown product operation: " + operation);
        }
    }

    private Object evaluateCompareOperation(Object left, Object right, CompareOperations operation) {
        double leftVal = toDouble(left);
        double rightVal = toDouble(right);

        switch (operation) {
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
                throw new UnsupportedOperationException("Unknown comparison operation: " + operation);
        }
    }

    // Type conversion utilities
    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof Boolean aBoolean) {
            return aBoolean ? 1.0 : 0.0;
        }
        if (value instanceof String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert string to double: " + value);
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to double");
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean aBoolean) {
            return aBoolean;
        }
        if (value instanceof Number number) {
            return number.doubleValue() != 0.0;
        }
        if (value instanceof String str) {
            return "true".equalsIgnoreCase(str) || "1".equals(str);
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to boolean");
    }

}
