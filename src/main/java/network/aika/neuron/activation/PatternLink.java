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

import network.aika.fields.AbstractFunction;
import network.aika.neuron.conjunctive.PatternSynapse;
import network.aika.neuron.conjunctive.PositiveFeedbackSynapse;
import network.aika.neuron.visitor.DownVisitor;
import network.aika.neuron.visitor.UpVisitor;
import network.aika.sign.Sign;
import network.aika.steps.link.LinkCounting;

import static network.aika.fields.Fields.func;
import static network.aika.fields.Fields.scale;

/**
 * @author Lukas Molzberger
 */
public class PatternLink extends AbstractPatternLink<PatternSynapse, BindingActivation> {

    private AbstractFunction outputEntropy;
    private AbstractFunction informationGain;

    public PatternLink(PatternSynapse s, BindingActivation input, PatternActivation output) {
        super(s, input, output);

        LinkCounting.add(this);
    }

    @Override
    public void addInputLinkingStep() {
        super.addInputLinkingStep();

        PositiveFeedbackSynapse posFeedbackSyn = (PositiveFeedbackSynapse) input.getNeuron().getInputSynapse(output.getNeuronProvider());
        if(posFeedbackSyn != null && !posFeedbackSyn.linkExists(input))
            posFeedbackSyn.createAndInitLink(output, input);
    }

    @Override
    public void connectGradientFields() {
        super.connectGradientFields();

        informationGain = func(
                this,
                "Information-Gain",
                input.net,
                output.net,
                (x1, x2) ->
                        synapse.getSurprisal(
                                Sign.getSign(x1),
                                Sign.getSign(x2),
                                input.getAbsoluteRange(),
                                true
                        ),
                forwardsGradient
        );

        outputEntropy = scale(this, "-Entropy", -1,
                output.getEntropy(),
                forwardsGradient
        );
    }

    public AbstractFunction getOutputEntropy() {
        return outputEntropy;
    }

    public AbstractFunction getInformationGain() {
        return informationGain;
    }

    @Override
    public void patternVisitDown(DownVisitor v) {
        v.next(this);
    }

    @Override
    public void patternVisitUp(UpVisitor v) {
        v.next(this);
    }

    @Override
    public void bindingVisitDown(DownVisitor v) {
    }

    @Override
    public void bindingVisitUp(UpVisitor v) {
    }

    @Override
    public void inhibVisitDown(DownVisitor v) {
    }

    @Override
    public void inhibVisitUp(UpVisitor v) {
    }
}
