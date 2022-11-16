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

import network.aika.fields.Multiplication;
import network.aika.neuron.conjunctive.PositiveFeedbackSynapse;
import network.aika.neuron.visitor.DownVisitor;
import network.aika.neuron.visitor.UpVisitor;

import static network.aika.fields.FieldLink.connect;
import static network.aika.fields.Fields.*;
import static network.aika.fields.ThresholdOperator.Type.ABOVE;

/**
 *
 * @author Lukas Molzberger
 */
public class PositiveFeedbackLink extends FeedbackLink<PositiveFeedbackSynapse, PatternActivation> {

    public PositiveFeedbackLink(PositiveFeedbackSynapse s, PatternActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    protected void addInputLinkingStep() {
    }

    @Override
    protected void initOnTransparent() {
        onTransparent = threshold(
                this,
                "onTransparent",
                0.0,
                ABOVE,
                synapse.getWeight()
        );
    }

    @Override
    protected void initWeightInputUB() {
        Multiplication weightedLB = mul(
                this,
                "!isOpen * x * weight",
                invert("!isOpen", output.getIsOpen()),
                initWeightedInput(true)
        );

        connect(weightedLB, getOutput().getNet(true));
    }

    @Override
    public void bindingVisitDown(DownVisitor v) {
    }

    @Override
    public void bindingVisitUp(UpVisitor v) {
    }

    @Override
    public void patternVisitDown(DownVisitor v) {
    }

    @Override
    public void patternVisitUp(UpVisitor v) {
    }

    @Override
    public void inhibVisitDown(DownVisitor v) {
    }

    @Override
    public void inhibVisitUp(UpVisitor v) {
    }
}
