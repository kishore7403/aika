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
package network.aika.text;

import network.aika.Model;
import network.aika.callbacks.SuspensionCallback;
import network.aika.neuron.conjunctive.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.utils.TestUtils.createNeuron;

/**
 *
* @author Lukas Molzberger
*/
public class TextModel extends Model {

    public TextModel() {
        super();
    }

    public TextModel(SuspensionCallback sc) {
        super(sc);
    }

    public LatentRelationNeuron lookupRelation(int rangeBegin, int rangeEnd) {
        return lookupNeuron("Rel.: " + rangeBegin + "," + rangeEnd, l -> {
            LatentRelationNeuron n = getTemplates().LATENT_RELATION_TEMPLATE.instantiateTemplate(true);
            n.setLabel(l);

            n.setRangeBegin(rangeBegin);
            n.setRangeEnd(rangeEnd);

            n.getBias().receiveUpdate(-4.0);
            n.setAllowTraining(false);
            n.updateSumOfLowerWeights();
            return n;
        });
    }

    public PatternNeuron lookupToken(String tokenLabel) {
        return lookupNeuron(tokenLabel, l -> {
            PatternNeuron n = getTemplates().PATTERN_TEMPLATE.instantiateTemplate(true);

            n.setTokenLabel(l);
            n.setNetworkInput(true);
            n.setLabel(l);
            n.setAllowTraining(false);
            return n;
        });
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);
    }
}
