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
package network.aika.neuron.steps.tasks;

import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.direction.Direction;
import network.aika.neuron.activation.visitor.ActVisitor;
import network.aika.neuron.steps.Linker;
import network.aika.neuron.steps.Step;
import network.aika.neuron.steps.activation.Induction;
import network.aika.neuron.steps.link.LinkInduction;
import network.aika.neuron.steps.link.Template;

import java.util.stream.Stream;

import static network.aika.neuron.activation.direction.Direction.INPUT;
import static network.aika.neuron.activation.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class TemplateTask extends Linker {

    public TemplateTask(Direction dir) {
        super(dir);
    }


    @Override
    public Stream<? extends Synapse> getTargetSynapses(Activation act, Direction dir) {
        return super.getTemplateTargetSynapses(act, dir);
    }

    @Override
    public boolean checkPropagate(Activation act, Synapse targetSynapse) {
        return targetSynapse.checkTemplatePropagate(direction, act);
    }

    @Override
    protected boolean exists(Activation act, Synapse s) {
        return act.templateLinkExists(direction, s);
    }

    private boolean neuronMatches(Neuron<?> currentN, Neuron<?> targetN) {
        return currentN.getTemplateGroup()
                .stream()
                .anyMatch(tn ->
                        tn.getId().intValue() == targetN.getId().intValue()
                );
    }

    @Override
    public void getNextSteps(Activation act) {
        Induction.add(act);
    }

    @Override
    public void getNextSteps(Link l) {
        Template.add(l);
        LinkInduction.add(l);
    }

    @Override
    public void closeLoopIntern(ActVisitor v, Activation iAct, Activation oAct) {
        if(oAct.getNeuron().isInputNeuron())
            return;

        if(v.getCurrentDir() != OUTPUT && !targetSynapse.isRecurrent())
            return;

        if (!neuronMatches(iAct.getNeuron(), targetSynapse.getInput()))
            return;

        if (!neuronMatches(oAct.getNeuron(), targetSynapse.getOutput()))
            return;

        if(!iAct.isFired())
            return;

        if(Link.templateLinkExists(targetSynapse, iAct, oAct))
            return;

        targetSynapse.closeLoop(this, v, iAct, oAct);
    }

    public String toString() {
        return "Template Task: (Target-Synapse:" + targetSynapse + ")";
    }
}
