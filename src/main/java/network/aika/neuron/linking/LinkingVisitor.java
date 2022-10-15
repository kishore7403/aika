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
package network.aika.neuron.linking;

import network.aika.Thought;
import network.aika.direction.Direction;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.PatternActivation;

/**
 * @author Lukas Molzberger
 */
public class LinkingVisitor extends Visitor {

    LinkingOperator operator;

    public LinkingVisitor(Thought t, LinkingOperator operator) {
        super(t);

        this.operator = operator;
    }

    protected LinkingVisitor(LinkingVisitor parent, Direction dir) {
        super(parent, dir);
    }

    @Override
    public LinkingVisitor up(PatternActivation origin) {
        return new LinkingVisitor(this, Direction.OUTPUT);
    }

    public void check(Link lastLink, Activation act) {
        operator.check(lastLink, act);
    }
}
