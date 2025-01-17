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

import network.aika.direction.Direction;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.PositiveFeedbackLink;
import network.aika.fields.Multiplication;
import network.aika.elements.neurons.PatternNeuron;

import static network.aika.direction.Direction.OUTPUT;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.mul;

/**
 *
 * @author Lukas Molzberger
 */
public class PositiveFeedbackSynapse extends FeedbackSynapse<
        PositiveFeedbackSynapse,
        PatternNeuron,
        PositiveFeedbackLink,
        PatternActivation
        >
{
    public PositiveFeedbackSynapse() {
        super(Scope.SAME);
    }

    public PositiveFeedbackLink createLink(PatternActivation input, BindingActivation output) {
        return new PositiveFeedbackLink(this, input, output);
    }

    public void initDummyLink(BindingActivation oAct) {
        if(oAct.getPosFeedbackDummy() != null)
            return;

        Multiplication dummyWeight = mul(
                oAct,
                "pos-feedback-dummy",
                oAct.getThought().getIsOpen(),
                getWeight()
        );

        dummyWeight.getInputLinkByArg(1)
                .setPropagateUpdates(false);

        oAct.setPosFeedbackDummy(dummyWeight);
        linkAndConnect(dummyWeight, -1, oAct.getNet());
    }

    public Direction getStoredAt() {
        return OUTPUT;
    }

    @Override
    public void linkAndPropagateOut(PatternActivation act) {
    }

    @Override
    public double getPreNetDummyWeight() {
        return weight.getCurrentValue();
    }

    @Override
    public double getSortingWeight() {
        return 0.0;
    }

    @Override
    public boolean checkLinkingEvent(Activation act) {
        return true;
    }

    @Override
    public double getPropagatePreNet(PatternActivation iAct) {
        return 0.0;
    }
}
