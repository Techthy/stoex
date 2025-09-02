package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import tools.vitruv.stoex.stoex.BoxedPDF;
import tools.vitruv.stoex.stoex.ContinuousSample;
import tools.vitruv.stoex.stoex.StoexFactory;

public class MonteCarloContinuousTest {

    private final int NUM_ITERATIONS = 1000;

    @Test
    @DisplayName("Test for sampled uniform distribution")
    public void testSampledUniformDistribution() {
        BoxedPDF uniformPDFOne = StoexFactory.eINSTANCE.createBoxedPDF();
        BoxedPDF uniformPDFTwo = StoexFactory.eINSTANCE.createBoxedPDF();

        for (int i = 1; i <= 100; i++) {
            // Create Samples for first uniform PDF
            ContinuousSample sampleOne = StoexFactory.eINSTANCE.createContinuousSample();
            sampleOne.setValue((i / 100.0));
            sampleOne.setProbability(0.01);
            uniformPDFOne.getSamples().add(sampleOne);

            // Create Samples for second uniform PDF
            ContinuousSample sampleTwo = StoexFactory.eINSTANCE.createContinuousSample();
            sampleTwo.setValue(i / 100.0);
            sampleTwo.setProbability(0.01);
            uniformPDFTwo.getSamples().add(sampleTwo);
        }

        for (ContinuousSample sample : uniformPDFOne.getSamples()) {
            System.out.println(
                    "Uniform PDF One - Value: " + sample.getValue() + ", Probability: " + sample.getProbability());
        }

        // for (ContinuousSample sample : uniformPDFTwo.getSamples()) {
        // System.out.println(
        // "Uniform PDF Two - Value: " + sample.getValue() + ", Probability: " +
        // sample.getProbability());
        // }

        MonteCarloContinuous monteCarlo = new MonteCarloContinuous();
        monteCarlo.printHistogram(uniformPDFOne, 10);
        monteCarlo.printHistogram(uniformPDFTwo, 10);
        BoxedPDF result = monteCarlo.monteCarloEstimation(uniformPDFOne, uniformPDFTwo);

        for (ContinuousSample sample : result.getSamples()) {
            System.out.println("Value: " + Math.round(sample.getValue() * 100.0) / 100.0 + ", PDF - Probability: "
                    + sample.getProbability());
        }

        // BoxedCDF CDF = monteCarlo.convertToCDF(result);
        // for (ContinuousSample sample : CDF.getSamples()) {
        // System.out.println("Value: " + Math.round(sample.getValue() * 100.0) / 100.0
        // + ", CDF Probability: " +
        // Math.round(sample.getProbability() * 100.0) / 100.0);
        // }

        monteCarlo.printHistogram(result, 100);

        // Make sure that the CDF is correct

        assertNotNull("Result should not be null", result);
        assertEquals("Result should have the same number of samples", uniformPDFOne.getSamples().size(),
                result.getSamples().size());

    }

}

// *
// DoublePDF[(0.5;0.1)(1.0;0.1)(1.5;0.1)(2.0;0.1)(2.5;0.1)(3.0;0.1)(3.5;0.1)(4.0;0.1)(4.5;0.1)(5.0;0.1)]
