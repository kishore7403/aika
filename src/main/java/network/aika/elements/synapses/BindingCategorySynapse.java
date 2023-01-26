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
import network.aika.elements.activations.BindingCategoryActivation;
import network.aika.elements.links.BindingCategoryLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.BindingCategoryNeuron;
import network.aika.visitor.linking.LinkingOperator;
import network.aika.visitor.linking.inhibitory.InhibitoryDownVisitor;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingCategorySynapse extends CategorySynapse<BindingCategorySynapse, BindingNeuron, BindingCategoryNeuron, BindingActivation, BindingCategoryActivation> {

    @Override
    public BindingCategoryLink createLink(BindingActivation input, BindingCategoryActivation output) {
        return new BindingCategoryLink(this, input, output);
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation bs) {
        new InhibitoryDownVisitor(bs.getThought(), c)
                .start(bs);
    }
}
