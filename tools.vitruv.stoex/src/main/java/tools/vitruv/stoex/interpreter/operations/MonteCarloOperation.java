package tools.vitruv.stoex.interpreter.operations;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

public class MonteCarloOperation {

    private final Random random = new Random();

    // public SampledDistribution addDistributions(SampledDistribution dist1,
    // SampledDistribution dist2) {
    // // Perform Monte Carlo addition of two sampled distributions
    // double[] samples1 =
    // dist1.getValues().stream().mapToDouble(Double::doubleValue).toArray();
    // double[] samples2 =
    // dist2.getValues().stream().mapToDouble(Double::doubleValue).toArray();
    // double[] summedSamples = evaluateTermOperation(samples1, samples2, 100000,
    // MonteCarloTermOperations.ADD);
    // SampledDistribution result =
    // StoexFactory.eINSTANCE.createSampledDistribution();
    // result.getValues().addAll(Arrays.stream(summedSamples).boxed().toList());
    // return result;
    // }

    /**
     * Performs Monte Carlo addition of two distributions represented by sample
     * arrays.
     *
     * @param dist1      Samples of the first distribution
     * @param dist2      Samples of the second distribution
     * @param numSamples Number of Monte Carlo samples to generate
     * @param operation  The term operation to perform (ADD, SUB, MUL, DIV)
     * @return Array of samples representing the summed distribution
     */
    public double[] evaluateTermOperation(double[] dist1, double[] dist2,
            int numSamples, ProbabilityFunctionOperations operation) {
        double[] result = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double sample1 = dist1[random.nextInt(dist1.length)];
            double sample2 = dist2[random.nextInt(dist2.length)];

            switch (operation) {
                case ADD:
                    result[i] = sample1 + sample2;
                    break;
                case SUB:
                    result[i] = sample1 - sample2;
                    break;
                case MUL:
                    result[i] = sample1 * sample2;
                    break;
                case DIV:
                    if (sample2 == 0) {
                        throw new ArithmeticException("Division by zero encountered in Monte Carlo operation.");
                    }
                    result[i] = sample1 / sample2;
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown term operation: " + operation);
            }
        }

        return result;
    }

    /**
     * Computes a histogram for a sample array.
     *
     * @param samples Array of samples
     * @param numBins Number of bins in the histogram
     * @return Histogram counts and bin edges
     */
    public double[][] histogram(double[] samples, int numBins) {
        double min = Arrays.stream(samples).min().orElse(0);
        double max = Arrays.stream(samples).max().orElse(1);
        double binWidth = (max - min) / numBins;

        double[] counts = new double[numBins];
        double[] binEdges = new double[numBins + 1];
        for (int i = 0; i <= numBins; i++) {
            binEdges[i] = min + i * binWidth;
        }

        for (double sample : samples) {
            int bin = (int) ((sample - min) / binWidth);
            if (bin >= numBins)
                bin = numBins - 1; // last bin
            counts[bin]++;
        }

        return new double[][] { counts, binEdges };
    }

    public void printHistogram(double[] results, int bins) {
        double min = DoubleStream.of(results).min().orElse(0.0);
        double max = DoubleStream.of(results).max().orElse(0.0);
        double binWidth = (max - min) / bins;

        int[] histogram = new int[bins];

        for (double value : results) {
            int binIndex = Math.min((int) ((value - min) / binWidth), bins - 1);
            histogram[binIndex]++;
        }

        System.out.println("\nHistogramm of the Results:");
        int maxCount = Arrays.stream(histogram).max().orElse(1);
        int resultCount = results.length;

        for (int i = 0; i < bins; i++) {
            double binStart = min + i * binWidth;
            double binEnd = binStart + binWidth;
            int barLength = (histogram[i] * 50) / maxCount; // Normiert auf 50 Zeichen
            double probability = Math.round((histogram[i] / (double) resultCount) * 10000.0) / 100.0;

            System.out.printf("[%6.2f, %6.2f), %6.2f %%  |", binStart, binEnd, probability);
            for (int j = 0; j < barLength; j++) {
                System.out.print("█");
            }
            System.out.printf(" (%d)\n", histogram[i]);
        }
    }
}
