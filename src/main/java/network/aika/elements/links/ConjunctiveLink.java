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
package network.aika.elements.links;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.ConjunctiveSynapse;

import static network.aika.fields.FieldLink.link;
import static network.aika.fields.Fields.mul;


/**
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveLink<S extends ConjunctiveSynapse, IA extends Activation<?>, OA extends ConjunctiveActivation<?>> extends Link<S, IA, OA> {


    public ConjunctiveLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    protected void linkTemplateAndInstance(ConjunctiveActivation instanceAct) {
        output.setTemplateInstance(instanceAct);
        instanceAct.setTemplate(output);
    }

    @Override
    public void connectWeightUpdate() {
        link(
                mul(
                        this,
                        "weight update",
                        getInput().getIsFiredForWeight(),
                        getOutput().getUpdateValue()
                ),
                synapse.getWeight()
        );

        link(
                mul(
                        this,
                        "bias update",
                        getInput().getIsFiredForBias(),
                        getOutput().getUpdateValue()
                ),
                output.getNeuron().getBias()
        );
    }
}