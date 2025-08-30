package tools.vitruv.stoex.interpreter.operations;

import java.util.Random;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import tools.vitruv.stoex.stoex.BoxedPDF;
import tools.vitruv.stoex.stoex.ContinuousSample;
import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.LognormalDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class SampleHelper {

    private static final Random random = new Random();
    private static final int DEFAULT_NUM_SAMPLES = 1000;

    public EList<ContinuousSample> getSamples(NormalDistribution distribution) {

        EList<ContinuousSample> samples = new BasicEList<>();

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = distribution.getMu() + distribution.getSigma() * random.nextGaussian();

            ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
            sample.setValue(value);
            sample.setProbability(1.0 / DEFAULT_NUM_SAMPLES);
            samples.add(sample);
        }

        return samples;
    }

    public EList<ContinuousSample> getSamples(ExponentialDistribution distribution) {

        EList<ContinuousSample> samples = new BasicEList<>();

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = -Math.log(1 - random.nextDouble()) / distribution.getLambda();

            ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
            sample.setValue(value);
            sample.setProbability(1.0 / DEFAULT_NUM_SAMPLES);
            samples.add(sample);
        }

        return samples;
    }

    public EList<ContinuousSample> getSamples(GammaDistribution distribution) {

        EList<ContinuousSample> samples = new BasicEList<>();

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = sampleGamma(distribution.getAlpha(), distribution.getTheta());

            ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
            sample.setValue(value);
            sample.setProbability(1.0 / DEFAULT_NUM_SAMPLES);
            samples.add(sample);
        }

        return samples;
    }

    public EList<ContinuousSample> getSamples(LognormalDistribution distribution) {

        EList<ContinuousSample> samples = new BasicEList<>();

        for (int i = 0; i < DEFAULT_NUM_SAMPLES; i++) {

            double value = Math.exp(distribution.getMu() + distribution.getSigma() * random.nextGaussian());

            ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
            sample.setValue(value);
            sample.setProbability(1.0 / DEFAULT_NUM_SAMPLES);
            samples.add(sample);
        }

        return samples;
    }

    public EList<ContinuousSample> getSamples(BoxedPDF distribution) {
        return distribution.getSamples();
    }

    public EList<ContinuousSample> getSamples(Object distribution) {
        if (distribution instanceof NormalDistribution normalDistribution) {
            return getSamples(normalDistribution);
        } else if (distribution instanceof ExponentialDistribution exponentialDistribution) {
            return getSamples(exponentialDistribution);
        } else if (distribution instanceof GammaDistribution gammaDistribution) {
            return getSamples(gammaDistribution);
        } else if (distribution instanceof LognormalDistribution lognormalDistribution) {
            return getSamples(lognormalDistribution);
        } else if (distribution instanceof BoxedPDF boxedPDF) {
            return getSamples(boxedPDF);
        }

        throw new IllegalArgumentException(
                "Cannot get Samples. Unsupported distribution type: " +
                        distribution.getClass().getName());
    }

    private double sampleGamma(double shape, double scale) {
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

}
