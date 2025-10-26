package tools.vitruv.stoex.interpreter.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

class DivOperationTest {

    private DivOperation op;

    @BeforeEach
    void setUp() {
        op = new DivOperation();
    }

    @Test
    void testIntDivision() {
        assertEquals(2, op.evaluate(6, 3));
        assertEquals(-3, op.evaluate(9, -3));
    }

    @Test
    void testIntDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> op.evaluate(5, 0));
    }

    @Test
    void testDoubleDivision() {
        assertEquals(2.5, op.evaluate(5.0, 2.0), 1e-10);
    }

    @Test
    void testDoubleDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> op.evaluate(1.0, 0.0));
    }

    @Test
    void testNormalDistributionDivideByScalar() {
        NormalDistribution left = StoexFactory.eINSTANCE.createNormalDistribution();
        left.setMu(10.0);
        left.setSigma(2.0);

        NormalDistribution result = op.evaluate(left, 2.0);
        assertEquals(5.0, result.getMu(), 1e-10);
        assertEquals(1.0, result.getSigma(), 1e-10);
    }

    @Test
    void testScalarDivideByNormalDistribution() {
        NormalDistribution right = StoexFactory.eINSTANCE.createNormalDistribution();
        right.setMu(4.0);
        right.setSigma(2.0);

        NormalDistribution result = op.evaluate(8.0, right);
        assertEquals(2.0, result.getMu(), 1e-10); // 8 / 4
        assertEquals(Math.abs(8.0) / 2.0, result.getSigma(), 1e-10);
    }

    @Test
    void testSampleArrayDivideByScalar() {
        double[] samples = new double[] { 2.0, 4.0, 6.0 };
        double divisor = 2.0;

        SampledDistribution sd = op.evaluate(samples, divisor);
        assertEquals(3, sd.getValues().size());
        assertEquals(1.0, sd.getValues().get(0), 1e-10);
        assertEquals(2.0, sd.getValues().get(1), 1e-10);
        assertEquals(3.0, sd.getValues().get(2), 1e-10);
    }

    @Test
    void testLeftZeroOverSamplesThrows() {
        double left = 0.0;
        double[] samplesRight = new double[] { 1.0, 2.0 };
        // According to implementation, dividing left==0 over samples throws
        assertThrows(ArithmeticException.class, () -> op.evaluate(left, samplesRight));
    }
}
