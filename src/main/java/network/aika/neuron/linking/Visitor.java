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
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.PatternActivation;
import network.aika.neuron.conjunctive.LatentRelationNeuron;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * @author Lukas Molzberger
 */
public abstract class Visitor {

    long v;
    Direction dir;

    public Visitor(Thought t) {
        this.v = t.getNewVisitorId();
        this.dir = Direction.INPUT;
    }

    protected Visitor(Visitor parent, Direction dir) {
        this.v = parent.v;
        this.dir = dir;
    }

    public abstract Visitor up(PatternActivation origin);
    public long getV() {
        return v;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public abstract void check(Link lastLink, Activation act);
}
