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
package network.aika.neuron.conjunctive;

import network.aika.Model;
import network.aika.neuron.ActivationFunction;
import network.aika.neuron.Neuron;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.ConjunctiveActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;

import static network.aika.neuron.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveNeuron<S extends ConjunctiveSynapse, A extends ConjunctiveActivation> extends Neuron<S, A> {

    private static final Logger log = LoggerFactory.getLogger(ConjunctiveNeuron.class);

    public ConjunctiveNeuron() {
        super();
    }

    public ConjunctiveNeuron(NeuronProvider p) {
        super(p);
    }

    public ConjunctiveNeuron(Model model, boolean addProvider) {
        super(model, addProvider);
    }

    protected void initFromTemplate(ConjunctiveNeuron n) {
        super.initFromTemplate(n);
    }

    /**
     * If the complete bias exceeds the threshold of 0 by itself, the neuron would become constantly active. The training
     * should account for that and reduce the bias back to a level, where the neuron can be blocked again by its input synapses.
     */
    public void limitBias() {
        if(bias.getCurrentValue() > 0.0)
            bias.setAndTriggerUpdate(0.0);
    }

    public void addInactiveLinks(Activation act) {
        inputSynapses
                .stream()
                .filter(s -> !act.inputLinkExists(s))
                .forEach(s ->
                        s.createLink(null, act)
                );
    }

    public ActivationFunction getActivationFunction() {
        return RECTIFIED_HYPERBOLIC_TANGENT;
    }

    public void updateAllowPropagate() {
        Collections.sort(
                inputSynapses,
                Comparator.<ConjunctiveSynapse>comparingDouble(s -> s.getSortingWeight())
        );

        int countAP = 0;
        double sum = getBias().getCurrentValue();
        for(ConjunctiveSynapse s: inputSynapses) {
            if(s.getWeight().getCurrentValue() <= 0.0)
                continue;

            sum += s.getWeight().getCurrentValue();

            s.setAllowPropagate(sum > 0.0);

            if(s.allowPropagate)
                countAP++;
        }

        if(countAP > 1)
            log.warn("countAP: " + countAP + " Activation merging not yet implemented.");
    }
}
