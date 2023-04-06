/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.elements.synapses;

import network.aika.Model;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.PatternLink;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.Range;
import network.aika.elements.neurons.SampleSpace;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.sign.Sign;
import network.aika.utils.Utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.sign.Sign.NEG;
import static network.aika.sign.Sign.POS;


/**
 *
 * @author Lukas Molzberger
 */
public class PatternSynapse extends ConjunctiveSynapse<
        PatternSynapse,
        BindingNeuron,
        PatternNeuron,
        PatternLink,
        BindingActivation,
        PatternActivation
        >
{

    protected SampleSpace sampleSpace = new SampleSpace();

    public PatternSynapse() {
        super(Scope.SAME);
    }

    @Override
    public PatternLink createLink(BindingActivation input, PatternActivation output) {
        return new PatternLink(this, input, output);
    }

    @Override
    public double getPropagatePreNet(BindingActivation iAct) {
        return getOutput().getBias().getCurrentValue() +
                weight.getCurrentValue() +
                getSumOfLowerWeights();
    }

    public SampleSpace getSampleSpace() {
        return sampleSpace;
    }

    public void setFrequency(Sign inputSign, Sign outputSign, double f) {
        if(inputSign == POS && outputSign == POS) {
            frequencyIPosOPos = f;
        } else if(inputSign == POS && outputSign == NEG) {
            frequencyIPosONeg = f;
        } else if(inputSign == NEG && outputSign == POS) {
            frequencyINegOPos = f;
        } else {
            throw new UnsupportedOperationException();
        }
        setModified();
    }

    public void applyMovingAverage(double alpha) {
        sampleSpace.applyMovingAverage(alpha);
        frequencyIPosOPos *= alpha;
        frequencyIPosONeg *= alpha;
        frequencyINegOPos *= alpha;
        setModified();
    }

    public void updateFrequencyForIandO(boolean inputActive,boolean outputActive){
        if(inputActive && outputActive) {
            frequencyIPosOPos += 1.0;
            setModified();
        } else if(inputActive) {
            frequencyIPosONeg += 1.0;
            setModified();
        } else if(outputActive) {
            frequencyINegOPos += 1.0;
            setModified();
        }
    }

    @Override
    public void count(PatternLink l) {
        double oldN = sampleSpace.getN();

        if(l.getInput() == null)
            return; // TODO: fix

        boolean inputActive = l.getInput() != null && l.getInput().isFired();
        boolean outputActive = l.getOutput().isFired();

        Range absoluteRange = l.getInput().getAbsoluteRange();
        if(absoluteRange == null)
            return;

        sampleSpace.countSkippedInstances(absoluteRange);

        sampleSpace.count();

        if(outputActive) {
            Double alpha = l.getConfig().getAlpha();
            if (alpha != null)
                applyMovingAverage(
                        Math.pow(alpha, sampleSpace.getN() - oldN)
                );
        }

        updateFrequencyForIandO(inputActive,outputActive);
        sampleSpace.updateLastPosition(absoluteRange);
    }


    public double getSurprisal(Sign inputSign, Sign outputSign, Range range, boolean addCurrentInstance) {
        double n = sampleSpace.getN(range);
        double probability = getProbability(inputSign, outputSign, n, addCurrentInstance);
        return Utils.surprisal(probability);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeDouble(frequencyIPosOPos);
        out.writeDouble(frequencyIPosONeg);
        out.writeDouble(frequencyINegOPos);

        sampleSpace.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        frequencyIPosOPos = in.readDouble();
        frequencyIPosONeg = in.readDouble();
        frequencyINegOPos = in.readDouble();

        sampleSpace = SampleSpace.read(in, m);
    }
}
