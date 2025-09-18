package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.ProbabilityFunction;

/**
 * Implements the operation "modulo" for different kinds of operands.
 * Note, that so far only integers and doubles are supported.
 * 
 * @author Hammann
 */
public class ModOperation {

    public double compute(double left, double right) {
        return left % right;
    }

    public int compute(int left, int right) {
        return left % right;
    }

    public Object compute(Object left, Object right) {
        if (left instanceof ProbabilityFunction || right instanceof ProbabilityFunction) {
            throw new IllegalArgumentException("Modulo operation is not defined for ProbabilityFunctions.");
        }
        return compute(toDouble(left), toDouble(right));
    }

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

}
