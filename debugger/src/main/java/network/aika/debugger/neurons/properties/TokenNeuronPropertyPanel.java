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
package network.aika.debugger.neurons.properties;

import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.TokenNeuron;


/**
 * @author Lukas Molzberger
 */
public class TokenNeuronPropertyPanel extends PatternNeuronPropertyPanel<PatternNeuron> {

    public TokenNeuronPropertyPanel(TokenNeuron n, Activation ref) {
        super(n, ref);

        addConstant("Token Label: ", n.getTokenLabel());
    }

    public static TokenNeuronPropertyPanel create(TokenNeuron n, Activation ref) {
        return new TokenNeuronPropertyPanel(n, ref);
    }
}
