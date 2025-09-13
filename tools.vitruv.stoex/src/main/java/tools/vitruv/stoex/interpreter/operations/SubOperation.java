package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.ProbabilityMassFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

/**
 * Implements the operation "subtraction" for different kinds of operands.
 *
 */
public class SubOperation {

    public int evaluate(int left, int right) {
        return left - right;
    }

    public double evaluate(double left, double right) {
        return left - right;
    }

    // CONTINUOUS

    public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() - right.getMu());
        result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
        return result;

    }

    public SampledDistribution subDistributions(double[] samplesLeft, double[] samplesRight) {

        MonteCarloOperation op = new MonteCarloOperation();
        double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
                ProbabilityFunctionOperations.SUB);

        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : combinedSamples) {
            result.getValues().add(d);
        }
        return result;
    }

    // Scalar + Distribution cases for CONTINUOUS distributions

    public NormalDistribution evaluate(NormalDistribution left, double right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() - right);
        result.setSigma(left.getSigma());
        return result;
    }

    public NormalDistribution evaluate(double left, NormalDistribution right) {
        return evaluate(right, left);
    }

    public SampledDistribution evaluate(double[] samplesLeft, double right) {
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : samplesLeft) {
            result.getValues().add(d - right);
        }
        return result;
    }

    public SampledDistribution evaluate(double left, double[] samplesRight) {
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : samplesRight) {
            result.getValues().add(left - d);
        }
        return result;
    }

    // DISCRETE

    public IntProbabilityMassFunction subDistributions(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right) {
        DiscreteConvolution conv = new DiscreteConvolution();
        return conv.convolve(left, right, ProbabilityFunctionOperations.SUB);
    }

    // Scalar + Distribution cases for DISCRETE distributions

    public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left, int right) {
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (var sample : left.getSamples()) {
            var newSample = StoexFactory.eINSTANCE.createIntSample();
            newSample.setValue(sample.getValue() - right);
            newSample.setProbability(sample.getProbability());
            result.getSamples().add(newSample);
        }
        return result;
    }

    public IntProbabilityMassFunction evaluate(int left, IntProbabilityMassFunction right) {
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (var sample : right.getSamples()) {
            var newSample = StoexFactory.eINSTANCE.createIntSample();
            newSample.setValue(left - sample.getValue());
            newSample.setProbability(sample.getProbability());
            result.getSamples().add(newSample);
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
            return subDistributions(helper.getSamples(leftPDF), helper.getSamples(rightPDF));
        } else if (left instanceof NormalDistribution leftNorm && right instanceof Number rightNum) {
            return evaluate(leftNorm, toDouble(rightNum));
        } else if (left instanceof Number leftNum && right instanceof NormalDistribution rightNorm) {
            return evaluate(toDouble(leftNum), rightNorm);
        } else if (left instanceof IntProbabilityMassFunction leftPMF
                && right instanceof IntProbabilityMassFunction rightPMF) {
            DiscreteConvolution conv = new DiscreteConvolution();
            return subDistributions(conv.convertToPMF(leftPMF), conv.convertToPMF(rightPMF));
        } else if (left instanceof ProbabilityMassFunction leftPMF && right instanceof Integer rightInt) {
            DiscreteConvolution conv = new DiscreteConvolution();
            return evaluate(conv.convertToPMF(leftPMF), rightInt);
        } else if (left instanceof Integer leftInt && right instanceof IntProbabilityMassFunction rightIntPMF) {
            DiscreteConvolution conv = new DiscreteConvolution();
            return evaluate(conv.convertToPMF(rightIntPMF), leftInt);
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
