package tools.vitruv.stoex.interpreter.visitors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.stoex.interpreter.TypeEnum;
import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.BoolLiteral;
import tools.vitruv.stoex.stoex.CompareExpression;
import tools.vitruv.stoex.stoex.DoubleLiteral;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.stoex.IfElseExpression;
import tools.vitruv.stoex.stoex.IntLiteral;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.Parenthesis;
import tools.vitruv.stoex.stoex.PoissonDistribution;
import tools.vitruv.stoex.stoex.PowerExpression;
import tools.vitruv.stoex.stoex.ProbabilityFunctionLiteral;
import tools.vitruv.stoex.stoex.ProductExpression;
import tools.vitruv.stoex.stoex.ProductOperations;
import tools.vitruv.stoex.stoex.StringLiteral;
import tools.vitruv.stoex.stoex.TermExpression;
import tools.vitruv.stoex.stoex.TermOperations;
import tools.vitruv.stoex.stoex.Variable;
import tools.vitruv.stoex.stoex.util.StoexSwitch;

/**
 * Enhanced type inference visitor that supports new distribution types
 */
public class TypeInferenceVisitor extends StoexSwitch<TypeEnum> {

    private final Map<Expression, TypeEnum> typeAnnotations = new HashMap<>();
    private final Map<String, TypeEnum> variableTypes = new HashMap<>();

    // Store and retrieve type annotations like the original
    public void setTypeAnnotation(Expression expr, TypeEnum type) {
        typeAnnotations.put(expr, type);
    }

    public TypeEnum getTypeAnnotation(Expression expr) {
        return typeAnnotations.get(expr);
    }

    // Variable type management
    public void setVariableType(String variableName, TypeEnum type) {
        variableTypes.put(variableName, type);
    }

    public TypeEnum getVariableType(String variableName) {
        return variableTypes.get(variableName);
    }

    public void clearVariableTypes() {
        variableTypes.clear();
    }

    // Override the main doSwitch to store results
    @Override
    public TypeEnum doSwitch(EObject eObject) {
        TypeEnum result = super.doSwitch(eObject);
        if (eObject instanceof Expression && result != null) {
            setTypeAnnotation((Expression) eObject, result);
        }
        return result;
    }

    // Basic literals - same as original
    @Override
    public TypeEnum caseIntLiteral(IntLiteral object) {
        return TypeEnum.INT;
    }

    @Override
    public TypeEnum caseDoubleLiteral(DoubleLiteral object) {
        return TypeEnum.DOUBLE;
    }

    @Override
    public TypeEnum caseBoolLiteral(BoolLiteral object) {
        return TypeEnum.BOOL;
    }

    @Override
    public TypeEnum caseStringLiteral(StringLiteral object) {
        return TypeEnum.STRING;
    }

    @Override
    public TypeEnum caseVariable(Variable object) {
        // Get the variable name from the AbstractNamedReference
        String variableName = getVariableName(object);
        TypeEnum varType = getVariableType(variableName);

        if (varType == null) {
            // If we don't know the variable type, we can't do type inference
            // In StoexEvaluator context, variables are handled at runtime
            return TypeEnum.DOUBLE; // Default assumption for variables
        }

        return varType;
    }

    /**
     * Helper method to extract variable name from Variable object
     */
    private String getVariableName(Variable variable) {
        if (variable.getId_Variable() != null) {
            return variable.getId_Variable().getReferenceName();
        }
        return null;
    }

    @Override
    public TypeEnum caseProbabilityFunctionLiteral(ProbabilityFunctionLiteral object) {
        // Delegate to the actual probability function inside
        return doSwitch(object.getFunction_ProbabilityFunctionLiteral());
    }

    @Override
    public TypeEnum caseParenthesis(Parenthesis object) {
        // Delegate to the inner expression
        return doSwitch(object.getInnerExpression());
    }

    // TODO: fix all error handling (exceptions should be custom!)
    @Override
    public TypeEnum caseBernoulliDistribution(BernoulliDistribution object) {
        // Validate parameter type
        // TypeEnum paramType = doSwitch(object.getP());
        // if (!isNumeric(paramType)) {
        // throw new RuntimeException("Bernoulli parameter must be numeric, got: " +
        // paramType);
        // throw new TypeMismatchException("Bernoulli parameter must be numeric, got: "
        // + paramType);
        // }
        return TypeEnum.BERNOULLI_PMF;
    }

