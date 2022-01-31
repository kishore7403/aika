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
package network.aika.neuron.disjunctive;

import network.aika.direction.Direction;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.BindingActivation;
import network.aika.neuron.activation.InhibitoryActivation;
import network.aika.neuron.axons.BindingAxon;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.conjunctive.BindingNeuron;
import network.aika.sign.Sign;
import network.aika.utils.Bound;


/**
 *
 * @author Lukas Molzberger
 */
public class InhibitorySynapse<N extends BindingNeuron & BindingAxon> extends DisjunctiveSynapse<N, InhibitoryNeuron, BindingActivation, InhibitoryActivation> {

    @Override
    public boolean checkBindingSignal(BindingSignal fromBS, Direction dir) {
        return false;
    }

    public Neuron getTemplatePropagateTargetNeuron(Activation<?> act) {
/*
        List<Activation<?>> candidates = act.getPatternBindingSignals().entrySet().stream()
                .map(e -> e.getKey())
                .flatMap(bAct -> bAct.getReverseBindingSignals().entrySet().stream())
                .map(e -> e.getKey())
                .filter(relAct -> relAct.getNeuron() instanceof InhibitoryNeuron)
                .collect(Collectors.toList());
*/
        return getOutput();
    }

    @Override
    protected Bound getProbabilityBound(Sign si, Sign so) {
        return so == Sign.POS ? Bound.LOWER : Bound.UPPER;
    }

    @Override
    public void setModified() {
        getInput().setModified();
    }
}
