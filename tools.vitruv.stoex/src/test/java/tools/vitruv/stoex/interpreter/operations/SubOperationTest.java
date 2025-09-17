package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class SubOperationTest {

    @Test
    @DisplayName("Should subtract Normal Distribution with double")
    public void testSubtractionWithDouble() {
        // Arrange
        NormalDistribution normalDist = StoexFactory.eINSTANCE.createNormalDistribution();
        normalDist.setMu(5.0);
        normalDist.setSigma(1.0);
        double valueToSubtract = 0.0;

        // Act
        NormalDistribution result = new SubOperation().evaluate(normalDist, valueToSubtract);

        // Assert
        assertEquals(5.0, result.getMu(), 0.001);
        assertEquals(1.0, result.getSigma(), 0.001);
    }
}
