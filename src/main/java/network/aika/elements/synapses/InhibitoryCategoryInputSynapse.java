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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.InhibitoryActivation;
import network.aika.elements.links.BindingCategoryInputLink;
import network.aika.elements.links.InhibitoryCategoryInputLink;
import network.aika.visitor.linking.LinkingOperator;
import network.aika.visitor.linking.category.CategoryDownVisitor;
import network.aika.visitor.linking.inhibitory.InhibitoryDownVisitor;

/**
 * The Inhibitory Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
public class InhibitoryCategoryInputSynapse extends CategoryInputSynapse
{
    public InhibitoryCategoryInputSynapse() {
        super(null);
    }

    @Override
    public InhibitoryCategoryInputLink createLink(CategoryActivation input, Activation output) {
        return new InhibitoryCategoryInputLink(this, input, (InhibitoryActivation) output);
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation bs) {
        new InhibitoryDownVisitor(bs.getThought(), c)
                .start(bs);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }
}
