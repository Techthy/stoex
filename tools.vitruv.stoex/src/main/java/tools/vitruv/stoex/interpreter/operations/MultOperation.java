package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
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

    public NormalDistribution evaluate(NormalDistribution left, double right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() * right);
        result.setSigma(left.getSigma() * Math.abs(right));
        return result;
    }

    public NormalDistribution evaluate(double left, NormalDistribution right) {
        return evaluate(right, left);
    }

    public SampledDistribution evaluate(double[] samplesLeft, double right) {
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : samplesLeft) {
            result.getValues().add(d * right);
        }
        return result;
    }

    public SampledDistribution evaluate(double left, double[] samplesRight) {
        return evaluate(samplesRight, left);
    }

    // DISCRETE

    public IntProbabilityMassFunction multDistributions(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right) {
        ProbabiltyMassFunctionHelper conv = new ProbabiltyMassFunctionHelper();
        return conv.combine(left, right, ProbabilityFunctionOperations.MUL);
    }

    // Scalar * Distribution cases for DISCRETE distributions

    public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left, int right) {
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (var sample : left.getSamples()) {
            var newSample = StoexFactory.eINSTANCE.createIntSample();
            newSample.setValue(sample.getValue() * right);
            newSample.setProbability(sample.getProbability());
            result.getSamples().add(newSample);
        }
        return result;
    }

    public IntProbabilityMassFunction evaluate(int left, IntProbabilityMassFunction right) {
        return evaluate(right, left);
    }

    // Fallback that handles String and Boolean as well as the mixture of types
    public Object evaluate(Object left, Object right) {
        if (left instanceof LognormalDistribution leftLog && right instanceof LognormalDistribution rightLog) {
            return evaluate(leftLog, rightLog);
        } else if (left instanceof ProbabilityDensityFunction leftPDF
                && right instanceof ProbabilityDensityFunction rightPDF) {
            SampleHelper helper = new SampleHelper();
            return multDistributions(helper.getSamples(leftPDF), helper.getSamples(rightPDF));
        } else if (left instanceof NormalDistribution leftNorm && right instanceof Number rightNum) {
            return evaluate(leftNorm, rightNum.doubleValue());
        } else if (left instanceof Number leftNum && right instanceof NormalDistribution rightNorm) {
            return evaluate(rightNorm, leftNum.doubleValue());
        } else if (left instanceof ProbabilityDensityFunction leftPDF && right instanceof Number rightNum) {
            SampleHelper helper = new SampleHelper();
            return evaluate(helper.getSamples(leftPDF), rightNum.doubleValue());
        } else if (left instanceof Number leftNum && right instanceof ProbabilityDensityFunction rightPDF) {
            SampleHelper helper = new SampleHelper();
            return evaluate(helper.getSamples(rightPDF), leftNum.doubleValue());
        } else if (left instanceof ProbabilityMassFunction leftPMF
                && right instanceof ProbabilityMassFunction rightPMF) {
            ProbabiltyMassFunctionHelper conv = new ProbabiltyMassFunctionHelper();
            return multDistributions(conv.convertToPMF(leftPMF), conv.convertToPMF(rightPMF));
        } else if (left instanceof ProbabilityMassFunction leftPMF && right instanceof Integer rightInt) {
            ProbabiltyMassFunctionHelper conv = new ProbabiltyMassFunctionHelper();
            return evaluate(conv.convertToPMF(leftPMF), rightInt);
        } else if (left instanceof Integer leftInt && right instanceof IntProbabilityMassFunction rightIntPMF) {
            ProbabiltyMassFunctionHelper conv = new ProbabiltyMassFunctionHelper();
            return evaluate(conv.convertToPMF(rightIntPMF), leftInt);
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
