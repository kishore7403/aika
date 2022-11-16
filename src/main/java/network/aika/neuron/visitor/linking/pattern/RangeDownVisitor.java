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
package network.aika.neuron.visitor.linking.pattern;

import network.aika.Thought;
import network.aika.neuron.Range;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.PatternActivation;
import network.aika.neuron.visitor.DownVisitor;

import static network.aika.neuron.Range.join;

/**
 * @author Lukas Molzberger
 */
public class RangeDownVisitor extends DownVisitor<PatternActivation> {

    private PatternActivation fromAct;
    private Range range = null;

    public RangeDownVisitor(PatternActivation fromAct) {
        super(fromAct.getThought());
        this.fromAct = fromAct;
    }

    public Range getRange() {
        return range;
    }

    @Override
    public void check(Link lastLink, Activation act) {
        if(act == fromAct)
            return;

        PatternActivation pAct = (PatternActivation) act;
        range = join(range, pAct.getRange());
    }

    @Override
    public void up(PatternActivation origin) {
    }

    protected void visitDown(Link l) {
        l.rangeVisitDown(this);
    }

    protected void visitDown(Activation act, Link l) {
        act.rangeVisitDown(this, l);
    }
}
