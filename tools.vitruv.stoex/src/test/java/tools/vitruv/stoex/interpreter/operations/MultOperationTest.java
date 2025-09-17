package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class MultOperationTest {

    @Test
    @DisplayName("Should multiply Normal Distribution with double")
    public void testMultiplyNormalDistributionWithDouble() {
        // Arrange
        NormalDistribution normalDist = StoexFactory.eINSTANCE.createNormalDistribution();
        normalDist.setMu(5.0);
        normalDist.setSigma(1.0);
        double valueToMultiply = 2.0;

        // Act
        NormalDistribution result = new MultOperation().evaluate(normalDist, valueToMultiply);

        // Assert
        assertEquals(10.0, result.getMu(), 0.001);
        assertEquals(2.0, result.getSigma(), 0.001);
    }

    @Test
    @DisplayName("Should multiply int with Normal Distribution")
    public void testMultiplyIntWithNormalDistribution() {
        // Arrange
        NormalDistribution normalDist = StoexFactory.eINSTANCE.createNormalDistribution();
        normalDist.setMu(0.36663);
        normalDist.setSigma(0.015);
        int valueToMultiply = 27;
        // Act
        NormalDistribution result = new MultOperation().evaluate(valueToMultiply, normalDist);
        // Assert
        assertEquals(9.86701, result.getMu(), 0.001);
        assertEquals(0.4050, result.getSigma(), 0.001);
    }
}