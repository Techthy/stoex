package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.ProbabilityMassFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class MultOperation {
    public int evaluate(int left, int right) {
        return left * right;
    }

    public double evaluate(double left, double right) {
        return left * right;
    }

    // CONTINUOUS

    public LognormalDistribution evaluate(LognormalDistribution left, LognormalDistribution right) {
        LognormalDistribution result = StoexFactory.eINSTANCE.createLognormalDistribution();
        result.setMu(left.getMu() + right.getMu());
        result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
        return result;
    }

    public SampledDistribution multDistributions(double[] samplesLeft, double[] samplesRight) {

        MonteCarloOperation op = new MonteCarloOperation();
        double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
                ProbabilityFunctionOperations.MUL);

        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : combinedSamples) {
            result.getValues().add(d);
        }
        return result;
    }

    // Scalar * Distribution cases for CONTINUOUS distributions

    public LognormalDistribution evaluate(LognormalDistribution left, double right) {
        LognormalDistribution result = StoexFactory.eINSTANCE.createLognormalDistribution();
        result.setMu(left.getMu() + Math.log(right));
        result.setSigma(left.getSigma());
        return result;
    }

    public LognormalDistribution evaluate(double left, LognormalDistribution right) {
        return evaluate(right, left);
    }

    public SampledDistribution scalarMultiplication(double[] samplesLeft, double right) {
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : samplesLeft) {
            result.getValues().add(d * right);
        }
        return result;
    }

    public SampledDistribution scalarMultiplication(double left, double[] samplesRight) {
        return scalarMultiplication(samplesRight, left);
    }

    // DISCRETE

    public IntProbabilityMassFunction multDistributions(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right) {
        DiscreteConvolution conv = new DiscreteConvolution();
        return conv.convolve(left, right, ProbabilityFunctionOperations.MUL);
    }

    // Scalar * Distribution cases for DISCRETE distributions

    public IntProbabilityMassFunction scalarMultiplication(IntProbabilityMassFunction left, int right) {
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (var sample : left.getSamples()) {
            var newSample = StoexFactory.eINSTANCE.createIntSample();
            newSample.setValue(sample.getValue() * right);
            newSample.setProbability(sample.getProbability());
            result.getSamples().add(newSample);
        }
        return result;
    }

    public IntProbabilityMassFunction scalarMultiplication(int left, IntProbabilityMassFunction right) {
        return scalarMultiplication(right, left);
    }

    // Fallback that handles String and Boolean as well as the mixture of types
    public Object evaluate(Object left, Object right) {
        if (left instanceof LognormalDistribution leftLog && right instanceof LognormalDistribution rightLog) {
            return evaluate(leftLog, rightLog);
        } else if (left instanceof ProbabilityDensityFunction leftPDF
                && right instanceof ProbabilityDensityFunction rightPDF) {
            SampleHelper helper = new SampleHelper();
            return multDistributions(helper.getSamples(leftPDF), helper.getSamples(rightPDF));
        } else if (left instanceof LognormalDistribution leftLog && right instanceof Number rightNum) {
            SampleHelper helper = new SampleHelper();
            return scalarMultiplication(helper.getSamples(leftLog), rightNum.doubleValue());
        } else if (left instanceof Number leftNum && right instanceof LognormalDistribution rightLog) {
            SampleHelper helper = new SampleHelper();
            return scalarMultiplication(helper.getSamples(rightLog), leftNum.doubleValue());
        } else if (left instanceof ProbabilityDensityFunction leftPDF && right instanceof Number rightNum) {
            SampleHelper helper = new SampleHelper();
            return scalarMultiplication(helper.getSamples(leftPDF), rightNum.doubleValue());
        } else if (left instanceof Number leftNum && right instanceof ProbabilityDensityFunction rightPDF) {
            SampleHelper helper = new SampleHelper();
            return scalarMultiplication(helper.getSamples(rightPDF), leftNum.doubleValue());
        } else if (left instanceof ProbabilityMassFunction leftPMF
                && right instanceof ProbabilityMassFunction rightPMF) {
            DiscreteConvolution conv = new DiscreteConvolution();
            return multDistributions(conv.convertToPMF(leftPMF), conv.convertToPMF(rightPMF));
        } else if (left instanceof ProbabilityMassFunction leftPMF && right instanceof Integer rightInt) {
            DiscreteConvolution conv = new DiscreteConvolution();
            return scalarMultiplication(conv.convertToPMF(leftPMF), rightInt);
        } else if (left instanceof Integer leftInt && right instanceof IntProbabilityMassFunction rightIntPMF) {
            DiscreteConvolution conv = new DiscreteConvolution();
            return scalarMultiplication(conv.convertToPMF(rightIntPMF), leftInt);
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
            throw new IllegalArgumentException("Unsupported type for multiplication: " + obj.getClass().getName());
        }
    }
}
