package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.ProbabilityDensityFunction;
import tools.vitruv.stoex.stoex.SampledDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class PowerOperator {

    public int evaluate(int base, int exponent) {
        return (int) Math.pow(base, exponent);
    }

    public double evaluate(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public SampledDistribution evaluate(double[] samples, Number exponent) {
        SampledDistribution result = StoexFactory.eINSTANCE.createSampledDistribution();
        MonteCarloOperation op = new MonteCarloOperation();
        for (double d : op.evaluatePowerOperation(samples, exponent.doubleValue())) {
            result.getValues().add(d);
        }
        return result;
    }

    // TODO DISCRETE case

    public Object evaluate(Object base, Object exponent) {

        if (base instanceof ProbabilityDensityFunction basePDF
                && exponent instanceof Number expNum) {
            SampleHelper helper = new SampleHelper();
            return evaluate(helper.getSamples(basePDF), expNum);
        }

        if (base instanceof Integer baseInt && exponent instanceof Integer expInt) {
            return evaluate((int) baseInt, (int) expInt);
        }

        return evaluate(toDouble(base), toDouble(exponent));
    }

    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof Boolean aBoolean) {
            return aBoolean ? 1.0 : 0.0;
        }
        if (value instanceof String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert string to double: " + value);
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to double");
    }

}
