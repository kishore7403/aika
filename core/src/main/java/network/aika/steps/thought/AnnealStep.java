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
package network.aika.steps.thought;

import network.aika.Thought;
import network.aika.elements.neurons.ActivationFunction;
import network.aika.steps.Phase;
import network.aika.steps.Step;
import network.aika.utils.Utils;

import static network.aika.steps.Phase.*;


/**
 *
 * @author Lukas Molzberger
 */
public class AnnealStep extends Step<Thought> {

    double nextStep;

    public static void add(Thought t) {
        add(new AnnealStep(t));
    }

    public AnnealStep(Thought t) {
        super(t);
    }

    @Override
    public void process() {
        Thought t = getElement();

        nextStep = t.getConfig().getAnnealStepSize() / ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT.outerGrad(t.getAnnealing().getCurrentValue());
        double nextAnnealValue = nextStep + t.getAnnealing().getCurrentValue();
        nextAnnealValue = Math.min(nextAnnealValue, 1.0);

        t.getAnnealing().setValue(nextAnnealValue);

        if (nextAnnealValue < 1.0)
            AnnealStep.add(t);
    }

    @Override
    public Phase getPhase() {
        return ANNEAL;
    }

    @Override
    public String toString() {
        return "docId:" + getElement().getId() +
                " NextStep:" + Utils.round(nextStep, 100000.0) +
                " NextAnnealValue:" + Utils.round(getElement().getAnnealing().getCurrentValue(), 100000.0);
    }
}
