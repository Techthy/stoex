package tools.vitruv.stoex.interpreter.operations;

import java.util.Random;

import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;

public class SampleHelper {

    private static final Random random = new Random();
    private static final int DEFAULT_NUM_SAMPLES = 10000;

    public double[] getSamples(SampledDistribution distribution) {
        return distribution.getValues().stream().mapToDouble(Double::doubleValue).toArray();
    }

    public double[] getSamples(NormalDistribution distribution) {

        double[] samples = new double[DEFAULT_NUM_SAMPLES];

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = distribution.getMu() + distribution.getSigma() *
                    random.nextGaussian();

            samples[i] = value;
        }

        return samples;
    }

    public double[] getSamples(ExponentialDistribution exponentialDistribution) {

        double[] samples = new double[DEFAULT_NUM_SAMPLES];

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = -Math.log(1 - random.nextDouble()) / exponentialDistribution.getLambda();

            samples[i] = value;
        }

        return samples;
    }

    public double[] getSamples(GammaDistribution distribution) {

        double[] samples = new double[DEFAULT_NUM_SAMPLES];

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = sampleGamma(distribution.getAlpha(), distribution.getTheta());

            samples[i] = value;
        }

        return samples;
    }

    public double sampleGamma(double shape, double scale) {
        if (shape < 1) {
            // Weibull algorithm
            double c = (1.0 / shape);
            double d = ((1 - shape) * Math.pow(shape, shape / (1 - shape)));
            while (true) {
                double u = random.nextDouble();
                double v = random.nextDouble();
                double z = -Math.log(u);
                double e = -Math.log(v);
                if (z + e >= d) {
                    return scale * Math.pow(z, c);
                }
            }
        } else {
            // Marsaglia and Tsang's method
            double d = shape - 1.0 / 3.0;
            double c = 1.0 / Math.sqrt(9.0 * d);
            while (true) {
                double x = random.nextGaussian();
                double v = 1.0 + c * x;
                if (v <= 0)
                    continue;
                v = v * v * v;
                double u = random.nextDouble();
                if (u < 1.0 - 0.0331 * x * x * x * x)
                    return scale * d * v;
                if (Math.log(u) < 0.5 * x * x + d * (1 - v + Math.log(v)))
                    return scale * d * v;
            }
        }

    }

    public double[] getSamples(LognormalDistribution distribution) {

        double[] samples = new double[DEFAULT_NUM_SAMPLES];

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = Math.exp(distribution.getMu() + distribution.getSigma() *
                    random.nextGaussian());

            samples[i] = value;
        }

        return samples;
    }

    public double[] getSamples(ProbabilityDensityFunction function) {
        if (function instanceof NormalDistribution normalDistribution) {
            return getSamples(normalDistribution);
        } else if (function instanceof ExponentialDistribution exponentialDistribution) {
            return getSamples(exponentialDistribution);
        } else if (function instanceof GammaDistribution gammaDistribution) {
            return getSamples(gammaDistribution);
        } else if (function instanceof SampledDistribution sampledDistribution) {
            return getSamples(sampledDistribution);
        } else if (function instanceof LognormalDistribution lognormalDistribution) {
            return getSamples(lognormalDistribution);
        }
        throw new IllegalArgumentException("Not implemented for the " + function.getClass().getSimpleName());
    }

}
