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
package network.aika.elements.activations;

import network.aika.FieldObject;
import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.Element;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.ActivationFunction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.Range;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.fields.*;
import network.aika.elements.synapses.Synapse;
import network.aika.steps.activation.InstantiationEdges;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.linking.pattern.PatternCategoryDownVisitor;
import network.aika.visitor.linking.pattern.PatternCategoryUpVisitor;
import network.aika.visitor.selfref.SelfRefDownVisitor;
import network.aika.visitor.UpVisitor;
import network.aika.steps.activation.Counting;
import network.aika.steps.activation.LinkingOut;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static network.aika.callbacks.EventType.CREATE;
import static network.aika.callbacks.EventType.UPDATE;
import static network.aika.elements.neurons.Range.joinTokenPosition;
import static network.aika.elements.neurons.Range.tokenPositionEquals;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.*;
import static network.aika.fields.ThresholdOperator.Type.*;
import static network.aika.steps.Phase.INFERENCE;
import static network.aika.steps.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public abstract class Activation<N extends Neuron> extends FieldObject implements Element, Comparable<Activation> {

    public static final Comparator<Activation> ID_COMPARATOR = Comparator.comparingInt(Activation::getId);

    protected final int id;
    protected N neuron;
    protected Thought thought;

    protected Timestamp created = Timestamp.NOT_SET;
    protected Timestamp fired = Timestamp.NOT_SET;

    protected FieldOutput value;
    protected FieldOutput negValue;

    protected SumField net;

    protected FieldOutput isFired;

    protected FieldFunction netOuterGradient;
    protected SumField gradient;

    protected Field updateValue;

    protected FieldOutput negUpdateValue;

    protected Map<NeuronProvider, Link> inputLinks;
    protected NavigableMap<OutputKey, Link> outputLinks;

    public boolean instantiationNodesIsQueued;
    public boolean instantiationEdgesIsQueued;
    protected boolean isNewInstance;

    protected Range range;
    protected Integer tokenPos;

    protected Consumer<Integer> onTokenPosUpdate;

    public Activation(int id, Thought t, N n) {
        this.id = id;
        this.neuron = n;
        this.thought = t;
        setCreated(t.getCurrentTimestamp());

        inputLinks = new TreeMap<>();
        outputLinks = new TreeMap<>(OutputKey.COMPARATOR);

        initNet();

        isFired = threshold(this, "isFired", 0.0, ABOVE, net);

        isFired.addEventListener("onFired", () -> {
                    fired = thought.getCurrentTimestamp();
                    LinkingOut.add(this);
                    Counting.add(this);
                }
        );

        value = func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> getActivationFunction().f(x)
        );

        negValue = threshold(
                this,
                "!value",
                0.0,
                BELOW_OR_EQUAL,
                value
        );

        gradient = new QueueSumField(this, TRAINING, "gradient", TOLERANCE);

        if (getConfig().isTrainingEnabled() && neuron.isTrainingAllowed()) {
            connectGradientFields();
            connectWeightUpdate();
        }

        initDummyLinks();

        thought.register(this);
        neuron.register(this);

        thought.onElementEvent(CREATE, this);
    }

    protected void connectWeightUpdate() {

    }

    protected void initNet() {
        net = new ValueSortedQueueField(this, INFERENCE, "net", null);
        linkAndConnect(getNeuron().getBias(), net)
                .setPropagateUpdates(false);
    }

    protected void initDummyLinks() {
    }

    public boolean isNewInstance() {
        return isNewInstance;
    }

    public boolean isAbstract() {
        return neuron.isAbstract();
    }

    public void setNet(double v) {
        net.setValue(v);
    }

    public boolean isSelfRef(BindingActivation oAct) {
        SelfRefDownVisitor v = new SelfRefDownVisitor(oAct);
        v.start(this);
        return v.isSelfRef();
    }

    public static boolean isSelfRef(BindingActivation in, BindingActivation out) {
        return in.isSelfRef(out) ||
                out.isSelfRef(in) ||
                in.getNeuron().isInstanceOf(out.getNeuron()) ||
                out.getNeuron().isInstanceOf(in.getNeuron());
    }

    public void bindingVisitDown(DownVisitor v, Link lastLink) {
        v.next(this);
    }

    public void bindingVisitUp(UpVisitor v, Link lastLink) {
        v.check(lastLink, this);
        v.next(this);
    }

    public void patternVisitDown(DownVisitor v, Link lastLink) {
        v.next(this);
    }

    public void patternVisitUp(UpVisitor v, Link lastLink) {
        v.check(lastLink, this);
        v.next(this);
    }

    public void inhibVisitDown(DownVisitor v, Link lastLink) {
        v.next(this);
    }

    public void inhibVisitUp(UpVisitor v, Link lastLink) {
        v.check(lastLink, this);
        v.next(this);
    }

    public void patternCatVisitDown(PatternCategoryDownVisitor v, Link lastLink) {
        v.next(this);
    }

    public void patternCatVisitUp(PatternCategoryUpVisitor v, Link lastLink) {
        v.check(lastLink, this);
        v.next(this);
    }

    public void selfRefVisitDown(DownVisitor v, Link lastLink) {
        v.next(this);
    }

    protected void connectGradientFields() {
        netOuterGradient =
                func(
                        this,
                        "f'(net)",
                        TOLERANCE,
                        net,
                        x -> getNeuron().getActivationFunction().outerGrad(x)
        );
    }

    public FieldOutput getIsFired() {
        return isFired;
    }

    public FieldFunction getNetOuterGradient() {
        return netOuterGradient;
    }

    public SumField getGradient() {
        return gradient;
    }

    public Field getUpdateValue() {
        return updateValue;
    }

    public FieldOutput getNegUpdateValue() {
        return negUpdateValue;
    }

    public int getId() {
        return id;
    }

    public FieldOutput getValue() {
        return value;
    }

    public FieldOutput getNegValue() {
        return negValue;
    }

    public boolean isInput() {
        return false;
    }

    public SumField getNet() {
        return net;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp ts) {
        this.created = ts;
    }

    public Timestamp getFired() {
        return fired;
    }

    public boolean isFired() {
        return isTrue(isFired);
    }

    public Thought getThought() {
        return thought;
    }

    public Range getRange() {
        return range;
    }

    public Integer getTokenPos() {
        return tokenPos;
    }

    public void updateRangeAndTokenPos(Range r, Integer tp) {
        Range newRange = Range.join(range, r);

        Integer newTokenPos = joinTokenPosition(tokenPos, tp);

        if(!r.equals(range) || !tokenPositionEquals(tokenPos, newTokenPos)) {
            propagateRangeAndTokenPosition();
        }

        this.range = newRange;
        this.tokenPos = newTokenPos;
        if(onTokenPosUpdate != null)
            onTokenPosUpdate.accept(newTokenPos);
    }

    protected void propagateRangeAndTokenPosition() {
        outputLinks.values().forEach(l ->
                l.propagateRangeOrTokenPos()
        );
    }

    public void setOnTokenPosUpdate(Consumer<Integer> onTokenPosUpdate) {
        this.onTokenPosUpdate = onTokenPosUpdate;
    }

    public Range getAbsoluteRange() {
        Range r = getRange();
        if(r == null) return null;
        return r.getAbsoluteRange(thought.getRange());
    }

    @Override
    public int compareTo(Activation act) {
        return ID_COMPARATOR.compare(this, act);
    }

    public OutputKey getOutputKey() {
        return new OutputKey(getNeuronProvider(), getId());
    }

    public String getLabel() {
        return getNeuron().getLabel();
    }

    public N getNeuron() {
        return neuron;
    }

    public void setNeuron(N n) {
        this.neuron = n;
    }

    public ActivationFunction getActivationFunction() {
        return neuron.getActivationFunction();
    }

    public <M extends Model> M getModel() {
        return (M) neuron.getModel();
    }

    public NeuronProvider getNeuronProvider() {
        return neuron.getProvider();
    }

    public Link getInputLink(Neuron n) {
        return inputLinks.get(n.getProvider());
    }

    public Link getInputLink(Synapse s) {
        return inputLinks.get(s.getPInput());
    }

    public <IL extends Link> Optional<IL> getInputLinkByType(Class<IL> linkType) {
        return getInputLinksByType(linkType)
                .findAny();
    }

    public <IL extends Link> Stream<IL> getInputLinksByType(Class<IL> linkType) {
        return getInputLinks()
                .filter(linkType::isInstance)
                .map(linkType::cast);
    }

    public <OL extends Link> Stream<OL> getOutputLinksByType(Class<OL> linkType) {
        return getOutputLinks()
                .filter(linkType::isInstance)
                .map(linkType::cast);
    }

    public Stream<Link> getOutputLinks(Synapse s) {
        return outputLinks
                .subMap(
                        new OutputKey(s.getOutput().getProvider(), Integer.MIN_VALUE),
                        true,
                        new OutputKey(s.getOutput().getProvider(), MAX_VALUE),
                        true
                ).values()
                .stream()
                .filter(l -> l.getSynapse() == s);
    }

    public void linkInputs() {
        inputLinks
                .values()
                .forEach(Link::linkInput);
    }

    public void unlinkInputs() {
        inputLinks
                .values()
                .forEach(Link::unlinkInput);
    }

    public void linkOutputs() {
        outputLinks
                .values()
                .forEach(Link::linkOutput);
    }

    public void unlinkOutputs() {
        outputLinks
                .values()
                .forEach(Link::unlinkOutput);
    }

    public void linkOutputLink(Link l) {
        Link el = outputLinks.put(
                new OutputKey(l.getOutput().getNeuronProvider(), l.getOutput().getId()),
                l
        );

        assert el == null;
    }

    public void linkInputLink(Link l) {
        Link el = inputLinks.put(
                l.getInput() != null ? l.getInput().getNeuronProvider() : l.getSynapse().getPInput(),
                l
        );
        assert el == null;
    }

    public void unlinkOutputLink(Link l) {
        OutputKey ok = l.getOutput().getOutputKey();
        outputLinks.remove(ok, this);
    }

    public void unlinkInputLink(Link l) {
        inputLinks.remove(l.getInput().getNeuronProvider(), l);
    }

    public void link() {
        linkInputs();
        linkOutputs();
    }

    public void unlink() {
        unlinkInputs();
        unlinkOutputs();
    }

    @Override
    public void disconnect() {
        super.disconnect();

        getInputLinks().forEach(l ->
                l.disconnect()
        );
    }

    public Stream<Link> getInputLinks() {
        return new ArrayList<>(inputLinks.values())
                .stream();
    }

    public Stream<Link> getOutputLinks() {
        return new ArrayList<>(outputLinks.values())
                .stream();
    }

    public Activation getTemplate() {
        return getOutputLinksByType(CategoryLink.class)
                .map(Link::getOutput)
                .map(Activation::getTemplate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public Stream<Activation> getTemplateInstances() {
        CategoryInputLink cil = getCategoryInputLink();
        if(cil == null || cil.getInput() == null)
            return Stream.empty();

        return cil.getInput()
                .getCategoryInputs();
    }

    public boolean isActiveTemplateInstance() {
        return isNewInstance || isTrue(isFired);
    }

    public CategoryInputLink getCategoryInputLink() {
        return getInputLinksByType(CategoryInputLink.class)
                .findFirst()
                .orElse(null);
    }

    public Activation getActiveTemplateInstance() {
        return getInputLinksByType(CategoryInputLink.class)
                .map(Link::getInput)
                .filter(Objects::nonNull)
                .findFirst()
                .map(Activation::getActiveTemplateInstance)
                .orElse(null);
    }

    public Activation<N> resolveAbstractInputActivation() {
        return neuron.isAbstract() ?
                getActiveTemplateInstance() :
                this;
    }

    public void instantiateTemplateNode() {
        N n = (N) neuron.instantiateTemplate();

        Activation<N> ti = n.createActivation(getThought());

        ti.tokenPos = tokenPos;
        ti.range = range;
        ti.isNewInstance = true;
        ti.fired = fired;

        linkTemplateAndInstance(ti);

        instantiateBias(ti);

        InstantiationEdges.add(this, ti);

        if(thought.getInstantiationCallback() != null)
            thought.getInstantiationCallback().onInstantiation(ti);
    }

    private void linkTemplateAndInstance(Activation<N> ti) {
        CategoryInputLink cl = getCategoryInputLink();
        if(cl == null)
            cl = createCategoryInputLink();

        cl.instantiateTemplate(cl.getInput(), ti);
    }

    private CategoryInputLink createCategoryInputLink() {
        CategoryInputSynapse catSyn = getNeuron().getCategoryInputSynapse();
        if(catSyn == null)
            return null;

        CategoryActivation catAct = catSyn.getInput().createActivation(thought);
        return catSyn.createAndInitLink(catAct, this);
    }

    protected void instantiateBias(Activation<N> ti) {
    }

    public void instantiateTemplateEdges(Activation<N> instanceAct) {
        getInputLinks()
                .forEach(l ->
                        l.instantiateTemplate(
                                l.getInput().resolveAbstractInputActivation(),
                                instanceAct
                        )
                );

        instanceAct.getNeuron().setLabel(
                getConfig().getLabel(this)
        );

        instanceAct.initDummyLinks();
        instanceAct.initFromTemplate();

        getOutputLinks()
                .forEach(l ->
                        l.instantiateTemplate(
                                instanceAct,
                                l.getOutput().resolveAbstractInputActivation()
                        )
                );
    }

    public void initFromTemplate() {
        fired = getTemplate().fired;
        thought.onElementEvent(UPDATE, this);
    }

    public String toString() {
        return getClass().getSimpleName() + " " + toKeyString();
    }

    public String toKeyString() {
        return "id:" + getId() + " n:[" + getNeuron().toKeyString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activation)) return false;
        Activation<?> that = (Activation<?>) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
