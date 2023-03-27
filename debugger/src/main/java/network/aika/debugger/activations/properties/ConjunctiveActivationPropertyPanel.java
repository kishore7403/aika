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
package network.aika.debugger.activations.properties;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.CategoryInputLink;

/**
 * @author Lukas Molzberger
 */
public class ConjunctiveActivationPropertyPanel<E extends ConjunctiveActivation> extends ActivationPropertyPanel<E> {


    public ConjunctiveActivationPropertyPanel(E act) {
        super(act);
    }

    @Override
    public void initTrainingSection(E act) {
        super.initTrainingSection(act);

        if(act.getTemplate() != null)
            addConstant("Template: ", getShortString(act.getTemplate()));

        Activation ati = act.getActiveTemplateInstance();
        if(ati != null)
            addConstant("Active Template-Instance: ", getShortString(ati));

        CategoryInputLink cil = act.getCategoryInputLink();
        if(cil == null || cil.getInput() == null)
            return;

        cil.getInput()
                .getCategoryInputs()
                .forEach(tiAct ->
                        addConstant("Template-Instance: ", getShortString(tiAct))
                );
    }
}