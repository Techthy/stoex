// package tools.vitruv.stoex.interpreter.operations;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// import tools.vitruv.stoex.stoex.BoxedCDF;
// import tools.vitruv.stoex.stoex.BoxedPDF;
// import tools.vitruv.stoex.stoex.ContinuousSample;
// import tools.vitruv.stoex.stoex.StoexFactory;

// public class MonteCarloContinuous {

// private static final int NUM_ITERATIONS = 10000000;

// public BoxedCDF convertToCDF(BoxedPDF pdf) {
// BoxedCDF cdf = StoexFactory.eINSTANCE.createBoxedCDF();
// List<ContinuousSample> pdfSamples = pdf.getSamples();

// // Make a copy of the samples to avoid modifying the original PDF
// List<ContinuousSample> cdfSamples = new ArrayList<>();
// for (ContinuousSample sample : pdfSamples) {
// ContinuousSample copy = StoexFactory.eINSTANCE.createContinuousSample();
// copy.setValue(sample.getValue());
// copy.setProbability(sample.getProbability());
// cdfSamples.add(copy);
// }
// cdf.getSamples().addAll(cdfSamples);

// // Sort the copied samples by value
// cdfSamples.sort((s1, s2) -> Double.compare(s1.getValue(), s2.getValue()));

// double cumulativeProbability = 0.0;
// for (ContinuousSample sample : cdfSamples) {
// cumulativeProbability += sample.getProbability();
// sample.setProbability(cumulativeProbability);
// }
// // Floating Point Comparison
// double epsilon = 0.02;
// if (Math.abs(cumulativeProbability - 1.0) > epsilon) {
// throw new IllegalArgumentException(
// "Invalid BoxedPDF: CDF does not sum to 1. Actual: " + cumulativeProbability);
// }
// System.out.println("Converted CDF Samples:");
// for (ContinuousSample sample : cdf.getSamples()) {
// System.out
// .println("CDF Sample - Value: " + sample.getValue() + ", Probability: " +
// sample.getProbability());
// }
// System.out.println("Total CDF Probability: " + cumulativeProbability);
// return cdf;
// }

// /**
// * Converts a BoxedPDF to a BoxedCDF, assuming each sample in the PDF
// * defines the upper right corner of a rectangle (i.e., the PDF is a step
// * function
// * where each sample's probability is the area up to and including that
// value).
// * This means the CDF at each sample is the sum of all probabilities for
// values
// * <= sample.getValue().
// */
// public BoxedCDF convertToCDFUpperRight(BoxedPDF pdf) {
// BoxedCDF cdf = StoexFactory.eINSTANCE.createBoxedCDF();
// List<ContinuousSample> pdfSamples = pdf.getSamples();

// // Make a copy of the samples to avoid modifying the original PDF
// List<ContinuousSample> cdfSamples = new ArrayList<>();
// for (ContinuousSample sample : pdfSamples) {
// ContinuousSample copy = StoexFactory.eINSTANCE.createContinuousSample();
// copy.setValue(sample.getValue());
// copy.setProbability(sample.getProbability());
// cdfSamples.add(copy);
// }

// // Sort the copied samples by value
// cdfSamples.sort((s1, s2) -> Double.compare(s1.getValue(), s2.getValue()));

// double cumulativeProbability = 0.0;
// for (ContinuousSample sample : cdfSamples) {
// cumulativeProbability += sample.getProbability();
// sample.setProbability(cumulativeProbability);
// }

// // Add all CDF samples to the result
// cdf.getSamples().addAll(cdfSamples);

// // Floating Point Comparison
// double epsilon = 0.02;
// if (Math.abs(cumulativeProbability - 1.0) > epsilon) {
// throw new IllegalArgumentException("Invalid BoxedPDF: CDF does not sum to
// 1.");
// }
// System.out.println("Converted CDF (Upper Right) Samples:");
// for (ContinuousSample sample : cdf.getSamples()) {
// System.out
// .println("CDF Sample - Value: " + sample.getValue() + ", Probability: " +
// sample.getProbability());
// }
// System.out.println("Total CDF Probability: " + cumulativeProbability);
// return cdf;
// }

