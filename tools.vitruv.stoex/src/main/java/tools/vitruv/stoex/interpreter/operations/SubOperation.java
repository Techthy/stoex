package tools.vitruv.stoex.interpreter.operations;

import tools.vitruv.stoex.stoex.NormalDistribution;
import tools.vitruv.stoex.stoex.StoexFactory;

public class SubOperation {

    public int evaluate(int left, int right) {
        return left - right;
    }

    public double evaluate(double left, double right) {
        return left - right;
    }

    public NormalDistribution evaluate(NormalDistribution left, NormalDistribution right) {
        NormalDistribution result = StoexFactory.eINSTANCE.createNormalDistribution();
        result.setMu(left.getMu() - right.getMu());
        result.setSigma(Math.sqrt(left.getSigma() * left.getSigma() + right.getSigma() * right.getSigma()));
        return result;
    }

}
