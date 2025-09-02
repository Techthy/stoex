package tools.vitruv.stoex.interpreter.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloDiscrete {

    public static void main(String[] args) {
        int numIterations = 100000;
        Random rand = new Random();

        // Diskrete Verteilung 1: Produktionskosten
        double[] costs = { 45.0, 50.0, 55.0 };
        double[] costProbabilities = { 0.2, 0.6, 0.2 };

        // Diskrete Verteilung 2: Verkaufspreise
        double[] prices = { 60.0, 70.0, 80.0, 90.0 };
        double[] priceProbabilities = { 0.1, 0.4, 0.4, 0.1 };

        // CDFs erstellen
        double[] costCDF = createCDF(costProbabilities);
        double[] priceCDF = createCDF(priceProbabilities);

        List<Double> allProfits = new ArrayList<>();

        // Simulation durchführen und alle Ergebnisse speichern
        for (int i = 0; i < numIterations; i++) {
            double productionCost = getRandomValue(costs, costCDF, rand);
            double sellingPrice = getRandomValue(prices, priceCDF, rand);
            double profit = sellingPrice - productionCost;
            allProfits.add(profit);
        }

        // Ergebnisse analysieren und Histogramm erstellen
        printHistogram(allProfits);
    }

    /**
     * Erstellt die kumulative Verteilungsfunktion (CDF) aus Wahrscheinlichkeiten.
     */
    private static double[] createCDF(double[] probabilities) {
        double[] cdf = new double[probabilities.length];
        cdf[0] = probabilities[0];
        for (int i = 1; i < probabilities.length; i++) {
            cdf[i] = cdf[i - 1] + probabilities[i];
        }
        return cdf;
    }

    /**
     * Zieht einen zufälligen Wert aus einer diskreten Verteilung mithilfe der CDF.
     */
    private static double getRandomValue(double[] values, double[] cdf, Random rand) {
        double randomDouble = rand.nextDouble();
        for (int i = 0; i < cdf.length; i++) {
            if (randomDouble <= cdf[i]) {
                return values[i];
            }
        }
        return values[values.length - 1];
    }

    /**
     * Druckt ein textbasiertes Histogramm der Ergebnisse.
     */
    private static void printHistogram(List<Double> profits) {
        // Finden des minimalen und maximalen Gewinns
        double minProfit = profits.stream().min(Double::compare).orElse(0.0);
        double maxProfit = profits.stream().max(Double::compare).orElse(0.0);

        int numBins = 10;
        double binWidth = (maxProfit - minProfit) / numBins;
        int[] histogram = new int[numBins];

        // Zuweisen der Gewinne zu den Histogramm-Bins
        for (double profit : profits) {
            int binIndex = (int) ((profit - minProfit) / binWidth);
            // Sicherstellen, dass der maximale Wert in den letzten Bin fällt
            if (binIndex == numBins) {
                binIndex--;
            }
            if (binIndex >= 0 && binIndex < numBins) {
                histogram[binIndex]++;
            }
        }

        // Drucken des Histogramms
        System.out.println("\nErgebnisverteilung (Histogramm):");
        System.out.println("-----------------------------------");
        int maxCount = 0;
        for (int count : histogram) {
            if (count > maxCount) {
                maxCount = count;
            }
        }

        for (int i = 0; i < numBins; i++) {
            double lowerBound = minProfit + i * binWidth;
            double upperBound = lowerBound + binWidth;
            String range = String.format("%.2f - %.2f", lowerBound, upperBound);
            int barLength = (int) (20.0 * histogram[i] / maxCount);
            String bar = "#".repeat(barLength);
            System.out.printf("%-18s | %-5s (%d)%n", range, bar, histogram[i]);
        }
    }

}
