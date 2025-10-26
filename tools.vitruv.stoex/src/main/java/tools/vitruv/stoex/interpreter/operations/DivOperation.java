package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.PoissonDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.ProbabilityMassFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

/**
 * This class implements the division operation for different kinds of operands.
 * Most of the implementations are based on closed form solutions for the ratio
 * of
 * distributions. If no closed form solution exists, a Monte Carlo sampling
 * approach
 * is used to approximate the resulting distribution.
 * 
 * @author Hammann
 */
public class DivOperation implements Operation {

    @Override
    public int evaluate(int left, int right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left / right;
    }

    @Override
    public double evaluate(double left, double right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left / right;
    }
    // ==================================================================
    // CONTINUOUS
    // ==================================================================

    @Override
    public ProbabilityDensityFunction evaluate(LognormalDistribution left, LognormalDistribution right) {
        LognormalDistribution result = StoexFactory.eINSTANCE.createLognormalDistribution();
        result.setMu(left.getMu() - right.getMu());
        result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
        return result;
    }

    @Override
    public SampledDistribution evaluate(double[] samplesLeft, double[] samplesRight) {

        MonteCarloOperation op = new MonteCarloOperation();
        double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
                ProbabilityFunctionOperations.DIV);

        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        for (double d : combinedSamples) {
            result.getValues().add(d);
        }
        return result;
    }

    // ==================================================================
    // Scalar cases for CONTINUOUS distributions
    // ==================================================================

    @Override
    public NormalDistribution evaluate(NormalDistribution left, double right) {
        if (right == 0) {
            throw new ArithmeticException("Division by zero");
        }
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() / right);
        result.setSigma(left.getSigma() / Math.abs(right));
        return result;
    }

    @Override
    public NormalDistribution evaluate(double left, NormalDistribution right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left / right.getMu());
        result.setSigma(Math.abs(left) / right.getSigma());
        return result;
    }

    @Override
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

    @Override
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

    // ==================================================================
    // DISCRETE
    // ==================================================================

    @Override
    public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right) {
        ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
        return conv.combine(left, right, ProbabilityFunctionOperations.DIV);
    }

    // ==================================================================
    // Scalar cases for DISCRETE distributions
    // ==================================================================

    @Override
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

    @Override
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

    @Override
    public ProbabilityDensityFunction evaluate(ExponentialDistribution left, ExponentialDistribution right) {
        SampleHelper helper = new SampleHelper();
        return evaluate(helper.getSamples(left), helper.getSamples(right));

    }

    @Override
    public ProbabilityDensityFunction evaluate(NormalDistribution left, NormalDistribution right) {
        SampleHelper helper = new SampleHelper();
        return evaluate(helper.getSamples(left), helper.getSamples(right));
    }

    @Override
    public ProbabilityDensityFunction evaluate(GammaDistribution left, GammaDistribution right) {
        SampleHelper helper = new SampleHelper();
        return evaluate(helper.getSamples(left), helper.getSamples(right));
    }

    @Override
    public ProbabilityMassFunction evaluate(PoissonDistribution left, PoissonDistribution right) {
        ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
        return evaluate(conv.convertToPMF(left), conv.convertToPMF(right));
    }

    @Override
    public ProbabilityMassFunction evaluate(BernoulliDistribution left, BernoulliDistribution right) {
        ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
        return evaluate(conv.convertToPMF(left), conv.convertToPMF(right));
    }

    @Override
    public ProbabilityMassFunction evaluate(BinomialDistribution left, BinomialDistribution right) {
        ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
        return evaluate(conv.convertToPMF(left), conv.convertToPMF(right));
    }

}
