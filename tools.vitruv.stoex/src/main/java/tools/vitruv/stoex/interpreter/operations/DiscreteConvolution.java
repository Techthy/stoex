package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.BernoulliDistribution;
import tools.vitruv.stoex.stoex.BinomialDistribution;
import tools.vitruv.stoex.stoex.DiscreteUniformDistribution;
import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.IntSample;
import tools.vitruv.stoex.stoex.PoissonDistribution;
import tools.vitruv.stoex.stoex.ProbabilityMassFunction;
import tools.vitruv.stoex.stoex.StoexFactory;

public class DiscreteConvolution {

    public IntProbabilityMassFunction convolve(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right,
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

    public IntProbabilityMassFunction convertToPMF(BernoulliDistribution distribution) {
        IntProbabilityMassFunction pmf = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        IntSample leftSuccess = StoexFactory.eINSTANCE.createIntSample();
        leftSuccess.setValue(1);
        leftSuccess.setProbability(distribution.getP());
        IntSample leftFailure = StoexFactory.eINSTANCE.createIntSample();
        leftFailure.setValue(0);
        leftFailure.setProbability(1 - distribution.getP());
        pmf.getSamples().add(leftSuccess);
        pmf.getSamples().add(leftFailure);

        return pmf;
    }

    public IntProbabilityMassFunction convertToPMF(BinomialDistribution distribution) {
        IntProbabilityMassFunction pmf = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        for (int k = 0; k <= distribution.getN(); k++) {
            IntSample sample = StoexFactory.eINSTANCE.createIntSample();
            sample.setValue(k);
            sample.setProbability(binomialProbability(distribution.getN(), distribution.getP(), k));
            pmf.getSamples().add(sample);
        }
        return pmf;
    }

    public IntProbabilityMassFunction convertToPMF(PoissonDistribution distribution) {
        IntProbabilityMassFunction pmf = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        double lambda = distribution.getLambda();
        for (int k = 0; k <= 10 * lambda; k++) {
            IntSample sample = StoexFactory.eINSTANCE.createIntSample();
            sample.setValue(k);
            sample.setProbability(poissonProbability(lambda, k));
            pmf.getSamples().add(sample);
        }
        return pmf;
    }

    public IntProbabilityMassFunction convertToPMF(DiscreteUniformDistribution distribution) {
        IntProbabilityMassFunction pmf = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        int a = distribution.getA();
        int b = distribution.getB();
        double probability = 1.0 / (b - a + 1);
        for (int k = a; k <= b; k++) {
            IntSample sample = StoexFactory.eINSTANCE.createIntSample();
            sample.setValue(k);
            sample.setProbability(probability);
            pmf.getSamples().add(sample);
        }
        return pmf;
    }

    public IntProbabilityMassFunction convertToPMF(ProbabilityMassFunction distribution) {
        if (distribution instanceof IntProbabilityMassFunction) {
            return (IntProbabilityMassFunction) distribution;
        } else if (distribution instanceof BernoulliDistribution bernoulliDistribution) {
            return convertToPMF(bernoulliDistribution);
        } else if (distribution instanceof BinomialDistribution binomialDistribution) {
            return convertToPMF(binomialDistribution);
        } else if (distribution instanceof PoissonDistribution poissonDistribution) {
            return convertToPMF(poissonDistribution);
        } else if (distribution instanceof DiscreteUniformDistribution discreteUniformDistribution) {
            return convertToPMF(discreteUniformDistribution);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported distribution type: " + distribution.getClass().getSimpleName());
        }
    }

    private double poissonProbability(double lambda, int k) {
        return (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
    }

    private double binomialProbability(int n, double p, int k) {
        double binomCoeff = factorial(n) / (factorial(k) * factorial(n - k));
        return binomCoeff * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    private double factorial(int num) {
        if (num == 0 || num == 1) {
            return 1;
        }
        double result = 1;
        for (int i = 2; i <= num; i++) {
            result *= i;
        }
        return result;
    }

    public void printHistogram(IntProbabilityMassFunction pmf) {
        for (IntSample sample : pmf.getSamples()) {
            System.out.println("Value: " + sample.getValue() + ", Probability: " + sample.getProbability());
        }
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

}
