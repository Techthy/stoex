package tools.vitruv.stoex.interpreter.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.IntSample;
import tools.vitruv.stoex.stoex.StoexFactory;

@DisplayName("Discrete Convolution Tests")
public class DiscreteConvolutionTest {

    @Test
    @DisplayName("Should convolve two IntPMFs")
    public void testConvolveIntPMFs() {
        // Create first IntPMF
        IntProbabilityMassFunction pmf1 = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        IntSample sample1 = StoexFactory.eINSTANCE.createIntSample();
        sample1.setValue(0);
        sample1.setProbability(0.1);
        pmf1.getSamples().add(sample1);
        IntSample sample2 = StoexFactory.eINSTANCE.createIntSample();
        sample2.setValue(1);
        sample2.setProbability(0.3);
        pmf1.getSamples().add(sample2);
        IntSample sample3 = StoexFactory.eINSTANCE.createIntSample();
        sample3.setValue(2);
        sample3.setProbability(0.6);
        pmf1.getSamples().add(sample3);
        // Create second IntPMF
        IntProbabilityMassFunction pmf2 = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        IntSample sample4 = StoexFactory.eINSTANCE.createIntSample();
        sample4.setValue(1);
        sample4.setProbability(0.4);
        pmf2.getSamples().add(sample4);
        IntSample sample5 = StoexFactory.eINSTANCE.createIntSample();
        sample5.setValue(2);
        sample5.setProbability(0.6);
        pmf2.getSamples().add(sample5);
        // Perform convolution
        DiscreteConvolution convolution = new DiscreteConvolution();
        IntProbabilityMassFunction result = convolution.convolve(pmf1, pmf2, ProbabilityFunctionOperations.ADD);
        // Validate results
        // Expected samples: (1; 0.04), (2; 0.22), (3; 0.48), (4; 0.36)
        assertEquals(4, result.getSamples().size());

        for (IntSample sample : result.getSamples()) {
            if (sample.getValue() == 1) {
                assertEquals(0.04, sample.getProbability(), 0.001);
            } else if (sample.getValue() == 2) {
                assertEquals(0.18, sample.getProbability(), 0.001);
            } else if (sample.getValue() == 3) {
                assertEquals(0.42, sample.getProbability(), 0.001);
            } else if (sample.getValue() == 4) {
                assertEquals(0.36, sample.getProbability(), 0.001);
            } else {
                fail("Unexpected sample value: " + sample.getValue());
            }
        }
    }
}