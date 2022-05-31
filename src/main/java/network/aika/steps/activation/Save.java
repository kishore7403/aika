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
package network.aika.steps.activation;

import network.aika.neuron.Neuron;
import network.aika.neuron.activation.DummyActivation;
import network.aika.steps.Phase;
import network.aika.steps.Step;

/**
 * Store model
 *
 * @author Lukas Molzberger
 */
public class Save extends Step<DummyActivation> {

    public static void add(Neuron n) {
        if(n.isTemplate())
            return;

        Step.add(new Save(new DummyActivation(n)));
    }

    private Save(DummyActivation act) {
        super(act);
    }

    @Override
    public Phase getPhase() {
        return Phase.POST;
    }

    @Override
    public void process() {
        getElement()
                .getNeuron()
                .getProvider()
                .save();
    }
}