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

import network.aika.neuron.activation.BindingActivation;
import network.aika.neuron.activation.ConjunctiveActivation;
import network.aika.neuron.activation.InputPatternLink;
import network.aika.neuron.activation.PatternActivation;

/**
 *
 * @author Lukas Molzberger
 */
public class InputPatternSynapse<S extends InputPatternSynapse, I extends ConjunctiveNeuron, L extends InputPatternLink<S, IA>, IA extends ConjunctiveActivation<?>> extends BindingNeuronSynapse<
        S,
        I,
        L,
        IA
        >
{
    public InputPatternSynapse() {
        super(Scope.INPUT);
    }

    @Override
    public InputPatternLink createLink(ConjunctiveActivation input, BindingActivation output) {
        return new InputPatternLink(this, input, output);
    }
}