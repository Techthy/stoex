package tools.vitruv.stoex.interpreter.visitors;

import java.util.HashMap;
import java.util.Map;

import tools.vitruv.stoex.stoex.AbstractNamedReference;
import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.BoolLiteral;
import tools.vitruv.stoex.stoex.BooleanOperatorExpression;
import tools.vitruv.stoex.stoex.CompareExpression;
import tools.vitruv.stoex.stoex.DiscreteUniformDistribution;
import tools.vitruv.stoex.stoex.DoubleLiteral;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
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
import tools.vitruv.stoex.stoex.StringLiteral;
import tools.vitruv.stoex.stoex.TermExpression;
import tools.vitruv.stoex.stoex.Variable;
import tools.vitruv.stoex.stoex.VariableReference;
import tools.vitruv.stoex.stoex.util.StoexSwitch;

/**
 * Expression visitor that returns mean values for probability distributions
 * and throws exceptions for any calculation operations
 */
public class ExpressionMeanVisitor extends StoexSwitch<Object> {

    private final Map<String, Object> variableValues = new HashMap<>();

    // Set variable values for evaluation
    public void setVariable(String name, Object value) {
        variableValues.put(name, value);
    }

    public Object getVariable(String name) {
        return variableValues.get(name);
    }

    // Basic literals - return their values as-is
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
        return value;
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

    // Probability distributions - return their mean values
    @Override
    public Object caseBernoulliDistribution(BernoulliDistribution object) {
        // Mean of Bernoulli distribution is p
        return object.getP();
    }

    @Override
    public Object caseBinomialDistribution(BinomialDistribution object) {
        // Mean of Binomial distribution is n * p
        return object.getN() * object.getP();
    }

    @Override
    public Object casePoissonDistribution(PoissonDistribution object) {
        // Mean of Poisson distribution is lambda
        return object.getLambda();
    }

    @Override
    public Object caseDiscreteUniformDistribution(DiscreteUniformDistribution object) {
        // Mean of discrete uniform distribution is (a + b) / 2
        return (object.getA() + object.getB()) / 2.0;
    }

    @Override
    public Object caseNormalDistribution(NormalDistribution object) {
        // Mean of Normal distribution is mu
        return object.getMu();
    }

    @Override
    public Object caseExponentialDistribution(ExponentialDistribution object) {
        // Mean of Exponential distribution is 1 / lambda
        return 1.0 / object.getLambda();
    }

    // Arithmetic operations - throw exceptions
    @Override
    public Object caseTermExpression(TermExpression object) {
        throw new UnsupportedOperationException(
                "Arithmetic operations are not supported in mean visitor. Term operation: " + object.getOperation());
    }

    @Override
    public Object caseProductExpression(ProductExpression object) {
        throw new UnsupportedOperationException(
                "Arithmetic operations are not supported in mean visitor. Product operation: " + object.getOperation());
    }

    @Override
    public Object casePowerExpression(PowerExpression object) {
        throw new UnsupportedOperationException("Power operations are not supported in mean visitor");
    }

    @Override
    public Object caseCompareExpression(CompareExpression object) {
        throw new UnsupportedOperationException(
                "Comparison operations are not supported in mean visitor. Compare operation: " + object.getOperation());
    }

    @Override
    public Object caseBooleanOperatorExpression(BooleanOperatorExpression object) {
        throw new UnsupportedOperationException(
                "Boolean operations are not supported in mean visitor. Boolean operation: " + object.getOperation());
    }

    @Override
    public Object caseIfElseExpression(IfElseExpression object) {
        throw new UnsupportedOperationException("Conditional expressions are not supported in mean visitor");
    }

    @Override
    public Object caseNegativeExpression(NegativeExpression object) {
        throw new UnsupportedOperationException("Negative expressions are not supported in mean visitor");
    }

    @Override
    public Object caseNotExpression(NotExpression object) {
        throw new UnsupportedOperationException("Not expressions are not supported in mean visitor");
    }

    @Override
    public Object caseFunctionLiteral(FunctionLiteral object) {
        throw new UnsupportedOperationException(
                "Function calls are not supported in mean visitor. Function: " + object.getId());
    }
}
