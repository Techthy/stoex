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

		// CONTINUOUS
		if (left instanceof NormalDistribution leftNorm && right instanceof NormalDistribution rightNorm) {
			return evaluate(leftNorm, rightNorm);
		} else if (left instanceof NormalDistribution leftNorm && right instanceof Number rightNum) {
			return evaluate(leftNorm, rightNum.doubleValue());
		} else if (left instanceof Number leftNum && right instanceof NormalDistribution rightNorm) {
			return evaluate(leftNum.doubleValue(), rightNorm);
		} else if (left instanceof ExponentialDistribution leftExp
				&& right instanceof ExponentialDistribution rightExp) {
			return evaluate(leftExp, rightExp);
		} else if (left instanceof GammaDistribution leftGamma && right instanceof GammaDistribution rightGamma) {
			return evaluate(leftGamma, rightGamma);
		} else if (left instanceof ProbabilityDensityFunction leftPDF
				&& right instanceof ProbabilityDensityFunction rightPDF) {
			SampleHelper helper = new SampleHelper();
			return addDistributions(helper.getSamples(leftPDF), helper.getSamples(rightPDF));
		} else if (left instanceof ProbabilityDensityFunction leftPDF && right instanceof Number rightNum) {
			SampleHelper helper = new SampleHelper();
			return scalarAddition(helper.getSamples(leftPDF), rightNum.doubleValue());
		} else if (left instanceof Number leftNum && right instanceof ProbabilityDensityFunction rightPDF) {
			SampleHelper helper = new SampleHelper();
			return scalarAddition(leftNum.doubleValue(), helper.getSamples(rightPDF));
			// DISCRETE
		} else if (left instanceof PoissonDistribution leftPoisson
				&& right instanceof PoissonDistribution rightPoisson) {
			return evaluate(leftPoisson, rightPoisson);
		} else if (left instanceof BernoulliDistribution leftBernoulli
				&& right instanceof BernoulliDistribution rightBernoulli) {
			return evaluate(leftBernoulli, rightBernoulli);
		} else if (left instanceof BinomialDistribution leftBinomial
				&& right instanceof BinomialDistribution rightBinomial) {
			return evaluate(leftBinomial, rightBinomial);
		} else if (left instanceof ProbabilityMassFunction leftPMF
				&& right instanceof ProbabilityMassFunction rightPMF) {
			DiscreteConvolution conv = new DiscreteConvolution();
			return addDistributions(conv.convertToPMF(leftPMF), conv.convertToPMF(rightPMF));
		} else if (left instanceof ProbabilityMassFunction leftPMF && right instanceof Integer rightInt) {
			DiscreteConvolution conv = new DiscreteConvolution();
			return scalarAddition(conv.convertToPMF(leftPMF), rightInt);
		} else if (left instanceof Integer leftInt && right instanceof IntProbabilityMassFunction rightIntPMF) {
			DiscreteConvolution conv = new DiscreteConvolution();
			return scalarAddition(conv.convertToPMF(rightIntPMF), leftInt);
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

	// Scalar + Distribution cases for CONTINUOUS distributions

	public NormalDistribution evaluate(NormalDistribution left, double right) {
		NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
		result.setMu(left.getMu() + right);
		result.setSigma(left.getSigma());
		return result;
	}

	public NormalDistribution evaluate(double left, NormalDistribution right) {
		return evaluate(right, left);
	}

	public SampledDistribution scalarAddition(double[] samplesLeft, double right) {
		SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
		for (double d : samplesLeft) {
			result.getValues().add(d + right);
		}
		return result;
	}

	public SampledDistribution scalarAddition(double left, double[] samplesRight) {
		return scalarAddition(samplesRight, left);
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
			return conv.convolve(conv.convertToPMF(left), conv.convertToPMF(right), ProbabilityFunctionOperations.ADD);
		}
		BinomialDistribution result = StoexFactory.eINSTANCE.createBinomialDistribution();
		result.setN(2);
		result.setP(left.getP());
		return result;
	}

	public ProbabilityMassFunction evaluate(BinomialDistribution left,
			BinomialDistribution right) {
		// Closed Form Solution exists only for same p
		if (left.getP() != right.getP()) {
			DiscreteConvolution conv = new DiscreteConvolution();
			return conv.convolve(conv.convertToPMF(left), conv.convertToPMF(right), ProbabilityFunctionOperations.ADD);
		}
		BinomialDistribution result = StoexFactory.eINSTANCE.createBinomialDistribution();
		result.setN(left.getN() + right.getN());
		result.setP(left.getP());
		return result;
	}

	public IntProbabilityMassFunction addDistributions(IntProbabilityMassFunction left,
			IntProbabilityMassFunction right) {
		DiscreteConvolution conv = new DiscreteConvolution();
		return conv.convolve(left, right, ProbabilityFunctionOperations.ADD);
	}

	// Scalar + Distribution cases for DISCRETE distributions

	public IntProbabilityMassFunction scalarAddition(IntProbabilityMassFunction left, int right) {
		IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
		for (var sample : left.getSamples()) {
			var newSample = StoexFactory.eINSTANCE.createIntSample();
			newSample.setValue(sample.getValue() + right);
			newSample.setProbability(sample.getProbability());
			result.getSamples().add(newSample);
		}
		return result;
	}

	public IntProbabilityMassFunction scalarAddition(int left, IntProbabilityMassFunction right) {
		return scalarAddition(right, left);
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
