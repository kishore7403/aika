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

import network.aika.Thought;
import network.aika.neuron.Range;
import network.aika.neuron.disjunctive.CategoryNeuron;

/**
 * @author Lukas Molzberger
 */
public class CategoryActivation<N extends CategoryNeuron<?, ?>> extends DisjunctiveActivation<N> {

    public CategoryActivation(int id, Thought t, N neuron) {
        super(id, t, neuron);
    }

    public ConjunctiveActivation getCategoryInput() {
        return (ConjunctiveActivation) inputLinks.values()
                .stream()
                .map(l -> l.getInput())
                .findFirst()
                .orElse(null);
    }

    @Override
    public Range getRange() {
        Activation iAct = getCategoryInput();
        return iAct != null ? iAct.getRange() : null;
    }

    @Override
    public Integer getTokenPos() {
        Activation iAct = getCategoryInput();
        return iAct != null ? iAct.getTokenPos() : null;
    }
}
