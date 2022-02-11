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
package network.aika;


import network.aika.callbacks.EventListener;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.Range;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.*;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.PatternBindingSignal;
import network.aika.steps.Phase;
import network.aika.steps.QueueKey;
import network.aika.steps.Step;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Thought<M extends Model> {

    protected final M model;

    private long id;
    private long absoluteBegin;

    private Timestamp timestampOnProcess = new Timestamp(0);
    private long timestampCounter = 0;
    private int activationIdCounter = 0;

    private final NavigableMap<QueueKey, Step> queue = new TreeMap<>(QueueKey.THOUGHT_COMPARATOR);

    private final TreeMap<Integer, Activation> activationsById = new TreeMap<>();
    private final Map<NeuronProvider, SortedSet<Activation<?>>> actsPerNeuron = new HashMap<>();
    private final List<EventListener> eventListeners = new ArrayList<>();

    private Config config;


    public Thought(M m) {
        model = m;
        id = model.createThoughtId();
        absoluteBegin = m.getN();
        m.setCurrentThought(this);
    }

    public long getId() {
        return id;
    }

    public void updateModel() {
        model.addToN(length());
    }

    public M getModel() {
        return model;
    }

    public abstract int length();

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void onActivationCreationEvent(Activation act, Synapse originSynapse, Activation originAct) {
        callEventListener(el ->
                el.onActivationCreationEvent(act, originSynapse, originAct)
        );
    }

    public void beforeProcessedEvent(Step s) {
        callEventListener(el ->
                el.beforeProcessedEvent(s)
        );
    }

    public void afterProcessedEvent(Step s) {
        callEventListener(el ->
                el.afterProcessedEvent(s)
        );
    }

    private void callEventListener(Consumer<EventListener> el) {
        getEventListeners().forEach(el);
    }

    public void onLinkCreationEvent(Link l) {
        getEventListeners()
                .forEach(
                        el -> el.onLinkCreationEvent(l)
                );
    }

    public synchronized Collection<EventListener> getEventListeners() {
        return new ArrayList<>(eventListeners);
    }

    public synchronized void addEventListener(EventListener l) {
        eventListeners.add(l);
    }

    public synchronized void removeEventListener(EventListener l) {
        eventListeners.remove(l);
    }

    public void register(Activation act) {
        activationsById.put(act.getId(), act);
    }

    public void register(NeuronProvider np, SortedSet<Activation<?>> acts) {
        actsPerNeuron.put(np, acts);
    }

    public void registerPatternBindingSignalSource(Activation act, PatternBindingSignal pbs) {
    }

    public void addStep(Step s) {
        queue.put(s, s);
    }

    public void removeStep(Step s) {
        Step removedStep = queue.remove(s);
        assert removedStep != null;
    }

    public Collection<Step> getQueue() {
        return queue.values();
    }

    public Range getRange() {
        return new Range(absoluteBegin, absoluteBegin + length());
    }

    private NavigableMap<QueueKey, Step> getFilteredQueue(Phase maxPhase) {
        if(maxPhase == null)
            return queue;

        return queue.headMap(
                new QueueKey.Key(maxPhase),
                true
        );
    }

    public void process() {
        process(null);
    }

    public void process(Phase maxPhase) {
        NavigableMap<QueueKey, Step> filteredQueue = getFilteredQueue(maxPhase);

        while (!filteredQueue.isEmpty()) {
            Step s = filteredQueue.pollFirstEntry().getValue();

            timestampOnProcess = getCurrentTimestamp();

            s.getElement().removeQueuedPhase(s);

            beforeProcessedEvent(s);
            s.process();
            afterProcessedEvent(s);
        }
    }

    public Timestamp getTimestampOnProcess() {
        return timestampOnProcess;
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(timestampCounter);
    }

    public Timestamp getNextTimestamp() {
        return new Timestamp(timestampCounter++);
    }

    public <E extends Element> List<Step> getStepsByElement(E element) {
        return queue
                .values()
                .stream()
                .filter(s -> s.getElement() == element)
                .collect(Collectors.toList());
    }

    public int createActivationId() {
        return activationIdCounter++;
    }

    public Activation getActivation(Integer id) {
        return activationsById.get(id);
    }

    public Collection<Activation> getActivations() {
        return activationsById.values();
    }

    public int getNumberOfActivations() {
        return activationsById.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Activation act: activationsById.values()) {
/*            if(!act.isActive())
                continue;
*/
            sb.append(act.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    public Stream<PatternBindingSignal> getLooselyRelatedBindingSignals(BindingSignal<?> fromBindingSignal, Integer looseLinkingRange) {
        return Stream.empty();
    }
}
