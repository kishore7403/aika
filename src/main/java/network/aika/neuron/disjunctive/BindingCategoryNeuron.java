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

import network.aika.Thought;
import network.aika.neuron.ActivationFunction;
import network.aika.neuron.activation.BindingCategoryActivation;
import network.aika.neuron.axons.BindingAxon;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingCategoryNeuron extends DisjunctiveNeuron<BindingCategorySynapse, BindingCategoryActivation> implements BindingAxon {

    @Override
    public BindingCategoryActivation createActivation(Thought t) {
        return new BindingCategoryActivation(t.createActivationId(), t, this);
    }

    @Override
    public BindingCategoryNeuron instantiateTemplate(boolean addProvider) {
        BindingCategoryNeuron n = new BindingCategoryNeuron();
        if(addProvider)
            n.addProvider(getModel());

        initFromTemplate(n);
        return n;
    }

    public ActivationFunction getActivationFunction() {
        return ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
    }
}