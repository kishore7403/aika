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
package network.aika.neuron.activation.linker;

import network.aika.neuron.INeuron;
import network.aika.neuron.activation.Activation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class LNode<N extends INeuron> {

    String label;

    List<LLink> links = new ArrayList<>();

    Class<N> neuronClass;

    public abstract Activation follow(Mode m, INeuron n, Activation act, LLink from, Activation startAct);

    public LNode(Class<N> neuronClass, String label) {
        this.neuronClass = neuronClass;
        this.label = label;
    }

    public boolean checkNeuron(INeuron n) {
        if(neuronClass != null && !n.getClass().equals(neuronClass)) {
            return false;
        }

        return true;
    }

    public void addLink(LLink l) {
        links.add(l);
    }

    public String toString() {
        return label + " " + (neuronClass != null ? neuronClass.getSimpleName() : "X");
    }
}
