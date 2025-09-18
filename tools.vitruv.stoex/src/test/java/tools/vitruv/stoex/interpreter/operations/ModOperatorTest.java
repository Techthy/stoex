package tools.vitruv.stoex.interpreter.operations;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ModOperatorTest {

    @Test
    public void testModIntegers() {
        ModOperation modOperation = new ModOperation();
        int result = modOperation.evaluate(10, 3);
        assertEquals(1, result);
    }

    @Test
    public void testModDoubles() {
        ModOperation modOperation = new ModOperation();
        double result = modOperation.evaluate(10.5, 3.0);
        assertEquals(1.5, result, 0.0001);
    }
}
