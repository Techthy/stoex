package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
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

    // CONTINUOUS

    public LognormalDistribution evaluate(LognormalDistribution left, LognormalDistribution right) {
        LognormalDistribution result = StoexFactory.eINSTANCE.createLognormalDistribution();
        result.setMu(left.getMu() - right.getMu());
        result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
        return result;
    }

    public SampledDistribution divDistributions(double[] samplesLeft, double[] samplesRight) {

        MonteCarloOperation op = new MonteCarloOperation();
        double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
                ProbabilityFunctionOperations.DIV);

        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : combinedSamples) {
            result.getValues().add(d);
        }
        return result;
    }

    // Scalar * distribution cases for CONTINUOUS distributions

    public NormalDistribution evaluate(NormalDistribution left, double right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() / right);
        result.setSigma(left.getSigma() / Math.abs(right));
        return result;
    }

    public NormalDistribution evaluate(double left, NormalDistribution right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left / right.getMu());
        result.setSigma(Math.abs(left) / right.getSigma());
        return result;
    }

    public SampledDistribution evaluate(double[] samplesLeft, double right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : samplesLeft) {
            result.getValues().add(d / right);
        }
        return result;
    }

    public SampledDistribution evaluate(double left, double[] samplesRight) {
        if (left == 0) {
            throw new ArithmeticException("Division by zero");
        }
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : samplesRight) {
            result.getValues().add(left / d);
        }
        return result;
    }

    // DISCRETE

    public IntProbabilityMassFunction multDistributions(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right) {
        ProbabiltyMassFunctionHelper conv = new ProbabiltyMassFunctionHelper();
        return conv.combine(left, right, ProbabilityFunctionOperations.DIV);
    }

    // Scalar * Distribution cases for DISCRETE distributions

    public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left, int right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (var sample : left.getSamples()) {
            if (sample.getValue() % right == 0) { // only include samples that divide evenly
                var newSample = StoexFactory.eINSTANCE.createIntSample();
                newSample.setValue(sample.getValue() / right);
                newSample.setProbability(sample.getProbability());
                result.getSamples().add(newSample);
            }
        }
        return result;
    }

    public IntProbabilityMassFunction evaluate(int left, IntProbabilityMassFunction right) {
        if (left == 0) {
            throw new ArithmeticException("Division by zero");
        }
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (var sample : right.getSamples()) {
            if (left % sample.getValue() == 0) { // only include samples that divide evenly
                var newSample = StoexFactory.eINSTANCE.createIntSample();
                newSample.setValue(left / sample.getValue());
                newSample.setProbability(sample.getProbability());
                result.getSamples().add(newSample);
            }
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
            return divDistributions(helper.getSamples(leftSample), helper.getSamples(rightSample));
        } else if (left instanceof NormalDistribution leftLog && right instanceof Number rightNum) {
            SampleHelper helper = new SampleHelper();
            return evaluate(helper.getSamples(leftLog), rightNum.doubleValue());
        } else if (left instanceof Number leftNum && right instanceof NormalDistribution rightLog) {
            SampleHelper helper = new SampleHelper();
            return evaluate(leftNum.doubleValue(), helper.getSamples(rightLog));
        } else if (left instanceof ProbabilityDensityFunction leftPDF && right instanceof Number rightNum) {
            SampleHelper helper = new SampleHelper();
            return evaluate(helper.getSamples(leftPDF), rightNum.doubleValue());
        } else if (left instanceof Number leftNum && right instanceof ProbabilityDensityFunction rightPDF) {
            SampleHelper helper = new SampleHelper();
            return evaluate(leftNum.doubleValue(), helper.getSamples(rightPDF));
        } else if (left instanceof IntProbabilityMassFunction leftIntPMF
                && right instanceof IntProbabilityMassFunction rightIntPMF) {
            return multDistributions(leftIntPMF, rightIntPMF);
        } else if (left instanceof IntProbabilityMassFunction leftIntPMF && right instanceof Integer rightInt) {
            return evaluate(leftIntPMF, rightInt);
        } else if (left instanceof Integer leftInt && right instanceof IntProbabilityMassFunction rightIntPMF) {
            return evaluate(rightIntPMF, leftInt);
        }

        if (left instanceof Integer leftInt && right instanceof Integer rightInt) {
            return evaluate((int) leftInt, (int) rightInt);
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
