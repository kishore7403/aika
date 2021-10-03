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
package network.aika.neuron.steps.activation;

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.PatternActivation;
import network.aika.neuron.activation.direction.Direction;
import network.aika.neuron.steps.*;
import network.aika.neuron.linker.TemplateTask;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Uses the Template Network defined in the {@link network.aika.neuron.Templates} to induce new template
 * activations and links.
 *
 * @author Lukas Molzberger
 */
public class TemplatePropagate extends TaskStep<Activation, TemplateTask> {

    public static void add(Activation act) {
        Step.add(new TemplatePropagate(act));
    }

    private TemplatePropagate(Activation act) {
        super(act, new TemplateTask());
    }

    @Override
    public Phase getPhase() {
        return Phase.TEMPLATE;
    }

    @Override
    public StepType getStepType() {
        return StepType.TEMPLATE;
    }

    public boolean checkIfQueued() {
        return true;
    }

    @Override
    public void process() {
        Activation act = getElement();

        if (!act.getNeuron().allowTemplatePropagate(act))
            return;

        task.propagate(act);
    }

    public String toString() {
        return "Act-Step: Template-Propagate";
    }
}
