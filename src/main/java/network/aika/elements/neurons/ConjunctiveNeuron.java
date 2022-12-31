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
package network.aika.elements.neurons;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.ConjunctiveSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;

import static network.aika.direction.Direction.INPUT;
import static network.aika.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveNeuron<S extends ConjunctiveSynapse, A extends ConjunctiveActivation> extends Neuron<S, A> {

    private static final Logger log = LoggerFactory.getLogger(ConjunctiveNeuron.class);

    public ConjunctiveNeuron() {
        bias.addEventListener(this::updateSumOfLowerWeights, true);
    }

    @Override
    protected void initFromTemplate(Neuron templateN) {
        super.initFromTemplate(templateN);

        S cis = (S) ((ConjunctiveNeuron)templateN).getCategoryInputSynapse();
        newCategorySynapse()
                .init(this, cis.getInput(), 10.0);
    }

    public abstract CategorySynapse newCategorySynapse();

    @Override
    public void setModified() {
        super.setModified();
    }

    public boolean isAbstract() {
        return getCategoryInputSynapse() != null;
    }

    public abstract CategoryInputSynapse getCategoryInputSynapse();

    public void addInactiveLinks(Activation bs) {
        getInputSynapsesAsStream()
                .filter(s -> !s.linkExists(bs))
                .forEach(s ->
                        s.createAndInitLink(null, bs)
                );
    }

    public ActivationFunction getActivationFunction() {
        return ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
    }

    protected void updateSumOfLowerWeights() {
        ConjunctiveSynapse[] inputSynapses = sortInputSynapses();

        double sum = getBias().getUpdatedCurrentValue();
        for(ConjunctiveSynapse s: inputSynapses) {
            double w = s.getWeight().getUpdatedCurrentValue();
            if(w <= 0.0)
                continue;

            s.setSumOfLowerWeights(sum);
            sum += w;

            s.setStoredAt(
                    sum < 0 ?
                            OUTPUT :
                            INPUT
            );
        }
    }

    @Override
    public void addInputSynapse(S s) {
        super.addInputSynapse(s);
        s.getWeight().addEventListener(this::updateSumOfLowerWeights, true);
    }

    private ConjunctiveSynapse[] sortInputSynapses() {
        ConjunctiveSynapse[] inputsSynapses = getInputSynapses().toArray(new ConjunctiveSynapse[0]);
        Arrays.sort(
                inputsSynapses,
                Comparator.comparingDouble(s -> s.getSortingWeight())
        );
        return inputsSynapses;
    }
}