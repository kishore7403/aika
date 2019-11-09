package network.aika.neuron;

import network.aika.Model;
import network.aika.Utils;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.search.Option;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;



public abstract class TSynapse extends Synapse {

    private double binaryFrequencyIPosOPos;
    private double frequencyIPosOPos;
    private double frequencyIPosONeg;
    private double frequencyINegOPos;
    private double frequencyINegONeg;
    public double N;
    protected boolean needsFrequencyUpdate;

    protected double countValueIPosOPos;
    protected double countValueIPosONeg;
    protected double countValueINegOPos;
    protected double countValueINegONeg;
    protected boolean needsCountUpdate;

    public int lastCount;

    public double lengthSum;
    public int numActs;

    public int numCounts = 0;



    public TSynapse(Neuron input, Neuron output, Integer id) {
        super(input, output, id);
        this.lastCount = 0;
    }


    public TSynapse(Neuron input, Neuron output, Integer id, int lastCount) {
        super(input, output, id);
        this.lastCount = lastCount;
    }


    public double getCoverage() {
        return Math.max(0.0, Math.min(1.0, getCoverageIntern()));
    }


    private double getCoverageIntern() {
        return getWeight() / getOutput().get().getBias();
    }


    public double computeMaxRelationWeights() {
        return 0;
    }


    public void initCountValues() {
        countValueIPosOPos = 0.0;
        countValueIPosONeg = 0.0;
        countValueINegOPos = 0.0;
        countValueINegONeg = 0.0;
        needsCountUpdate = true;
    }


    public void updateCountValue(Option io, Option oo) {
        double inputValue = io != null ? io.getState().value : 0.0;
        double outputValue = oo != null ? oo.getState().value : 0.0;

        if(!needsCountUpdate) {
            return;
        }
        needsCountUpdate = false;

        double optionProp = (io != null ? io.getP() : 1.0) * (oo != null ? oo.getP() : 1.0);

        countValueIPosOPos += (Sign.POS.getX(inputValue) * Sign.POS.getX(outputValue) * optionProp);
        countValueIPosONeg += (Sign.POS.getX(inputValue) * Sign.NEG.getX(outputValue) * optionProp);
        countValueINegOPos += (Sign.NEG.getX(inputValue) * Sign.POS.getX(outputValue) * optionProp);
        countValueINegONeg += (Sign.NEG.getX(inputValue) * Sign.NEG.getX(outputValue) * optionProp);

        needsFrequencyUpdate = true;
    }


    public void updateFrequencies(double alpha, Activation iAct, Activation oAct) {
        if(!needsFrequencyUpdate) {
            return;
        }
        needsFrequencyUpdate = false;

        double stepsWithin;
        double stepsBefore;
        if(iAct != null) {
            int beginPos = StatUtil.getCurrentPos(iAct, BEGIN);
            int endPos = StatUtil.getCurrentPos(iAct, END);

            stepsWithin = endPos - beginPos;
            stepsBefore = beginPos - lastCount;
            lastCount = endPos;

            lengthSum += stepsWithin;
            numActs++;
        } else {
            stepsWithin = getAvgLength();

            int endPos = StatUtil.getCurrentPos(oAct, END);

            stepsBefore = (endPos - stepsWithin) - lastCount;
            lastCount = endPos;
        }

        frequencyIPosOPos = (stepsWithin * countValueIPosOPos) + (alpha * frequencyIPosOPos);
        frequencyIPosONeg = (stepsWithin * countValueIPosONeg) + (alpha * frequencyIPosONeg);
        frequencyINegOPos = (stepsWithin * countValueINegOPos) + (alpha * frequencyINegOPos);
        frequencyINegONeg = stepsBefore + (stepsWithin * countValueINegONeg) + (alpha * frequencyINegONeg);

        binaryFrequencyIPosOPos = (countValueIPosOPos > 0.0 ? 1.0 : 0.0) + (alpha * binaryFrequencyIPosOPos);

        N = stepsBefore + stepsWithin + (alpha * N);

        numCounts++;
    }


