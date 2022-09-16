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
package network.aika.neuron.disjunctive;

import network.aika.neuron.bindingsignal.Transition;

import java.util.List;
import java.util.stream.Stream;

import static network.aika.neuron.bindingsignal.FixedTerminal.fixed;
import static network.aika.neuron.bindingsignal.PrimitiveTransition.transition;
import static network.aika.neuron.bindingsignal.State.INPUT;
import static network.aika.neuron.bindingsignal.TransitionMode.MATCH_AND_PROPAGATE;


/**
 *
 * @author Lukas Molzberger
 */
public class InputInhibitorySynapse extends InhibitorySynapse {

    private static List<Transition> TRANSITIONS = List.of(
            transition(
                    fixed(INPUT),
                    fixed(INPUT),
                    MATCH_AND_PROPAGATE,
                    InputInhibitorySynapse.class
            )
    );

    @Override
    public Stream<Transition> getTransitions() {
        return TRANSITIONS.stream();
    }
}