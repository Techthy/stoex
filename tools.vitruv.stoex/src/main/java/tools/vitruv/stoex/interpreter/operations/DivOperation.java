package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class DivOperation {

    public int evaluate(int left, int right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left / right;
    }

    public double evaluate(double left, double right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left / right;
    }

    public LognormalDistribution evaluate(LognormalDistribution left, LognormalDistribution right) {
        LognormalDistribution result = StoexFactory.eINSTANCE.createLognormalDistribution();
        result.setMu(left.getMu() - right.getMu());
        result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
        return result;
    }

    public SampledDistribution subDistributions(double[] samplesLeft, double[] samplesRight) {

        MonteCarloOperation op = new MonteCarloOperation();
        double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
                ProbabilityFunctionOperations.DIV);

        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : combinedSamples) {
            result.getValues().add(d);
        }
        return result;
    }

    // Fallback that handles String and Boolean as well as the mixture of types
    public Object evaluate(Object left, Object right) {
        if (left instanceof LognormalDistribution leftLog && right instanceof LognormalDistribution rightLog) {
            return evaluate(leftLog, rightLog);
        } else if (left instanceof ProbabilityDensityFunction leftSample
                && right instanceof ProbabilityDensityFunction rightSample) {
            SampleHelper helper = new SampleHelper();
            return subDistributions(helper.getSamples(leftSample), helper.getSamples(rightSample));
        }

        double leftVal = toDouble(left);
        double rightVal = toDouble(right);
        return evaluate(leftVal, rightVal);
    }

    private double toDouble(Object obj) {
        if (obj instanceof Number number) {
            return number.doubleValue();
        } else if (obj instanceof Boolean bool) {
            return bool ? 1.0 : 0.0;
        } else if (obj instanceof String str) {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert String to double: " + str);
            }
        } else {
            throw new IllegalArgumentException("Unsupported type for division: " + obj.getClass().getName());
        }
    }

}
