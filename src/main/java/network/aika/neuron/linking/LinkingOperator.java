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
package network.aika.neuron.linking;

import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Element;
import network.aika.neuron.activation.Link;
import network.aika.neuron.conjunctive.ConjunctiveSynapse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Molzberger
 */
public abstract class LinkingOperator<E extends Element> {

    protected Activation fromBS;

    protected ConjunctiveSynapse syn;

    protected ArrayList<E> results = new ArrayList<>();

    public LinkingOperator(Activation fromBS, ConjunctiveSynapse syn) {
        this.fromBS = fromBS;
        this.syn = syn;
    }

    public List<E> getResults() {
        return results;
    }

    public abstract void check(LinkingVisitor v, Link lastLink, Activation act);

}
