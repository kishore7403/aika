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

import network.aika.Thought;
import network.aika.neuron.activation.*;
import network.aika.neuron.visitor.linking.LinkingDownVisitor;
import network.aika.neuron.visitor.linking.LinkingOperator;

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

    protected boolean getDummyLinkUB() {
        return true;
    }

    @Override
    public boolean propagateCheck(PatternActivation iAct) {
        return false;
    }

    @Override
    public boolean checkLinkingEvent(Activation act) {
        return true;
    }

    /*
    @Override
    protected boolean checkCausal(PatternActivation iAct, BindingActivation oAct) {
        return true;
    }
*/
    @Override
    public boolean isPropagate() {
        return false;
    }
}
