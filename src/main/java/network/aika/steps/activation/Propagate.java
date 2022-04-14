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
package network.aika.steps.activation;

import network.aika.Thought;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.steps.*;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static network.aika.direction.Direction.OUTPUT;


/**
 * The job of the linking phase is to propagate information through the network by creating the required activations and links.
 * Each activation and each link have an corresponding neuron or synapse respectively. Depending on the data set in the
 * document, a neuron might have several activations associated with it. During propagation an input activation
 * causes the creating of a link in one or more output synapses and the creation of an output activation. Initially the value
 * of the input activation and the weight of the synapse might not suffice to activate the output activation. But that might
 * change later on as more input links are added to the activation. New input links are added by the closeCycle method. This
 * method is called by the visitor which follows the links in the activation network to check that both input and output
 * activation of a new link refer to the same object in the input data set.
 *
 * @author Lukas Molzberger
 */
public class Propagate extends Step<Activation> {

    public static void add(BindingSignal bs, boolean template) {
        if(template && !bs.getActivation().getConfig().isTemplatesEnabled())
            return;

        Propagate step = new Propagate(bs, template);

        if(step.hasTargetSynapses())
            Step.add(step);
    }

    private boolean template;
    private List<Synapse> targetSynapses;
    private BindingSignal bindingSignal;

    private Propagate(BindingSignal bs, boolean template) {
        super(bs.getActivation());

        this.template = template;
        this.bindingSignal = bs;

        Activation act = bs.getActivation();
        if(!act.checkAllowPropagate())
            return;

        Neuron<?, ?> n = act.getNeuron();
        targetSynapses = n.getTargetSynapses(OUTPUT, template)
                .filter(s ->
                        s.allowPropagate(act)
                )
                .collect(Collectors.toList());
    }

    private boolean hasTargetSynapses() {
        return targetSynapses != null && !targetSynapses.isEmpty();
    }

    @Override
    public Phase getPhase() {
        return Phase.PROCESSING;
    }

    @Override
    public void process() {
        Activation act = getElement();

        if(template && !act.getNeuron().checkTemplateInductionThreshold(act))
            return;

        targetSynapses.stream()
                .filter(s ->
                        !act.linkExists(OUTPUT, s, template)
                )
                .forEach(ts ->
                        propagate(bindingSignal, ts)
                );
    }

    public static Activation propagate(BindingSignal fromBS, Synapse targetSynapse) {
        Activation fromAct = fromBS.getActivation();
        Thought t = fromAct.getThought();

        Activation toAct = targetSynapse.getOutput().createActivation(t);
        toAct.init(targetSynapse, fromAct);

        BindingSignal toBS = fromBS.propagate(targetSynapse, toAct);
        Link l = targetSynapse.createLink(fromBS, toBS);
        toBS.setLink(l);
        return toAct;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((template ? "Template " : "") + getElement());
        targetSynapses.forEach(ts ->
                sb.append("\n    " + ts)
        );
        return sb.toString();
    }
}