    @Override
    public TypeEnum caseBinomialDistribution(BinomialDistribution object) {
        // TypeEnum nType = doSwitch(object.getN());
        // TypeEnum pType = doSwitch(object.getP());

        // if (!isInteger(nType)) {
        // throw new RuntimeException("Binomial n parameter must be integer, got: " +
        // nType);
        // }
        // if (!isNumeric(pType)) {
        // throw new RuntimeException("Binomial p parameter must be numeric, got: " +
        // pType);
        // }
        return TypeEnum.BINOMIAL_PMF;
    }

    @Override
    public TypeEnum casePoissonDistribution(PoissonDistribution object) {
        // Validate parameter type
        // TypeEnum lambdaType = doSwitch(object.getLambda());
        // if (!isNumeric(lambdaType)) {
        // throw new RuntimeException("Poisson lambda must be numeric, got: " +
        // lambdaType);
        // }
        return TypeEnum.POISSON_PMF;
    }

    @Override
    public TypeEnum caseNormalDistribution(NormalDistribution object) {
        // TypeEnum muType = doSwitch(object.getMu());
        // TypeEnum sigmaType = doSwitch(object.getSigma());

        // if (!isNumeric(muType) || !isNumeric(sigmaType)) {
        // throw new RuntimeException("Normal parameters must be numeric");
        // }
        return TypeEnum.NORMAL_PDF;
    }

    @Override
    public TypeEnum caseExponentialDistribution(ExponentialDistribution object) {
        // TypeEnum rateType = doSwitch(object.getRate());
        // if (!isNumeric(rateType)) {
        // throw new RuntimeException("Exponential rate must be numeric, got: " +
        // rateType);
        // }
        return TypeEnum.EXPONENTIAL_PDF;
    }

    // Arithmetic operations - enhanced with distribution support
    @Override
    public TypeEnum caseTermExpression(TermExpression object) {
        TypeEnum leftType = doSwitch(object.getLeft());
        TypeEnum rightType = doSwitch(object.getRight());

        return inferArithmeticType(leftType, rightType, object.getOperation());
    }

    @Override
    public TypeEnum caseProductExpression(ProductExpression object) {
        TypeEnum leftType = doSwitch(object.getLeft());
        TypeEnum rightType = doSwitch(object.getRight());

        return inferProductArithmeticType(leftType, rightType, object.getOperation());
    }

    @Override
    public TypeEnum casePowerExpression(PowerExpression object) {
        TypeEnum baseType = doSwitch(object.getBase());
        TypeEnum exponentType = doSwitch(object.getExponent());

        // Power operations with distributions
        if (isDistribution(baseType)) {
            if (!isNumeric(exponentType)) {
                throw new RuntimeException("Distribution exponent must be numeric");
            }
            return baseType; // Keep the distribution type
        }

        // Regular numeric power
        if (isNumeric(baseType) && isNumeric(exponentType)) {
            return TypeEnum.DOUBLE; // Power usually results in double
        }

        throw new RuntimeException("Invalid power operation: " + baseType + " ^ " + exponentType);
    }

    @Override
    public TypeEnum caseCompareExpression(CompareExpression object) {
        TypeEnum leftType = doSwitch(object.getLeft());
        TypeEnum rightType = doSwitch(object.getRight());

        // Comparisons with distributions result in boolean PMF
        if (isDistribution(leftType) || isDistribution(rightType)) {
            return TypeEnum.BOOL_PMF;
        }

        // Regular comparisons result in boolean
        return TypeEnum.BOOL;
    }

    @Override
    public TypeEnum caseIfElseExpression(IfElseExpression object) {
        TypeEnum conditionType = doSwitch(object.getConditionExpression());
        TypeEnum ifType = doSwitch(object.getIfExpression());
        TypeEnum elseType = doSwitch(object.getElseExpression());

        // Condition with distribution creates uncertainty
        if (isDistribution(conditionType)) {
            return promoteTypes(ifType, elseType);
        }

        // Regular if-else
        if (conditionType != TypeEnum.BOOL) {
            throw new RuntimeException("If condition must be boolean, got: " + conditionType);
        }

        return promoteTypes(ifType, elseType);
    }

