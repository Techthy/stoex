package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class PowerOperatorTest {

    private PowerOperator powerOperator;

    @BeforeEach
    public void setUp() {
        powerOperator = new PowerOperator();
    }

    @Test
    @DisplayName("Test integer exponentiation")
    public void testEvaluateInt() {
        assertEquals(8, powerOperator.evaluate(2, 3));
    }

    @Test
    @DisplayName("Test double exponentiation")
    public void testEvaluateDouble() {
        assertEquals(8.0, powerOperator.evaluate(2.0, 3.0), 0.001);
    }

    @Test
    @DisplayName("Test NormalDistribution exponentiation")
    public void testEvaluateNormalDistribution() {
        NormalDistribution normalDist = StoexFactory.eINSTANCE.createNormalDistribution();
        normalDist.setMu(30);
        normalDist.setSigma(2);
        Object result = powerOperator.evaluate(normalDist, 2);
        assertTrue(result instanceof SampledDistribution);
        SampledDistribution sampledDist = (SampledDistribution) result;
        int mean = sampledDist.getValues().stream().mapToInt(Double::intValue).sum()
                / sampledDist.getValues().size();
        // Formula for normal distribution squared mean: mu^2 + sigma^2
        assertEquals(904, mean, 5); // Expect mean around 904 with a margin of 5
    }

}
