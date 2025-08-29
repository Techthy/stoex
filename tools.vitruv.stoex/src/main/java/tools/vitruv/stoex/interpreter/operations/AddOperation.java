package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.BoxedPDF;
import tools.vitruv.stoex.stoex.ContinuousSample;
import tools.vitruv.stoex.stoex.NormalDistribution;
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

	public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right) {
		NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
		result.setMu(left.getMu() + right.getMu());
		result.setSigma(Math.sqrt(Math.pow(left.getSigma(), 2) + Math.pow(right.getSigma(), 2)));
		return result;

	}

	public BoxedPDF evaluate(BoxedPDF left, BoxedPDF right) {
		BoxedPDF result = StoexFactory.eINSTANCE.createBoxedPDF();

		// Perform convolution of the two BoxedPDFs
		for (ContinuousSample leftSample : left.getSamples()) {
			double leftValue = leftSample.getValue();
			double leftProb = leftSample.getProbability();

			for (ContinuousSample rightSample : right.getSamples()) {
				double rightValue = rightSample.getValue();
				double rightProb = rightSample.getProbability();

				double sumValue = leftValue + rightValue;
				double sumProb = leftProb * rightProb;

				// Create a new sample with sumValue and sumProb
				ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
				sample.setValue(sumValue);
				sample.setProbability(sumProb);
				result.getSamples().add(sample);
			}
		}

		return result;

	}

	// public BoxedPDF evaluate(Probab)

	// public Object compute(Object left, Object right) {
	// if (left instanceof Integer && right instanceof Integer) {
	// return compute((Integer) left, (Integer) right);
	// } else if (left instanceof Double && right instanceof Double) {
	// return compute((Double) left, (Double) right);
	// } else if (left instanceof NormalDistribution && right instanceof
	// NormalDistribution) {
	// return compute((NormalDistribution) left, (NormalDistribution) right);
	// } else if (left instanceof BoxedPDF && right instanceof BoxedPDF) {
	// return compute((BoxedPDF) left, (BoxedPDF) right);
	// } else {
	// throw new UnsupportedOperationException("Addition not supported for types " +
	// left.getClass() + " and "
	// + right.getClass() + ".");
	// }
	// }

}
