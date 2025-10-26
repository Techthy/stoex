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

public interface Operation {

    public int evaluate(int left, int right);

    public double evaluate(double left, double right);

    public ProbabilityDensityFunction evaluate(ExponentialDistribution left, ExponentialDistribution right);

    public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right);

    public SampledDistribution evaluate(double[] left, double[] right);

    public ProbabilityDensityFunction evaluate(GammaDistribution left, GammaDistribution right);

    public NormalDistribution evaluate(NormalDistribution left, double right);

    public NormalDistribution evaluate(double left, NormalDistribution right);

    public SampledDistribution evaluate(double[] left, double right);

    public SampledDistribution evaluate(double left, double[] right);

    public PoissonDistribution evaluate(PoissonDistribution left, PoissonDistribution right);

    public ProbabilityMassFunction evaluate(BernoulliDistribution left, BernoulliDistribution right);

    public ProbabilityMassFunction evaluate(BinomialDistribution left,
            BinomialDistribution right);

    public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left,
            IntProbabilityMassFunction right);

    public IntProbabilityMassFunction evaluate(IntProbabilityMassFunction left, int right);

    public IntProbabilityMassFunction evaluate(int left, IntProbabilityMassFunction right);

}
