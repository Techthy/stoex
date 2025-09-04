package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.IntSample;
import tools.vitruv.stoex.stoex.StoexFactory;

public class DiscreteConvolution {

    public IntProbabilityMassFunction convolve(IntProbabilityMassFunction left, IntProbabilityMassFunction right,
            ProbabilityFunctionOperations operation) {
        IntProbabilityMassFunction result = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();

        for (IntSample sampleLeft : left.getSamples()) {
            for (IntSample sampleRight : right.getSamples()) {

                int newValue = evaluate(sampleLeft.getValue(), sampleRight.getValue(), operation);
                double newProbability = sampleLeft.getProbability() * sampleRight.getProbability();

                IntSample existingSample = result.getSamples().stream()
                        .filter(s -> s.getValue() == newValue)
                        .findFirst()
                        .orElse(null);

                if (existingSample != null) {
                    existingSample.setProbability(existingSample.getProbability() + newProbability);
                } else {
                    IntSample newSample = StoexFactory.eINSTANCE.createIntSample();
                    newSample.setValue(newValue);
                    newSample.setProbability(newProbability);
                    result.getSamples().add(newSample);
                }
            }
        }

        return result;
    }

    private int evaluate(int leftValue, int rightValue, ProbabilityFunctionOperations operation) {
        switch (operation) {
            case ADD -> {
                return leftValue + rightValue;
            }
            case SUB -> {
                return leftValue - rightValue;
            }
            case MUL -> {
                return leftValue * rightValue;
            }
            case DIV -> {
                if (rightValue == 0) {
                    throw new ArithmeticException("Division by zero in convolution operation.");
                }
                return leftValue / rightValue;
            }
            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    public IntProbabilityMassFunction convolve(BernoulliDistribution left, BernoulliDistribution right,
            ProbabilityFunctionOperations operation) {
        IntProbabilityMassFunction leftPMF = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        IntSample leftSuccess = StoexFactory.eINSTANCE.createIntSample();
        leftSuccess.setValue(1);
        leftSuccess.setProbability(left.getP());
        IntSample leftFailure = StoexFactory.eINSTANCE.createIntSample();
        leftFailure.setValue(0);
        leftFailure.setProbability(1 - left.getP());
        leftPMF.getSamples().add(leftSuccess);
        leftPMF.getSamples().add(leftFailure);

        IntProbabilityMassFunction rightPMF = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        IntSample rightSuccess = StoexFactory.eINSTANCE.createIntSample();
        rightSuccess.setValue(1);
        rightSuccess.setProbability(right.getP());
        IntSample rightFailure = StoexFactory.eINSTANCE.createIntSample();
        rightFailure.setValue(0);
        rightFailure.setProbability(1 - right.getP());
        rightPMF.getSamples().add(rightSuccess);
        rightPMF.getSamples().add(rightFailure);

        return convolve(leftPMF, rightPMF, operation);
    }

    public void printHistogram(IntProbabilityMassFunction pmf) {
        for (IntSample sample : pmf.getSamples()) {
            System.out.println("Value: " + sample.getValue() + ", Probability: " + sample.getProbability());
        }
    }

}
