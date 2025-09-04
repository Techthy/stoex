package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;
import tools.vitruv.stoex.stoex.TermOperations;

/**
 * Implements the operation "subtraction" for different kinds of operands.
 *
 * closed form solutions exist for NormalDistribution, ExponentialDistribution,
 * and GammaDistribution
 *
 */
public class SubOperation {

    public int evaluate(int left, int right) {
        return left - right;
    }

    public double evaluate(double left, double right) {
        return left - right;
    }

    public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() - right.getMu());
        result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
        return result;

    }

    public SampledDistribution addDistributions(double[] samplesLeft, double[] samplesRight) {

        MonteCarloOperation op = new MonteCarloOperation();
        double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000, TermOperations.ADD);

        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : combinedSamples) {
            result.getValues().add(d);
        }
        return result;
    }

    // Fallback that handles String and Boolean as well as the mixture of types
    public Object evaluate(Object left, Object right) {

        if (left instanceof NormalDistribution leftNorm && right instanceof NormalDistribution rightNorm) {
            return evaluate(leftNorm, rightNorm);
        } else if (left instanceof ProbabilityDensityFunction leftPDF
                && right instanceof ProbabilityDensityFunction rightPDF) {
            SampleHelper helper = new SampleHelper();
            return addDistributions(helper.getSamples(leftPDF), helper.getSamples(rightPDF));
        }

        double leftVal = toDouble(left);
        double rightVal = toDouble(right);
        return evaluate(leftVal, rightVal);
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
