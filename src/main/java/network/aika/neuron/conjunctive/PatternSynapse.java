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

import network.aika.direction.Direction;
import network.aika.neuron.activation.*;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.SingleTransition;
import network.aika.neuron.bindingsignal.Transition;

import java.util.List;
import java.util.stream.Stream;

import static network.aika.neuron.bindingsignal.FixedTerminal.fixed;
import static network.aika.neuron.bindingsignal.State.*;
import static network.aika.neuron.bindingsignal.SingleTransition.transition;
import static network.aika.neuron.bindingsignal.TransitionMode.*;
import static network.aika.neuron.bindingsignal.VariableTerminal.variable;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternSynapse extends ConjunctiveSynapse<PatternSynapse, BindingNeuron, PatternNeuron, PatternLink, BindingActivation, PatternActivation> {

    private static List<Transition> TRANSITIONS = List.of(
            transition(
                    fixed(SAME),
                    fixed(SAME),
                    MATCH_ONLY
            ),
            transition(
                    fixed(INPUT),
                    variable(INPUT),
                    MATCH_AND_PROPAGATE
            )
    );

    @Override
    public boolean isPropagate() {
        return true;
    }

    @Override
    public boolean propagateCheck(BindingSignal<BindingActivation> inputBS) {
        return true;
    }

    @Override
    public PatternLink createLink(BindingActivation input, PatternActivation output) {
        return new PatternLink(this, input, output);
    }

    @Override
    public Stream<Transition> getTransitions() {
        return TRANSITIONS.stream();
    }

    @Override
    public boolean networkInputsAllowed(Direction dir) {
        return !isTemplate();
    }
}
