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


/**
 *
 * @author Lukas Molzberger
 */
public class PatternSynapse extends AbstractPatternSynapse<
        PatternSynapse,
        BindingNeuron,
        PatternLink,
        BindingActivation
        >
{
    public PatternSynapse() {
        super();
    }

    @Override
    public boolean isPropagate() {
        return true;
    }

    @Override
    public boolean propagateCheck(BindingActivation iAct) {
        return true;
    }

    @Override
    public PatternLink createLink(BindingActivation input, PatternActivation output) {
        PositiveFeedbackSynapse posFeedbackSyn = (PositiveFeedbackSynapse) input.getNeuron().getInputSynapse(output.getNeuronProvider());
        if(posFeedbackSyn != null)
            new PositiveFeedbackLink(posFeedbackSyn, output, input);

        return new PatternLink(this, input, output);
    }
}
