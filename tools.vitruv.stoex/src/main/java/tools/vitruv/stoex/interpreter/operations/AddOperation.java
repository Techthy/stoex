package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.PoissonDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.ProbabilityMassFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

/**
 * Implements the operation "addition" for different kinds of operands.
 *
 * closed form solutions exist for NormalDistribution, ExponentialDistribution,
 * and GammaDistribution
 *
 */
public class AddOperation {

	public int evaluate(int left, int right) {
		return left + right;
	}

	public double evaluate(double left, double right) {
		return left + right;
	}

	public Object evaluate(Object left, Object right) {

		if (left instanceof NormalDistribution leftNorm && right instanceof NormalDistribution rightNorm) {
			return evaluate(leftNorm, rightNorm);
		} else if (left instanceof ExponentialDistribution leftExp
				&& right instanceof ExponentialDistribution rightExp) {
			return evaluate(leftExp, rightExp);
		} else if (left instanceof GammaDistribution leftGamma && right instanceof GammaDistribution rightGamma) {
			return evaluate(leftGamma, rightGamma);
		} else if (left instanceof PoissonDistribution leftPoisson
				&& right instanceof PoissonDistribution rightPoisson) {
			return evaluate(leftPoisson, rightPoisson);
		} else if (left instanceof BernoulliDistribution leftBernoulli
				&& right instanceof BernoulliDistribution rightBernoulli) {
			return evaluate(leftBernoulli, rightBernoulli);
		} else if (left instanceof IntProbabilityMassFunction leftIntPMF
				&& right instanceof IntProbabilityMassFunction rightIntPMF) {
			return evaluate(leftIntPMF, rightIntPMF);
		} else if (left instanceof ProbabilityDensityFunction leftPDF
				&& right instanceof ProbabilityDensityFunction rightPDF) {
			SampleHelper helper = new SampleHelper();
			return addDistributions(helper.getSamples(leftPDF), helper.getSamples(rightPDF));
		}

		double leftVal = toDouble(left);
		double rightVal = toDouble(right);
		return evaluate(leftVal, rightVal);
	}

	// Closed form solution for CONTINUOUS distributions

	public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right) {
		NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
		result.setMu(left.getMu() + right.getMu());
		result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
		return result;

	}

	public ProbabilityDensityFunction evaluate(ExponentialDistribution left, ExponentialDistribution right) {
		// Closed Form Solution exists only for same lambda
		if (left.getLambda() != right.getLambda()) {
			SampleHelper helper = new SampleHelper();
			return addDistributions(helper.getSamples(left), helper.getSamples(right));
		}

		GammaDistribution result = StoexFactory.eINSTANCE.createGammaDistribution();
		result.setAlpha(2);
		result.setTheta(1 / left.getLambda());
		return result;
	}

	public SampledDistribution addDistributions(double[] samplesLeft, double[] samplesRight) {

		MonteCarloOperation op = new MonteCarloOperation();
		double[] combinedSamples = op.evaluateTermOperation(samplesLeft, samplesRight, 10000,
				ProbabilityFunctionOperations.ADD);

		SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
		for (double d : combinedSamples) {
			result.getValues().add(d);
		}
		return result;
	}

	public ProbabilityDensityFunction evaluate(GammaDistribution left, GammaDistribution right) {
		// Closed Form Solution exists only for same theta
		if (left.getTheta() != right.getTheta()) {
			SampleHelper helper = new SampleHelper();
			return addDistributions(helper.getSamples(left), helper.getSamples(right));
		}

		GammaDistribution result = StoexFactory.eINSTANCE.createGammaDistribution();
		result.setAlpha(left.getAlpha() + right.getAlpha());
		result.setTheta(left.getTheta());
		return result;
	}

	// Closed form solution for DISCRETE distributions

	public PoissonDistribution evaluate(PoissonDistribution left, PoissonDistribution right) {
		PoissonDistribution result = StoexFactory.eINSTANCE.createPoissonDistribution();
		result.setLambda(left.getLambda() + right.getLambda());
		return result;
	}

	public ProbabilityMassFunction evaluate(BernoulliDistribution left,
			BernoulliDistribution right) {
		// Closed Form Solution exists only for same p
		if (left.getP() != right.getP()) {
			DiscreteConvolution conv = new DiscreteConvolution();
			return conv.convolve(left, right, ProbabilityFunctionOperations.ADD);
		}
		BinomialDistribution result = StoexFactory.eINSTANCE.createBinomialDistribution();
		result.setN(2);
		result.setP(left.getP());
		return result;
	}

	public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left, IntProbabilityMassFunction right) {
		DiscreteConvolution conv = new DiscreteConvolution();
		return conv.convolve(left, right, ProbabilityFunctionOperations.ADD);
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
