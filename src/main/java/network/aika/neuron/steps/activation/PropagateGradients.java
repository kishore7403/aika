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
import network.aika.neuron.steps.Step;
import network.aika.neuron.steps.link.PropagateGradientAndUpdateWeight;
import network.aika.utils.Utils;

import static network.aika.neuron.activation.direction.Direction.INPUT;
import static network.aika.neuron.activation.direction.Direction.OUTPUT;

/**
 * Propagates the gradient of this activation backwards to all its input-links.
 *
 * @author Lukas Molzberger
 */
public abstract class PropagateGradients extends Step<Activation>  {

    protected PropagateGradients(Activation act) {
        super(act);
    }

    protected void propagateGradientsOut(Activation act, double[] g) {
        Utils.checkTolerance(act, g);

        act.updateOutputGradientSum(g);

        PropagateGradientAndUpdateWeight.addInputs(act, g);

        UpdateBias.add(act, act.getConfig().getLearnRate() * Utils.sum(g));

        TemplateCloseLoop.add(act, INPUT);

//        addLinksToQueue(INPUT, LinkStep.TEMPLATE);

        if(!act.isFired())
            return;

        TemplatePropagate.add(act, INPUT);
        TemplateCloseLoop.add(act, OUTPUT);
        TemplatePropagate.add(act, OUTPUT);
    }
}
