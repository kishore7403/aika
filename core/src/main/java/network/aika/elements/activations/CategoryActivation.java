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
package network.aika.elements.activations;

import network.aika.Thought;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Range;
import network.aika.elements.neurons.CategoryNeuron;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public class CategoryActivation extends DisjunctiveActivation<CategoryNeuron> {

    public CategoryActivation(int id, Thought t, CategoryNeuron neuron) {
        super(id, t, neuron);
    }

    @Override
    public Activation getTemplate() {
        return getOutputLinksByType(CategoryInputLink.class)
                .map(Link::getOutput)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Activation getActiveTemplateInstance() {
        return getCategoryInputs()
                .filter(Activation::isActiveTemplateInstance)
                .max(
                        Comparator.comparingDouble(act ->
                                act.getNet().getCurrentValue()
                        )
                )
                .orElse(null);
    }

    public Stream<Activation> getCategoryInputs() {
        return inputLinks.values()
                .stream()
                .map(l -> l.getInput());
    }

    public Activation getCategoryInput() {
        return getCategoryInputs()
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
