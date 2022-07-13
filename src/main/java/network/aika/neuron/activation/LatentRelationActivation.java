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
package network.aika.neuron.activation;

import network.aika.Thought;
import network.aika.fields.Field;
import network.aika.fields.ValueSortedQueueField;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.PrimitiveTransition;
import network.aika.neuron.bindingsignal.State;
import network.aika.neuron.conjunctive.LatentRelationNeuron;

/**
 * @author Lukas Molzberger
 */
public class LatentRelationActivation extends BindingActivation {

    protected LatentRelationActivation(int id, LatentRelationNeuron n) {
        super(id, n);
    }

    public LatentRelationActivation(int id, Thought t, LatentRelationNeuron n) {
        super(id, t, n);
    }

    @Override
    protected Field initNet() {
        return new ValueSortedQueueField(this, "net", 10.0);
    }

    public BindingSignal addLatentBindingSignal(PatternActivation fromOriginAct, PrimitiveTransition t) {
        BindingSignal originBS = fromOriginAct.getBindingSignal(State.SAME);
        BindingSignal latentBS = new BindingSignal(originBS, t);
        latentBS.init(this);
        addBindingSignal(latentBS);
//        QueueField qf = (QueueField) latentBS.getOnArrived();
//        qf.process();

        return latentBS;
    }
}