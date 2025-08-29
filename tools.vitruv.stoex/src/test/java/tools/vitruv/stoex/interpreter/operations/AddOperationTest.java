package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.BoxedPDF;
import tools.vitruv.stoex.stoex.ContinuousSample;
import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

@DisplayName("Add Operation Tests")
public class AddOperationTest {

    @Test
    @DisplayName("Should add two normal distributions")
    public void testAddNormalDistributions() {
        // Arrange
        NormalDistribution input1 = StoexFactory.eINSTANCE.createNormalDistribution();
        input1.setMu(1.0);
        input1.setSigma(2.0);

        NormalDistribution input2 = StoexFactory.eINSTANCE.createNormalDistribution();
        input2.setMu(3.0);
        input2.setSigma(4.0);

        AddOperation addOperation = new AddOperation();

        // Act
        NormalDistribution result = addOperation.evaluate(input1, input2);

        // Assert
        assertNotNull(result);
        assertEquals(4.0, result.getMu(), 0.001);
        assertEquals(Math.sqrt(Math.pow(2.0, 2) + Math.pow(4.0, 2)), result.getSigma(), 0.001);
    }

    public void testAddBoxedPDF() {
        // Arrange
        BoxedPDF input1 = StoexFactory.eINSTANCE.createBoxedPDF();
        ContinuousSample sample1_1 = StoexFactory.eINSTANCE.createContinuousSample();
        sample1_1.setValue(0.2);
        ContinuousSample sample1_2 = StoexFactory.eINSTANCE.createContinuousSample();
        sample1_2.setValue(0.5);
        input1.getSamples().add(sample1_1);
        input1.getSamples().add(sample1_2);

        BoxedPDF input2 = StoexFactory.eINSTANCE.createBoxedPDF();
        ContinuousSample sample2_1 = StoexFactory.eINSTANCE.createContinuousSample();
        sample2_1.setValue(0.3);
        ContinuousSample sample2_2 = StoexFactory.eINSTANCE.createContinuousSample();
        sample2_2.setValue(0.4);
        input2.getSamples().add(sample2_1);
        input2.getSamples().add(sample2_2);

        AddOperation addOperation = new AddOperation();

        // Act
        BoxedPDF result = addOperation.evaluate(input1, input2);

        // Assert
        assertNotNull(result);
        // The resulting BoxedPDF should have a size of input1.size + input2.size - 1
        assertEquals(input1.getSamples().size() + input2.getSamples().size() - 1, result.getSamples().size());
    }

}
