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
package network.aika.neuron.conjunctive;

import network.aika.Thought;
import network.aika.direction.Direction;
import network.aika.fields.QueueField;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.BindingActivation;
import network.aika.neuron.activation.LatentRelationActivation;
import network.aika.neuron.bindingsignal.BSKey;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.SingleTransition;


import java.util.stream.Stream;

import static network.aika.neuron.bindingsignal.State.SAME;
import static network.aika.neuron.conjunctive.PrimaryInputSynapse.SAME_INPUT_TRANSITION;
import static network.aika.neuron.conjunctive.ReversePatternSynapse.SAME_SAME_TRANSITION;

/**
 *
 * @author Lukas Molzberger
 */
public class LatentRelationNeuron extends BindingNeuron {

    private int rangeBegin = -1;
    private int rangeEnd = -1;

    public LatentRelationNeuron() {
    }

    public int getRangeBegin() {
        return rangeBegin;
    }

    public void setRangeBegin(int rangeBegin) {
        this.rangeBegin = rangeBegin;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    @Override
    public LatentRelationActivation createActivation(Thought t) {
        return new LatentRelationActivation(t.createActivationId(), t, this);
    }

    @Override
    public LatentRelationNeuron instantiateTemplate(boolean addProvider) {
        LatentRelationNeuron n = new LatentRelationNeuron();
        if(addProvider)
            n.addProvider(getModel());

        initFromTemplate(n);
        return n;
    }

    @Override
    public Stream<BindingSignal> getRelatedBindingSignals(BindingSignal fromBS, Direction dir) {
        if(isTemplate())
            return Stream.empty();

        Stream<BindingSignal> toBSs = super.getRelatedBindingSignals(fromBS, dir);

        return dir == Direction.OUTPUT ?
                toBSs :
                Stream.concat(toBSs, getRelatedBindingSignalsInternal(fromBS));
    }

    private Stream<BindingSignal> getRelatedBindingSignalsInternal(BindingSignal fromBS) {
        boolean dir = fromBS.getLink() == null;
        Thought<?> t = fromBS.getThought();
        return t.getRelatedTokens(fromBS, (dir ? 1 : -1) * rangeBegin, this)
                .map(tokenAct -> tokenAct.getBindingSignal(SAME))
                .map(bs ->
                        createLatentInstance(fromBS, bs, dir)
                );
    }

    private BindingSignal createLatentInstance(BindingSignal fromBS, BindingSignal toBS, boolean direction) {
        SingleTransition fromTransition = getTransitionByDirection(direction);
        SingleTransition toTransition = getTransitionByDirection(!direction);

        LatentRelationActivation latentRelAct = lookupLatentRelAct(fromBS, toBS);
        if(latentRelAct == null) {
            latentRelAct = createActivation(fromBS.getThought());
            latentRelAct.init(null, null);
        }

        BindingSignal latentFromBS = addLatentBindingSignal(fromBS, fromTransition, latentRelAct);
        addLatentBindingSignal(toBS, toTransition, latentRelAct);
        return latentFromBS;
    }

    private LatentRelationActivation lookupLatentRelAct(BindingSignal<?> fromBS, BindingSignal<?> toBS) {
        Activation<?> originAct = fromBS.getOriginActivation();
        return (LatentRelationActivation) originAct.getReverseBindingSignals(this)
                .map(bs -> bs.getActivation())
                .filter(act ->
                        act.getBindingSignal(BSKey.createKey(toBS)) != null
                ).findAny()
                .orElse(null);
    }

    private BindingSignal addLatentBindingSignal(BindingSignal bs, SingleTransition t, BindingActivation latentRelAct) {
        BindingSignal latentBS = new BindingSignal(bs, t);
        latentBS.init(latentRelAct);
        latentRelAct.addBindingSignal(latentBS);
        QueueField qf = (QueueField) latentBS.getOnArrived();
        qf.process();

        return latentBS;
    }

    private SingleTransition getTransitionByDirection(boolean direction) {
        return direction ?
                SAME_SAME_TRANSITION :
                SAME_INPUT_TRANSITION;
    }
}