package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.ExponentialDistribution;
import tools.vitruv.stoex.stoex.GammaDistribution;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

@DisplayName("Add Operation Tests")
public class AddOperationTest {

    private AddOperation addOperation;

    @BeforeEach
    public void setUp() {
        addOperation = new AddOperation();
    }

    @Test
    @DisplayName("Should add two integers")
    public void testAddIntegers() {
        int result = addOperation.evaluate(2, 3);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Should add two doubles")
    public void testAddDoubles() {
        double result = addOperation.evaluate(2.5, 3.5);
        assertEquals(6.0, result, 0.001);
    }

    @Test
    @DisplayName("Should add double and integer")
    public void testAddDoubleAndInteger() {
        double result = addOperation.evaluate(2.5, 3);
        assertEquals(5.5, result, 0.001);
    }

    @Test
    @DisplayName("Should add two Normal Distributions")
    public void testAddNormalDistributions() {
        NormalDistribution dist1 = StoexFactory.eINSTANCE.createNormalDistribution();
        dist1.setMu(0);
        dist1.setSigma(1);
        NormalDistribution dist2 = StoexFactory.eINSTANCE.createNormalDistribution();
        dist2.setMu(1);
        dist2.setSigma(2);
        NormalDistribution result = addOperation.evaluate(dist1, dist2);

        // sum of normal distributions the mean is simply the sum of both means
        assertEquals(1, result.getMu(), 0.001);
        // the variance is the sum of both variances (sigma^2)
        assertEquals(Math.sqrt(1 + 4), result.getSigma(), 0.001);
    }

    @Test
    @DisplayName("Should add two Exponential Distributions with **same** lambda")
    public void testAddExponentialDistributionsSameLambda() {
        ExponentialDistribution dist1 = StoexFactory.eINSTANCE.createExponentialDistribution();
        dist1.setLambda(0.5);
        ExponentialDistribution dist2 = StoexFactory.eINSTANCE.createExponentialDistribution();
        dist2.setLambda(0.5);
        ProbabilityDensityFunction result = addOperation.evaluate(dist1, dist2);

        assertTrue(result instanceof GammaDistribution);
        GammaDistribution gammaResult = (GammaDistribution) result;
        assertEquals(2, gammaResult.getAlpha(), 0.001);
        assertEquals(1 / 0.5, gammaResult.getTheta(), 0.001);
    }

    @Test
    @DisplayName("Should add two Exponential Distributions with **different** lambda")
    public void testAddExponentialDistributionsDifferentLambda() {
        // Arrange
        ExponentialDistribution dist1 = StoexFactory.eINSTANCE.createExponentialDistribution();
        dist1.setLambda(0.5);
        ExponentialDistribution dist2 = StoexFactory.eINSTANCE.createExponentialDistribution();
        dist2.setLambda(1.0);

        // Act
        ProbabilityDensityFunction result = addOperation.evaluate(dist1, dist2);

        // Assert
        assertTrue(result instanceof SampledDistribution);

        SampledDistribution sampledResult = (SampledDistribution) result;
        MonteCarloOperation operation = new MonteCarloOperation();
        double[] valuesArray = sampledResult.getValues().stream().mapToDouble(Double::doubleValue).toArray();
        double[][] histogram = operation.histogram(valuesArray, 10);
        for (int i = 0; i < histogram[0].length - 1; i++) {
            assertTrue(histogram[0][i] + 10 >= histogram[0][i + 1]);
        }
    }

}