// // TODO: also do that for different operators
// /**
// * Performs Monte Carlo estimation for two BoxedPDFs, assuming each sample in
// * the PDF
// * defines the upper right corner of a rectangle (step function
// interpretation).
// * The PDFs are first converted to CDFs using convertToCDFUpperRight.
// */
// public BoxedPDF monteCarloEstimation(BoxedPDF left, BoxedPDF right) {
// BoxedCDF leftCDF = convertToCDFUpperRight(left);
// BoxedCDF rightCDF = convertToCDFUpperRight(right);

// List<Double> samples = new ArrayList<>();

// for (int i = 0; i < NUM_ITERATIONS; i++) {
// ContinuousSample leftSample = drawRandomSample(leftCDF);
// ContinuousSample rightSample = drawRandomSample(rightCDF);
// samples.add(leftSample.getValue() + rightSample.getValue());
// }

// // double minSamples =

// // Calculate histogram bins
// int bins = 100;
// double min =
// samples.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
// double max =
// samples.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
// double binWidth = (max - min) / bins;

// // Count samples in each bin
// int[] binCounts = new int[bins];
// for (double value : samples) {
// int bin = (int) ((value - min) / binWidth);
// if (bin >= bins)
// bin = bins - 1; // last bin inclusive
// binCounts[bin]++;
// }

// // Create BoxedPDF from histogram
// BoxedPDF result = StoexFactory.eINSTANCE.createBoxedPDF();
// for (int i = 0; i < bins; i++) {
// ContinuousSample sample = StoexFactory.eINSTANCE.createContinuousSample();
// // Use upper right corner -> one ahead
// double upperRightCorner = min + (i + 1) * binWidth;
// sample.setValue(upperRightCorner);
// // Probability is count in bin divided by total samples and bin width (PDF)
// sample.setProbability((double) binCounts[i] / (samples.size() * binWidth));
// result.getSamples().add(sample);
// }

// // Normalize BoxedPDF
// double totalProbability =
// result.getSamples().stream().mapToDouble(ContinuousSample::getProbability).sum();
// if (totalProbability > 0) {
// for (ContinuousSample sample : result.getSamples()) {
// sample.setProbability(sample.getProbability() / totalProbability);
// }
// }

// return result;
// }

// public ContinuousSample drawRandomSample(BoxedCDF cdf) {
// int numberOfSamples = cdf.getSamples().size();
// if (numberOfSamples == 0) {
// throw new IllegalArgumentException("CDF is empty");
// }

// // Ziehe eine Zufallszahl im Intervall [0, 1)
// double randomValue = Math.random();

// // Finde das entsprechende Sample in der CDF
// for (ContinuousSample sample : cdf.getSamples()) {
// if (randomValue < sample.getProbability()) {
// return sample;
// }
// }

// // Sollte nie erreicht werden, da die CDF normiert ist
// throw new IllegalStateException("Unreachable");
// }

// // Prints PDF
// /**
// * Prints a histogram for a BoxedPDF, assuming each sample represents the
// upper
// * right corner of a rectangle (step function).
// * The probability for each bin is taken directly from the sample's
// probability
// * (PDF value at the upper edge).
// */
// public void printHistogram(BoxedPDF pdf, int bins) {
// List<ContinuousSample> samples = pdf.getSamples();
// if (samples.isEmpty()) {
// System.out.println("No samples to display.");
// return;
// }

// // Sort samples by value (upper right corners)
// samples.sort((s1, s2) -> Double.compare(s1.getValue(), s2.getValue()));

// double min = 0;
// double max = samples.get(samples.size() - 1).getValue();
// double binWidth = (max - min) / bins;

// // Prepare histogram bins
// double[] binEdges = new double[bins + 1];
// for (int i = 0; i <= bins; i++) {
// binEdges[i] = min + i * binWidth;
// }
// double[] binProbabilities = new double[bins];

// // For each sample (upper right corner), assign its probability to the
// // corresponding bin
// int sampleIdx = 0;
// for (int bin = 0; bin < bins; bin++) {
// double upperEdge = binEdges[bin + 1];
// // Find the sample with value <= upperEdge (since samples are sorted)
// while (sampleIdx < samples.size() && samples.get(sampleIdx).getValue() <=
// upperEdge) {
// // The PDF value at this upper edge is the sample's probability
// binProbabilities[bin] = samples.get(sampleIdx).getProbability();
// sampleIdx++;
// }
// }

