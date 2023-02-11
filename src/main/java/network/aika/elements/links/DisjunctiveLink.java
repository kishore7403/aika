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

import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.DisjunctiveActivation;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.fields.FieldLink;

import static network.aika.fields.Fields.mul;

/**
 * @author Lukas Molzberger
 */
public class DisjunctiveLink<S extends DisjunctiveSynapse, IA extends ConjunctiveActivation<?>, OA extends DisjunctiveActivation> extends Link<S, IA, OA> {

    public DisjunctiveLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    @Override
    public void connectWeightUpdate() {
        FieldLink.link(
                mul(
                        this,
                        "weight update",
                        getInput().getIsFired(),
                        getOutput().getUpdateValue()
                ),
                synapse.getWeight()
        );
    }
}
