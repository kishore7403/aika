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
package network.aika.neuron.activation;

import network.aika.Thought;
import network.aika.neuron.Range;
import network.aika.neuron.Synapse;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.BranchBindingSignal;
import network.aika.neuron.bindingsignal.PatternBindingSignal;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.steps.activation.Linking;
import network.aika.steps.activation.TemplateLinking;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternActivation extends Activation<PatternNeuron> {

    protected Map<Activation<?>, PatternBindingSignal> reverseBindingSignals = new TreeMap<>();


    protected PatternActivation(int id, PatternNeuron n) {
        super(id, n);
    }

    public PatternActivation(int id, Thought t, PatternNeuron patternNeuron) {
        super(id, t, patternNeuron);
    }

    public void registerReverseBindingSignal(Activation targetAct, PatternBindingSignal bindingSignal) {
        reverseBindingSignals.put(targetAct, bindingSignal);

        Linking.add(targetAct, bindingSignal);
        TemplateLinking.add(targetAct, bindingSignal);
    }

    @Override
    public Stream<PatternBindingSignal> getReverseBindingSignals() {
        return reverseBindingSignals.values().stream();
    }

    @Override
    public void init(Synapse originSynapse, Activation originAct) {
        super.init(originSynapse, originAct);
        addBindingSignal(new PatternBindingSignal(this, (byte) 0));
    }

    public boolean checkPropagatePatternBindingSignal(PatternBindingSignal bs) {
        return bs.getOriginActivation() == this;
    }

    public boolean isSelfRef(Activation iAct) {
        return reverseBindingSignals.containsKey(iAct);
    }

    @Override
    public Range getRange() {
        return getBranchBindingSignals().values().stream()
                .map(s -> s.getOriginActivation().getRange())
                .reduce(
                        new Range(0, 0),
                        (a, s) -> Range.join(a, s)
                );
    }
}