    public void setFrequency(double fPP, double fPN, double fNP, double fNN) {
        frequencyIPosOPos = fPP;
        frequencyIPosONeg = fPN;
        frequencyINegOPos = fNP;
        frequencyINegONeg = fNN;
    }


    public double[] getPXiGivenXout() {
        double freqXoutPos = frequencyIPosOPos + frequencyINegOPos;
        double freqXoutNeg = frequencyIPosONeg + frequencyINegONeg;

        return new double[] {
                frequencyIPosOPos / freqXoutPos,
                frequencyINegOPos / freqXoutPos,
                frequencyIPosONeg / freqXoutNeg,
                frequencyINegONeg / freqXoutNeg
        };
    }


    public double[] getPXiXout() {
        return new double[] {
                frequencyIPosOPos / N,
                frequencyINegOPos / N,
                frequencyIPosONeg / N,
                frequencyINegONeg / N
        };
    }

    public double[] getCounts() {
        return new double[]{
                frequencyIPosOPos,
                frequencyINegOPos,
                frequencyIPosONeg,
                frequencyINegONeg
        };
    }

    public double getAvgLength() {
        return lengthSum / (double) numActs;
    }


    public String freqToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IPosOPos:" + Utils.round(frequencyIPosOPos));
        sb.append(" INegOPos:" + Utils.round(frequencyINegOPos));
        sb.append(" IPosONeg:" + Utils.round(frequencyIPosONeg));
        sb.append(" INegONeg:" + Utils.round(frequencyINegONeg));
        return sb.toString();
    }

    public String propToString() {
        double[] pXiXout = getPXiXout();
        StringBuilder sb = new StringBuilder();
        sb.append("IPosOPos:" + Utils.round(pXiXout[0]));
        sb.append(" INegOPos:" + Utils.round(pXiXout[1]));
        sb.append(" IPosONeg:" + Utils.round(pXiXout[2]));
        sb.append(" INegONeg:" + Utils.round(pXiXout[3]));
        return sb.toString();
    }


    public String toString() {
        return super.toString() + " f:(" + freqToString() + ")";
    }


    public String pXiToString() {
        double[] pXiXout = getPXiXout();
        StringBuilder sb = new StringBuilder();
        sb.append("Pos:" + Utils.round(pXiXout[0] + pXiXout[2]));
        sb.append(" Neg:" + Utils.round(pXiXout[1] + pXiXout[3]));
        return sb.toString();
    }


    public String pXoutToString() {
        double[] pXiXout = getPXiXout();
        StringBuilder sb = new StringBuilder();
        sb.append("Pos:" + Utils.round(pXiXout[0] + pXiXout[1]));
        sb.append(" Neg:" + Utils.round(pXiXout[2] + pXiXout[3]));
        return sb.toString();
    }


    public double getReliability() {
        return binaryFrequencyIPosOPos >= TNeuron.RELIABILITY_THRESHOLD ? Math.log(binaryFrequencyIPosOPos - (TNeuron.RELIABILITY_THRESHOLD - 1.0)) : 0.0;
    }


    // TODO: metaSynapse
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(frequencyIPosOPos);
        out.writeDouble(frequencyIPosONeg);
        out.writeDouble(frequencyINegOPos);
        out.writeDouble(frequencyINegONeg);
        out.writeDouble(N);

        out.writeInt(lastCount);
    }


    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        frequencyIPosOPos = in.readDouble();
        frequencyIPosONeg = in.readDouble();
        frequencyINegOPos = in.readDouble();
        frequencyINegONeg = in.readDouble();
        N = in.readDouble();

        lastCount = in.readInt();
    }


    public static class Builder extends Synapse.Builder {
        protected SynapseFactory getSynapseFactory() {
            return (input, output, id) -> new TSynapse(input, output, id);
        }
    }
}
