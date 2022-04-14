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

import network.aika.neuron.activation.*;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.State;
import network.aika.neuron.bindingsignal.Transition;
import network.aika.neuron.disjunctive.InhibitoryNeuron;

import java.util.List;

import static network.aika.fields.Fields.mul;
import static network.aika.fields.Fields.scale;
import static network.aika.neuron.bindingsignal.Transition.transition;


/**
 *
 * @author Lukas Molzberger
 */
public class NegativeFeedbackSynapse extends BindingNeuronSynapse<NegativeFeedbackSynapse, InhibitoryNeuron, NegativeFeedbackLink, InhibitoryActivation> {

    private static List<Transition> TRANSITIONS = List.of(
            transition(State.INPUT, State.INPUT, true, 0),
            transition(State.BRANCH, State.BRANCH, true, 1)
    );

    @Override
    public NegativeFeedbackLink createLink(BindingSignal<InhibitoryActivation> input, BindingSignal<BindingActivation> output) {
        return new NegativeFeedbackLink(this, input, output);
    }

    @Override
    public void setWeight(double w) {
        weight.receiveUpdate(0, w);
    }

    @Override
    public boolean linkExists(InhibitoryActivation iAct, BindingActivation oAct) {
        if(super.linkExists(iAct, oAct))
            return true;

        return oAct.getBranches().stream()
                .anyMatch(bAct -> super.linkExists(iAct, bAct));
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }

    @Override
    public boolean linkingCheck(BindingSignal<InhibitoryActivation> iBS, BindingSignal<BindingActivation> oBS) {
        if(oBS.getActivation().isSeparateBranch(iBS.getActivation()))
            return false;

        if(!iBS.getActivation().isFired())
            return false;

        if(isTemplate() && !iBS.isSelfRef(oBS))
            return false;

        // Skip BindingNeuronSynapse.checkLinkingPreConditions
        // --> Do not check Link.isForward(iAct, oAct)
        return commonLinkingCheck(iBS, oBS);
    }

    @Override
    public boolean checkTemplateLinkingPreConditions(BindingSignal<InhibitoryActivation> iBS, BindingSignal<BindingActivation> oBS) {
        if(!iBS.isSelfRef(oBS))
            return false;

        return super.checkTemplateLinkingPreConditions(iBS, oBS);
    }

    @Override
    public List<Transition> getTransitions() {
        return TRANSITIONS;
    }
}