// // Convert PDF values to expected counts for visualization
// int[] histogram = new int[bins];
// for (int i = 0; i < bins; i++) {
// histogram[i] = (int) (binProbabilities[i] * NUM_ITERATIONS * binWidth);
// }
// int maxCount = Arrays.stream(histogram).max().orElse(1);

// System.out.println("Histogram (Upper Right Interpretation):");
// for (int i = 0; i < bins; i++) {
// double lower = binEdges[i];
// double upper = binEdges[i + 1];
// String range = String.format("%.2f - %.2f", lower, upper);
// int barLen = (int) (40.0 * histogram[i] / maxCount);
// String bar = "#".repeat(barLen);
// System.out.printf("%-20s | %-40s (%d)%n", range, bar, histogram[i]);
// }
// }

// }
// // public static void main(String[] args) {
// // int numIterations = 100000;
// // Random rand = new Random();

// // // Beispiel-Samples für kontinuierliche Verteilungen
// // // Generieren von 10000 Samples, die einer Normalverteilung ähneln
// // double[] costsSamples = new double[10000];
// // for (int i = 0; i < costsSamples.length; i++) {
// // costsSamples[i] = 50.0 + rand.nextGaussian() * 5.0;
// // }

// // // Generieren von 10000 Samples, die einer Gleichverteilung ähneln
// // double[] pricesSamples = new double[10000];
// // for (int i = 0; i < pricesSamples.length; i++) {
// // pricesSamples[i] = 60.0 + rand.nextDouble() * 20.0;
// // }

// // // Erstellen der Sampler für die beiden Verteilungen
// // DistributionSampler costSampler = new DistributionSampler(costsSamples);
// // DistributionSampler priceSampler = new DistributionSampler(pricesSamples);

// // List<Double> allProfits = new ArrayList<>();

// // // Monte-Carlo-Simulation
// // for (int i = 0; i < numIterations; i++) {
// // double productionCost = costSampler.drawRandomValue();
// // double sellingPrice = priceSampler.drawRandomValue();
// // double profit = sellingPrice - productionCost;
// // allProfits.add(profit);
// // }

// // // Ergebnisse ausgeben
// // printHistogram(allProfits);
// // }

// // /**
// // * Eine Hilfsklasse zum Ziehen von Werten aus einer empirischen Verteilung.
// // */
// // static class DistributionSampler {
// // private final double[] sortedSamples;
// // private final Random random;

// // public DistributionSampler(double[] samples) {
// // this.sortedSamples = Arrays.copyOf(samples, samples.length);
// // Arrays.sort(this.sortedSamples);
// // this.random = new Random();
// // }

// // }

// // /**
// // * Druckt ein textbasiertes Histogramm der Ergebnisse.
// // */
// // private static void printHistogram(List<Double> profits) {
// // double minProfit = profits.stream().min(Double::compare).orElse(0.0);
// // double maxProfit = profits.stream().max(Double::compare).orElse(0.0);

// // int numBins = 20;
// // double binWidth = (maxProfit - minProfit) / numBins;
// // int[] histogram = new int[numBins];

// // for (double profit : profits) {
// // int binIndex = (int) ((profit - minProfit) / binWidth);
// // if (binIndex < 0)
// // binIndex = 0;
// // if (binIndex >= numBins)
// // binIndex = numBins - 1;
// // histogram[binIndex]++;
// // }

// // System.out.println("Ergebnisverteilung (Histogramm) für kontinuierliche
// // Samples:");
// //
// System.out.println("---------------------------------------------------------------");
// // int maxCount = Arrays.stream(histogram).max().orElse(0);

// // for (int i = 0; i < numBins; i++) {
// // double lowerBound = minProfit + i * binWidth;
// // double upperBound = lowerBound + binWidth;
// // String range = String.format("%.2f - %.2f", lowerBound, upperBound);
// // int barLength = (int) (40.0 * histogram[i] / maxCount);
// // String bar = "#".repeat(barLength);
// // System.out.printf("%-20s | %-40s (%d)%n", range, bar, histogram[i]);
// // }
// // }
// // }