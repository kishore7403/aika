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
package network.aika.neuron.bindingsignal;

import network.aika.direction.Direction;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static network.aika.direction.Direction.INPUT;
import static network.aika.direction.Direction.OUTPUT;
import static network.aika.fields.Fields.isTrue;
import static network.aika.neuron.bindingsignal.LatentLinking.latentLinking;
import static network.aika.neuron.bindingsignal.TransitionMode.*;

/**
 * @author Lukas Molzberger
 */
public class SingleTransition<I extends SingleTerminal, O extends SingleTerminal> implements Transition {

    private static final Logger log = LoggerFactory.getLogger(SingleTransition.class);

    protected I input;
    protected O output;
    protected TransitionMode mode;

    protected SingleTransition(I input, O output, TransitionMode mode) {
        this.input = input;
        this.output = output;
        this.mode = mode;
        input.setType(INPUT);
        output.setType(OUTPUT);

        input.setTransition(this);
        output.setTransition(this);
    }

    public static <I extends SingleTerminal, O extends SingleTerminal> SingleTransition<I, O> transition(I input, O output, TransitionMode transitionMode) {
        return new SingleTransition(input, output, transitionMode);
    }

    public void linkAndPropagate(Synapse ts, BindingSignal fromBS, Direction dir) {
        link(ts, fromBS, dir);
        if (dir != OUTPUT)
            return;

        latentLinking(this, ts, fromBS);
        propagate(this, ts, fromBS);
    }

    public void link(Synapse ts, BindingSignal fromBS, Direction dir) {
        Stream<BindingSignal> bsStream = ts.getRelatedBindingSignals(fromBS.getOriginActivation(), this, dir);

        bsStream
                .filter(toBS -> fromBS != toBS)
                .forEach(toBS ->
                        link(this, ts, fromBS, toBS, dir)
                );
    }

    public static void propagate(SingleTransition t, Synapse ts, BindingSignal fromBS) {
        if(!ts.isPropagate())
            return;

        ts.propagate(fromBS, null);
    }

    public static void link(SingleTransition t, Synapse ts, BindingSignal fromBS, BindingSignal toBS, Direction dir) {
        if(!t.isMatching())
            return;

        if(!ts.checkLinkingEvent(toBS.getActivation(), dir))
            return;

        BindingSignal inputBS = dir.getInput(fromBS, toBS);
        BindingSignal outputBS = dir.getOutput(fromBS, toBS);

        ts.link(inputBS, outputBS);
    }

    @Override
    public Stream<Terminal> getInputTerminals() {
        return Stream.of(input);
    }

    @Override
    public Stream<Terminal> getOutputTerminals() {
        return Stream.of(output);
    }

    public I getInput() {
        return input;
    }

    public O getOutput() {
        return output;
    }

    public State next(Direction dir) {
        return (dir == OUTPUT ? output : input).getState();
    }

    public TransitionMode getMode() {
        return mode;
    }

    public boolean isPropagate() {
        return mode == PROPAGATE_ONLY || mode == MATCH_AND_PROPAGATE;
    }

    public boolean isMatching() {
        return mode == MATCH_ONLY || mode == MATCH_AND_PROPAGATE;
    }

    public String toString() {
        return "Input:" + input +
                " Output:" + output +
                " Mode:" + mode;
    }
}
