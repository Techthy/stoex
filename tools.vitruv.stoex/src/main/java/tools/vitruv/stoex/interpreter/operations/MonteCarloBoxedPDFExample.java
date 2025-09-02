
// /**
// * Beispiel für Monte Carlo Simulation mit zwei BoxedPDFs
// * Die werden addiert und das Ergebnis analysiert
// */
// import java.util.*;
// import java.util.stream.DoubleStream;

// public class MonteCarloBoxedPDFExample {

// public static void main(String[] args) {
// // Simulation mit verschiedenen Anzahl von Samples
// int[] sampleSizes = { 1000, 10000, 100000 };

// for (int numSamples : sampleSizes) {
// System.out.println("\n=== Monte Carlo Simulation mit " + numSamples + "
// Samples ===");
// runMonteCarloSimulation(numSamples);
// }
// }

// public static void runMonteCarloSimulation(int numSamples) {
// // Erstelle zwei BoxedPDFs (simuliert)
// BoxedPDFSimulator pdf1 = createNormalDistributionPDF(5.0, 1.0); // μ=5, σ=1
// BoxedPDFSimulator pdf2 = createUniformDistributionPDF(2.0, 4.0); // uniform
// zwischen 2 und 4

// System.out.println("PDF1 (Normal): μ=5.0, σ=1.0");
// System.out.println("PDF2 (Uniform): [2.0, 4.0]");
// System.out.println("Theoretische Summe: μ=8.0, σ≈1.0");

// // Monte Carlo Sampling
// double[] results = new double[numSamples];

// for (int i = 0; i < numSamples; i++) {
// double sample1 = pdf1.drawSample();
// double sample2 = pdf2.drawSample();
// results[i] = sample1 + sample2; // Addition der Samples
// }

// // Statistische Auswertung
// analyzeResults(results);

// // Histogram ausgeben (vereinfacht)
// printHistogram(results, 20);
// }

// private static void analyzeResults(double[] results) {
// double mean = DoubleStream.of(results).average().orElse(0.0);
// double variance = DoubleStream.of(results)
// .map(x -> Math.pow(x - mean, 2))
// .average().orElse(0.0);
// double stdDev = Math.sqrt(variance);
// double min = DoubleStream.of(results).min().orElse(0.0);
// double max = DoubleStream.of(results).max().orElse(0.0);

// System.out.printf("Ergebnisse der Monte Carlo Simulation:\n");
// System.out.printf(" Mittelwert: %.3f\n", mean);
// System.out.printf(" Standardabweichung: %.3f\n", stdDev);
// System.out.printf(" Varianz: %.3f\n", variance);
// System.out.printf(" Minimum: %.3f\n", min);
// System.out.printf(" Maximum: %.3f\n", max);

// // Quantile berechnen
// Arrays.sort(results);
// int n = results.length;
// System.out.printf(" 5%% Quantil: %.3f\n", results[n * 5 / 100]);
// System.out.printf(" 25%% Quantil: %.3f\n", results[n * 25 / 100]);
// System.out.printf(" 50%% Quantil (Median): %.3f\n", results[n * 50 / 100]);
// System.out.printf(" 75%% Quantil: %.3f\n", results[n * 75 / 100]);
// System.out.printf(" 95%% Quantil: %.3f\n", results[n * 95 / 100]);
// }

// private static void printHistogram(double[] results, int bins) {
// double min = DoubleStream.of(results).min().orElse(0.0);
// double max = DoubleStream.of(results).max().orElse(0.0);
// double binWidth = (max - min) / bins;

// int[] histogram = new int[bins];

// for (double value : results) {
// int binIndex = Math.min((int) ((value - min) / binWidth), bins - 1);
// histogram[binIndex]++;
// }

// System.out.println("\nHistogram der Ergebnisse:");
// int maxCount = Arrays.stream(histogram).max().orElse(1);

// for (int i = 0; i < bins; i++) {
// double binCenter = min + (i + 0.5) * binWidth;
// int barLength = (histogram[i] * 50) / maxCount; // Normiert auf 50 Zeichen

// System.out.printf("%6.2f |", binCenter);
// for (int j = 0; j < barLength; j++) {
// System.out.print("█");
// }
// System.out.printf(" (%d)\n", histogram[i]);
// }
// }

// // Vereinfachte BoxedPDF Implementierung für Demonstration
// static class BoxedPDFSimulator {
// private List<Double> values;
// private List<Double> cumulativeProbabilities;
// private Random random = new Random();

// public BoxedPDFSimulator(List<Double> values, List<Double> probabilities) {
// this.values = new ArrayList<>(values);
// this.cumulativeProbabilities = computeCumulativeProbabilities(probabilities);
// }

// private List<Double> computeCumulativeProbabilities(List<Double>
// probabilities) {
// List<Double> cumulative = new ArrayList<>();
// double sum = 0.0;
// for (double prob : probabilities) {
// sum += prob;
// cumulative.add(sum);
// }
// return cumulative;
// }

// public double drawSample() {
// double rand = random.nextDouble();

// // Finde das entsprechende Intervall
// for (int i = 0; i < cumulativeProbabilities.size(); i++) {
// if (rand <= cumulativeProbabilities.get(i)) {
// // Lineare Interpolation innerhalb des Intervalls
// if (i == 0) {
// return values.get(0) * rand / cumulativeProbabilities.get(0);
// } else {
// double prevCumProb = cumulativeProbabilities.get(i - 1);
// double currCumProb = cumulativeProbabilities.get(i);
// double prevValue = values.get(i - 1);
// double currValue = values.get(i);

// double t = (rand - prevCumProb) / (currCumProb - prevCumProb);
// return prevValue + t * (currValue - prevValue);
// }
// }
// }
// return values.get(values.size() - 1); // Fallback
// }
// }

// // Erstelle eine diskretisierte Normalverteilung
// private static BoxedPDFSimulator createNormalDistributionPDF(double mean,
// double stdDev) {
// List<Double> values = new ArrayList<>();
// List<Double> probabilities = new ArrayList<>();

// // Diskretisierung von μ-3σ bis μ+3σ in 20 Schritten
// int numBins = 20;
// double start = mean - 3 * stdDev;
// double end = mean + 3 * stdDev;
// double step = (end - start) / numBins;

// for (int i = 0; i < numBins; i++) {
// double x = start + (i + 1) * step;
// values.add(x);

// // Approximiere Normalverteilung
// double prob = Math.exp(-0.5 * Math.pow((x - mean) / stdDev, 2));
// probabilities.add(prob);
// }

// // Normalisiere Wahrscheinlichkeiten
// double sum = probabilities.stream().mapToDouble(Double::doubleValue).sum();
// probabilities.replaceAll(p -> p / sum);

// return new BoxedPDFSimulator(values, probabilities);
// }

// // Erstelle eine diskretisierte Gleichverteilung
// private static BoxedPDFSimulator createUniformDistributionPDF(double min,
// double max) {
// List<Double> values = new ArrayList<>();
// List<Double> probabilities = new ArrayList<>();

// int numBins = 10;
// double step = (max - min) / numBins;
// double uniformProb = 1.0 / numBins;

// for (int i = 0; i < numBins; i++) {
// values.add(min + (i + 1) * step);
// probabilities.add(uniformProb);
// }

// return new BoxedPDFSimulator(values, probabilities);
// }
// }
