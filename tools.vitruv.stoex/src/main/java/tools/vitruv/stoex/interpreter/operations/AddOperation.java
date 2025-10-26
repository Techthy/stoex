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
 * This class implements the add operation for different kinds of operands.
 * Most of the implementations are based on closed form solutions for the sum of
 * distributions. If no closed form solution exists, a Monte Carlo sampling
 * approach
 * is used to approximate the resulting distribution.
 * 
 * @author Hammann
 */
public class AddOperation implements Operation {

	public int evaluate(Integer left, Integer right) {
		return left + right;
	}

	@Override
	public int evaluate(int left, int right) {
		return left + right;
	}

	@Override
	public double evaluate(double left, double right) {
		return left + right;
	}

	// ==================================================================
	// CONTINUOUS
	// ==================================================================

	@Override
	public ProbabilityDensityFunction evaluate(NormalDistribution left, NormalDistribution right) {
		NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
		result.setMu(left.getMu() + right.getMu());
		result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
		return result;

	}

	@Override
	public ProbabilityDensityFunction evaluate(ExponentialDistribution left, ExponentialDistribution right) {
		// Closed Form Solution exists only for same lambda
		if (left.getLambda() != right.getLambda()) {
			SampleHelper helper = new SampleHelper();
			return evaluate(helper.getSamples(left), helper.getSamples(right));
		}

		GammaDistribution result = StoexFactory.eINSTANCE.createGammaDistribution();
		result.setAlpha(2);
		result.setTheta(1 / left.getLambda());
		return result;
	}

	@Override
	public SampledDistribution evaluate(double[] samplesLeft, double[] samplesRight) {

		MonteCarloOperation op = new MonteCarloOperation();
		double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
				ProbabilityFunctionOperations.ADD);

		SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
		for (double d : combinedSamples) {
			result.getValues().add(d);
		}
		return result;
	}

	@Override
	public ProbabilityDensityFunction evaluate(GammaDistribution left, GammaDistribution right) {
		// Closed Form Solution exists only for same theta
		if (left.getTheta() != right.getTheta()) {
			SampleHelper helper = new SampleHelper();
			return evaluate(helper.getSamples(left), helper.getSamples(right));
		}

		GammaDistribution result = StoexFactory.eINSTANCE.createGammaDistribution();
		result.setAlpha(left.getAlpha() + right.getAlpha());
		result.setTheta(left.getTheta());
		return result;
	}

	// ==================================================================
	// Scalar cases for CONTINUOUS distributions
	// ==================================================================

	@Override
	public NormalDistribution evaluate(NormalDistribution left, double right) {
		NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
		result.setMu(left.getMu() + right);
		result.setSigma(left.getSigma());
		return result;
	}

	@Override
	public NormalDistribution evaluate(double left, NormalDistribution right) {
		return evaluate(right, left);
	}

	@Override
	public SampledDistribution evaluate(double[] samplesLeft, double right) {
		SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
		for (double d : samplesLeft) {
			result.getValues().add(d + right);
		}
		return result;
	}

	@Override
	public SampledDistribution evaluate(double left, double[] samplesRight) {
		return evaluate(samplesRight, left);
	}

	// ==================================================================
	// DISCRETE
	// ==================================================================

	@Override
	public ProbabilityMassFunction evaluate(PoissonDistribution left, PoissonDistribution right) {
		PoissonDistribution result = StoexFactory.eINSTANCE.createPoissonDistribution();
		result.setLambda(left.getLambda() + right.getLambda());
		return result;
	}

	@Override
	public ProbabilityMassFunction evaluate(BernoulliDistribution left,
			BernoulliDistribution right) {
		// Closed Form Solution exists only for same p
		if (left.getP() != right.getP()) {
			ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
			return conv.combine(conv.convertToPMF(left), conv.convertToPMF(right), ProbabilityFunctionOperations.ADD);
		}
		BinomialDistribution result = StoexFactory.eINSTANCE.createBinomialDistribution();
		result.setN(2);
		result.setP(left.getP());
		return result;
	}

	@Override
	public ProbabilityMassFunction evaluate(BinomialDistribution left,
			BinomialDistribution right) {
		// Closed Form Solution exists only for same p
		if (left.getP() != right.getP()) {
			ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
			return conv.combine(conv.convertToPMF(left), conv.convertToPMF(right), ProbabilityFunctionOperations.ADD);
		}
		BinomialDistribution result = StoexFactory.eINSTANCE.createBinomialDistribution();
		result.setN(left.getN() + right.getN());
		result.setP(left.getP());
		return result;
	}

	@Override
	public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left,
			IntProbabilityMassFunction right) {
		ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
		return conv.combine(left, right, ProbabilityFunctionOperations.ADD);
	}

	// ==================================================================
	// Scalar cases for DISCRETE distributions
	// ==================================================================

	@Override
	public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left, int right) {
		IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
		for (var sample : left.getSamples()) {
			var newSample = StoexFactory.eINSTANCE.createIntSample();
			newSample.setValue(sample.getValue() + right);
			newSample.setProbability(sample.getProbability());
			result.getSamples().add(newSample);
		}
		return result;
	}

	@Override
	public IntProbabilityMassFunction evaluate(int left, IntProbabilityMassFunction right) {
		return evaluate(right, left);
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

	@Override
	public ProbabilityDensityFunction evaluate(LognormalDistribution left, LognormalDistribution right) {
		SampleHelper helper = new SampleHelper();
		return evaluate(helper.getSamples(left), helper.getSamples(right));
	}
}