    // Type inference helper methods
    private TypeEnum inferArithmeticType(TypeEnum left, TypeEnum right, TermOperations operation) {
        // PMF + PMF = PMF (via convolution)
        if (isDiscrete(left) && isDiscrete(right)) {
            return TypeEnum.DOUBLE_PMF;
        }

        // PDF + PDF = PDF (via convolution)
        if (isContinuous(left) && isContinuous(right)) {
            return TypeEnum.DOUBLE_PDF;
        }

        // Distribution + constant = same distribution type (domain shift)
        if (isDistribution(left) && isNumeric(right)) {
            return left;
        }
        if (isNumeric(left) && isDistribution(right)) {
            return right;
        }

        // Mixed PMF + PDF = PDF (discretization)
        if ((isDiscrete(left) && isContinuous(right)) ||
                (isContinuous(left) && isDiscrete(right))) {
            return TypeEnum.DOUBLE_PDF;
        }

        // Basic numeric operations
        if (isNumeric(left) && isNumeric(right)) {
            if (left == TypeEnum.DOUBLE || right == TypeEnum.DOUBLE) {
                return TypeEnum.DOUBLE;
            }
            return TypeEnum.INT;
        }

        throw new RuntimeException("Cannot perform " + operation + " on types " + left + " and " + right);
    }

    // Overload for ProductOperations
    private TypeEnum inferProductArithmeticType(TypeEnum left, TypeEnum right, ProductOperations operation) {
        // PMF * PMF = PMF (via convolution)
        if (isDiscrete(left) && isDiscrete(right)) {
            return TypeEnum.DOUBLE_PMF;
        }

        // PDF * PDF = PDF (via convolution)
        if (isContinuous(left) && isContinuous(right)) {
            return TypeEnum.DOUBLE_PDF;
        }

        // Distribution * constant = same distribution type (scaling)
        if (isDistribution(left) && isNumeric(right)) {
            return left;
        }
        if (isNumeric(left) && isDistribution(right)) {
            return right;
        }

        // Mixed PMF * PDF = PDF (discretization)
        if ((isDiscrete(left) && isContinuous(right)) ||
                (isContinuous(left) && isDiscrete(right))) {
            return TypeEnum.DOUBLE_PDF;
        }

        // Basic numeric operations
        if (isNumeric(left) && isNumeric(right)) {
            if (left == TypeEnum.DOUBLE || right == TypeEnum.DOUBLE) {
                return TypeEnum.DOUBLE;
            }
            return TypeEnum.INT;
        }

        throw new RuntimeException("Cannot perform " + operation + " on types " + left + " and " + right);
    }

    private TypeEnum promoteTypes(TypeEnum type1, TypeEnum type2) {
        if (type1 == type2)
            return type1;

        // Distribution promotion rules
        if (isDistribution(type1) || isDistribution(type2)) {
            if (isContinuous(type1) || isContinuous(type2)) {
                return TypeEnum.DOUBLE_PDF;
            }
            return TypeEnum.DOUBLE_PMF;
        }

        // Numeric promotion
        if (isNumeric(type1) && isNumeric(type2)) {
            if (type1 == TypeEnum.DOUBLE || type2 == TypeEnum.DOUBLE) {
                return TypeEnum.DOUBLE;
            }
            return TypeEnum.INT;
        }

        throw new RuntimeException("Cannot promote incompatible types: " + type1 + " and " + type2);
    }

    // Type checking utilities
    private boolean isNumeric(TypeEnum type) {
        return type == TypeEnum.INT || type == TypeEnum.DOUBLE;
    }

    private boolean isInteger(TypeEnum type) {
        return type == TypeEnum.INT;
    }

    private boolean isDistribution(TypeEnum type) {
        return isDiscrete(type) || isContinuous(type);
    }

    private boolean isDiscrete(TypeEnum type) {
        return type == TypeEnum.BERNOULLI_PMF ||
                type == TypeEnum.BINOMIAL_PMF ||
                type == TypeEnum.POISSON_PMF ||
                type == TypeEnum.INT_PMF ||
                type == TypeEnum.DOUBLE_PMF ||
                type == TypeEnum.BOOL_PMF ||
                type == TypeEnum.ENUM_PMF;
    }

    private boolean isContinuous(TypeEnum type) {
        return type == TypeEnum.NORMAL_PDF ||
                type == TypeEnum.EXPONENTIAL_PDF ||
                type == TypeEnum.GAMMA_PDF ||
                type == TypeEnum.LOGNORMAL_PDF ||
                type == TypeEnum.UNIFORM_PDF ||
                type == TypeEnum.DOUBLE_PDF;
    }
}