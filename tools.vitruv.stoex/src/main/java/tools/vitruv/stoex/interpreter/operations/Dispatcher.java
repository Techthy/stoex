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

public class Dispatcher {

    private Operation operation;

    public Dispatcher(Operation operation) {
        this.operation = operation;
    }

    public Object dispatch(Object left, Object right) {
        // Portable switch-like implementation using explicit classification of argument
        // types.
        TypeKind l = classify(left);
        TypeKind r = classify(right);

        switch (l) {
            case NORMAL -> {
                if (r == TypeKind.NORMAL) {
                    return operation.evaluate((NormalDistribution) left, (NormalDistribution) right);
                } else if (isNumeric(r)) {
                    return operation.evaluate((NormalDistribution) left, ((Number) right).doubleValue());
                }
            }
            case EXPONENTIAL -> {
                if (r == TypeKind.EXPONENTIAL) {
                    return operation.evaluate((ExponentialDistribution) left, (ExponentialDistribution) right);
                }
            }
            case GAMMA -> {
                if (r == TypeKind.GAMMA) {
                    return operation.evaluate((GammaDistribution) left, (GammaDistribution) right);
                }
            }
            case LOGNORMAL -> {
                if (r == TypeKind.LOGNORMAL) {
                    return operation.evaluate((LognormalDistribution) left, (LognormalDistribution) right);
                }
            }
            case PDF -> {
                if (r == TypeKind.PDF) {
                    return operation.evaluate(
                            new SampleHelper().getSamples((ProbabilityDensityFunction) left),
                            new SampleHelper().getSamples((ProbabilityDensityFunction) right));
                } else if (isNumeric(r)) {
                    return operation.evaluate(
                            new SampleHelper().getSamples((ProbabilityDensityFunction) left),
                            ((Number) right).doubleValue());
                }
            }
            case NUMBER -> {
                if (r == TypeKind.NORMAL) {
                    return operation.evaluate(((Number) left).doubleValue(), (NormalDistribution) right);
                } else if (r == TypeKind.PDF) {
                    return operation.evaluate(((Number) left).doubleValue(),
                            new SampleHelper().getSamples((ProbabilityDensityFunction) right));
                }
            }
            case POISSON -> {
                if (r == TypeKind.POISSON) {
                    return operation.evaluate((PoissonDistribution) left, (PoissonDistribution) right);
                } else if (isMassFunction(r)) {
                    return evalPMF((ProbabilityMassFunction) left, (ProbabilityMassFunction) right);
                }
            }
            case BERNOULLI -> {
                if (r == TypeKind.BERNOULLI) {
                    return operation.evaluate((BernoulliDistribution) left, (BernoulliDistribution) right);
                } else if (isMassFunction(r)) {
                    return evalPMF((ProbabilityMassFunction) left, (ProbabilityMassFunction) right);
                }
            }
            case BINOMIAL -> {
                if (r == TypeKind.BINOMIAL) {
                    return operation.evaluate((BinomialDistribution) left, (BinomialDistribution) right);
                }
            }
            case PMF -> {
                if (r == TypeKind.PMF) {
                    return evalPMF((ProbabilityMassFunction) left, (ProbabilityMassFunction) right);
                } else if (r == TypeKind.INTEGER) {
                    return operation.evaluate(
                            new ProbabilityMassFunctionHelper().convertToPMF((ProbabilityMassFunction) left),
                            (Integer) right);
                }
            }
            case INT_PMF -> {
                // left is IntProbabilityMassFunction; original code handled Integer left &
                // IntPMF right
                if (r == TypeKind.INTEGER) {
                    ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
                    return operation.evaluate(conv.convertToPMF((IntProbabilityMassFunction) left), (Integer) right);
                } else if (r == TypeKind.INT_PMF) {
                    return operation.evaluate((IntProbabilityMassFunction) left, (IntProbabilityMassFunction) right);
                } else if (isMassFunction(r)) {
                    return evalPMF((IntProbabilityMassFunction) left, (ProbabilityMassFunction) right);
                }
            }
            case INTEGER -> {
                if (r == TypeKind.INT_PMF) {
                    ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
                    return operation.evaluate(conv.convertToPMF((IntProbabilityMassFunction) right), (Integer) left);
                } else if (r == TypeKind.NORMAL) {
                    return operation.evaluate(((Number) left).doubleValue(), (NormalDistribution) right);
                } else if (r == TypeKind.INTEGER) {
                    return operation.evaluate((int) left, (int) right);
                }
            }
            default -> {
                // fall through to numeric conversion
            }
        }

        // fallback to numeric conversion
        double leftVal = toDouble(left);
        double rightVal = toDouble(right);
        return operation.evaluate(leftVal, rightVal);
    }

    private boolean isMassFunction(TypeKind kind) {
        return kind == TypeKind.PMF || kind == TypeKind.INT_PMF || kind == TypeKind.BERNOULLI
                || kind == TypeKind.BINOMIAL;
    }

    private boolean isNumeric(TypeKind kind) {
        return kind == TypeKind.NUMBER || kind == TypeKind.INTEGER;
    }

    private ProbabilityMassFunction evalPMF(ProbabilityMassFunction left, ProbabilityMassFunction right) {
        ProbabilityMassFunctionHelper conv = new ProbabilityMassFunctionHelper();
        return operation.evaluate(
                conv.convertToPMF(left),
                conv.convertToPMF(right));
    }

    private enum TypeKind {
        NORMAL,
        EXPONENTIAL,
        GAMMA,
        LOGNORMAL,
        PDF,
        PMF,
        POISSON,
        BERNOULLI,
        BINOMIAL,
        INT_PMF,
        INTEGER,
        NUMBER,
        OTHER
    }

    private TypeKind classify(Object o) {
        if (o instanceof NormalDistribution)
            return TypeKind.NORMAL;
        if (o instanceof ExponentialDistribution)
            return TypeKind.EXPONENTIAL;
        if (o instanceof GammaDistribution)
            return TypeKind.GAMMA;
        if (o instanceof LognormalDistribution)
            return TypeKind.LOGNORMAL;
        if (o instanceof ProbabilityDensityFunction)
            return TypeKind.PDF;
        if (o instanceof IntProbabilityMassFunction)
            return TypeKind.INT_PMF;
        if (o instanceof PoissonDistribution)
            return TypeKind.POISSON;
        if (o instanceof BernoulliDistribution)
            return TypeKind.BERNOULLI;
        if (o instanceof BinomialDistribution)
            return TypeKind.BINOMIAL;
        if (o instanceof ProbabilityMassFunction)
            return TypeKind.PMF;
        if (o instanceof Integer)
            return TypeKind.INTEGER; // Integer before Number
        if (o instanceof Number)
            return TypeKind.NUMBER;
        return TypeKind.OTHER;
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
