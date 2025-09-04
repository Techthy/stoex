
package tools.vitruv.stoex.interpreter.operations;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link MonteCarloOperation} class,
 * which performs Monte Carlo operations on probability distributions
 * represented as arrays of doubles.
 * 
 * Note: Due to the stochastic nature of Monte Carlo simulations, some tests may
 * occasionally fail if the delta values are too tight.
 * If failures persist, it indicates an issue with the implementation.
 * Otherwise, consider adjusting the delta values.
 */
public class MonteCarloOperationTest {

    @Test
    @DisplayName("Should add two uniform distributions given as samples in a double[]")
    public void testAdditionMonteCarlo() {
        // Arrange
        MonteCarloOperation operation = new MonteCarloOperation();

        // Create two uniform distributions:
        int n1 = 1000;
        int n2 = 1000;
        double[] dist1 = new double[n1];
        double[] dist2 = new double[n2];
        for (int i = 0; i < n1; i++) {
            dist1[i] = 1.0 + (2.0 - 1.0) * i / (n1 - 1); // Uniform from 1 to 2
        }
        for (int i = 0; i < n2; i++) {
            dist2[i] = 3.0 + (5.0 - 3.0) * i / (n2 - 1); // Uniform from 3 to 5
        }

        // Act
        double[] result = operation.evaluateTermOperation(dist1, dist2, 100000, ProbabilityFunctionOperations.ADD);
        double[][] histogram = operation.histogram(result, 10);

        // Assert
        double minExpected = 4.0; // 1+3
        double maxExpected = 7.0; // 2+5

        double actualMin = histogram[1][0];
        double actualMax = histogram[1][histogram[1].length - 1];

        // Allow small margin due to floating point
        assertEquals(minExpected, actualMin, 0.05);
        assertEquals(maxExpected, actualMax, 0.05);

        // Assert that the bins actually match the correct Mass
        // (probability a value lies within that bin meaning the bounds)
        double[] actualMassPerInterval = { 0.0225, 0.0675, 0.1125, 0.1475, 0.1500, 0.1500, 0.1475, 0.1125, 0.0675,
                0.0225 };
        for (int i = 0; i < histogram[0].length; i++) {
            double probability = Math.round((histogram[0][i] / 100000.0) * 10000.0) / 10000.0;
            assertEquals(actualMassPerInterval[i], probability, 0.005);
        }
    }

    @Test
    @DisplayName("Should subtract two normal distributions given as samples in a double[]")
    public void testSubtractionMonteCarlo() {
        // Arrange
        MonteCarloOperation operation = new MonteCarloOperation();
        Random rand = new Random();

        int n1 = 1000;
        int n2 = 1000;
        double[] dist1 = new double[n1];
        double[] dist2 = new double[n2];
        for (int i = 0; i < n1; i++) {
            dist1[i] = 200 + 3 * rand.nextGaussian(); // N(200, 3)
        }
        for (int i = 0; i < n2; i++) {
            dist2[i] = 10 + 4 * rand.nextGaussian(); // N(10, 4)
        }

        // Act
        double[] result = operation.evaluateTermOperation(dist1, dist2, 100000, ProbabilityFunctionOperations.SUB);

        // Assert
        // N(200, 3) - N(10, 4) = N(190, 5)
        double mean = Arrays.stream(result).average().orElse(0);
        double ssddev = Math.sqrt(Arrays.stream(result).map(x -> Math.pow(x - mean, 2)).average().orElse(0));
        assertEquals(mean, 190.0, 0.5);
        assertEquals(ssddev, 5.0, 0.25);

        // Notice that the values vary too much to do a comparison by bin as above!
    }

    @Test
    @DisplayName("Histogram counts should sum to total number of samples")
    public void testHistogramCountsSum() {
        MonteCarloOperation operation = new MonteCarloOperation();
        double[] samples = new double[500];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = Math.sin(i * 0.01);
        }
        double[][] histogram = operation.histogram(samples, 8);

        double total = 0;
        for (double count : histogram[0]) {
            total += count;
        }
        assertEquals(samples.length, (int) total);
    }

}
