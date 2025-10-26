package tools.vitruv.stoex.interpreter.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.DiscreteUniformDistribution;
import tools.vitruv.stoex.stoex.IntProbabilityMassFunction;
import tools.vitruv.stoex.stoex.IntSample;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class DispatcherTest {

    @Test
    @DisplayName("Should add number and sampled distribution")
    public void testAddDoubleAndSampledDistribution() {
        SampledDistribution dist = StoexFactory.eINSTANCE.createSampledDistribution();
        dist.getValues().add(5.0);
        dist.getValues().add(7.0);
        double value = 3.0;
        Dispatcher dispatcher = new Dispatcher(new AddOperation());
        Object result = dispatcher.dispatch(dist, value);
        assertTrue(result instanceof SampledDistribution);
        SampledDistribution sampledResult = (SampledDistribution) result;
        assertEquals(2, sampledResult.getValues().size());
        assertEquals(8.0, sampledResult.getValues().get(0), 0.001);
        assertEquals(10.0, sampledResult.getValues().get(1), 0.001);
    }

    @Test
    @DisplayName("Should add two discrete Probability Mass Functions")
    public void testAddIntPMFs() {
        IntProbabilityMassFunction pmf1 = StoexFactory.eINSTANCE.createIntProbabilityMassFunction();
        IntSample sample1 = StoexFactory.eINSTANCE.createIntSample();
        sample1.setValue(0);
        sample1.setProbability(0.2);
        pmf1.getSamples().add(sample1);
        IntSample sample2 = StoexFactory.eINSTANCE.createIntSample();
        sample2.setValue(1);
        sample2.setProbability(0.8);
        pmf1.getSamples().add(sample2);
        DiscreteUniformDistribution pmf2 = StoexFactory.eINSTANCE.createDiscreteUniformDistribution();
        pmf2.setA(1);
        pmf2.setB(2);

        Dispatcher dispatcher = new Dispatcher(new AddOperation());
        Object result = dispatcher.dispatch(pmf1, pmf2);
        assertTrue(result instanceof IntProbabilityMassFunction);
        IntProbabilityMassFunction intPmfResult = (IntProbabilityMassFunction) result;
        // Expected samples: (1; 0.2), (2; 0.4), (3; 0.4)
        assertEquals(3, intPmfResult.getSamples().size());
        for (IntSample sample : intPmfResult.getSamples()) {
            switch (sample.getValue()) {
                case 1 -> assertEquals(0.1, sample.getProbability(), 0.001);
                case 2 -> assertEquals(0.5, sample.getProbability(), 0.001);
                case 3 -> assertEquals(0.4, sample.getProbability(), 0.001);
                default -> {
                    fail("Unexpected sample value: " + sample.getValue());
                }
            }
        }
    }

}
