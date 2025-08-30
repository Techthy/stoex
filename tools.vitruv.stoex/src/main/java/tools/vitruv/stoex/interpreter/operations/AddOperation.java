package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.BoxedPDF;
import tools.vitruv.stoex.stoex.ContinuousSample;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.StoexFactory;

/**
 * Implements the operation "addition" for different kinds of operands.
 * 
 * @author Hammann
 */
public class AddOperation {

	public int evaluate(int left, int right) {
		return left + right;
	}

	public double evaluate(double left, double right) {
		return left + right;
	}

	// Fallback that handles String and Boolean as well as the mixture of types
	public Object evaluate(Object left, Object right) {
		if (left instanceof NormalDistribution leftNorm && right instanceof NormalDistribution rightNorm) {
			return evaluate(leftNorm, rightNorm);
		} else if (left instanceof ExponentialDistribution leftExp
				&& right instanceof ExponentialDistribution rightExp) {
			return evaluate(leftExp, rightExp);
		} else if (left instanceof GammaDistribution leftGamma && right instanceof GammaDistribution rightGamma) {
			return evaluate(leftGamma, rightGamma);
		} else if (left instanceof ProbabilityDensityFunction leftPDF
				&& right instanceof ProbabilityDensityFunction rightPDF) {
			return evaluate(leftPDF, rightPDF);
		}

		System.out.println("Left type: " + (left == null ? "null" : left.getClass().getName()));
		System.out.println("Right type: " + (right == null ? "null" : right.getClass().getName()));

		double leftVal = toDouble(left);
		double rightVal = toDouble(right);
		return leftVal + rightVal;
	}

	// General Case for all continuous distributions
	public BoxedPDF evaluate(ProbabilityDensityFunction left, ProbabilityDensityFunction right) {
		return monteCarloBoxedPDF(left, right);
	}

	// Monte Carlo Estimation for all ProbabilityDensityFunction types
	public BoxedPDF monteCarloBoxedPDF(ProbabilityDensityFunction left, ProbabilityDensityFunction right) {
		SampleHelper sampleHelper = new SampleHelper();

		java.util.List<ContinuousSample> leftSamples = sampleHelper.getSamples(left);
		java.util.List<ContinuousSample> rightSamples = sampleHelper.getSamples(right);

		BoxedPDF result = StoexFactory.eINSTANCE.createBoxedPDF();
		for (ContinuousSample leftSample : leftSamples) {
			for (ContinuousSample rightSample : rightSamples) {
				double leftValue = leftSample.getValue();
				double rightValue = rightSample.getValue();
				double sumValue = leftValue + rightValue;
				double sumProb = leftSample.getProbability() * rightSample.getProbability();

				ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
				sample.setValue(sumValue);
				sample.setProbability(sumProb);
				result.getSamples().add(sample);
			}
		}
		return result;
	}

	public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right) {
		NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
		result.setMu(left.getMu() + right.getMu());
		result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
		return result;

	}

	public ProbabilityDensityFunction evaluate(ExponentialDistribution left, ExponentialDistribution right) {
		// Closed Form Solution exists only for same lambda
		if (left.getLambda() != right.getLambda()) {
			return monteCarloBoxedPDF(left, right);
		}

		GammaDistribution result = StoexFactory.eINSTANCE.createGammaDistribution();
		result.setAlpha(2);
		result.setTheta(1 / left.getLambda());
		return result;
	}

	public ProbabilityDensityFunction evaluate(GammaDistribution left, GammaDistribution right) {
		// Closed Form Solution exists only for same theta
		if (left.getTheta() != right.getTheta()) {
			return monteCarloBoxedPDF(left, right);
		}

		GammaDistribution result = StoexFactory.eINSTANCE.createGammaDistribution();
		result.setAlpha(left.getAlpha() + right.getAlpha());
		result.setTheta(left.getTheta());
		return result;
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
