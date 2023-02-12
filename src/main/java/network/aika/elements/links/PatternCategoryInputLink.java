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
package network.aika.elements.links;

import network.aika.direction.Direction;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.activations.PatternCategoryActivation;
import network.aika.elements.synapses.PatternCategoryInputSynapse;
import network.aika.elements.synapses.PatternCategorySynapse;
import network.aika.visitor.Visitor;


/**
 * @author Lukas Molzberger
 */
public class PatternCategoryInputLink extends AbstractPatternLink<PatternCategoryInputSynapse, PatternCategoryActivation> {

    public PatternCategoryInputLink(PatternCategoryInputSynapse s, PatternCategoryActivation input, PatternActivation output) {
        super(s, input, output);
    }

    @Override
    public void instantiateTemplate(PatternCategoryActivation iAct, PatternActivation oAct) {
        if(iAct == null || oAct == null)
            return;

        Link l = iAct.getInputLink(oAct.getNeuron());

        if(l != null)
            return;

        PatternCategorySynapse s = new PatternCategorySynapse();
        s.initFromTemplate(oAct.getNeuron(), iAct.getNeuron(), synapse);

        s.createLinkFromTemplate(oAct, iAct, this);
    }

    @Override
    protected void connectGradientFields() {
        initGradient();

        super.connectGradientFields();
    }

    @Override
    public void addInputLinkingStep() {
        super.addInputLinkingStep();

        input.getInputLinks()
                .map(l -> (ConjunctiveActivation)l.getInput())
                .forEach(act ->
                        output.linkTemplateAndInstance(act)
                );
    }

    @Override
    public void patternVisit(Visitor v) {
    }
}