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
package network.aika.visitor;

import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;

/**
 * @author Lukas Molzberger
 */
public abstract class Visitor {

    private long v;

    public Visitor(Thought t) {
        this.v = t.getNewVisitorId();
    }

    protected Visitor(Visitor parent) {
        this.v = parent.v;
    }

    public abstract void next(Activation<?> act);

    public abstract void next(Link<?, ?, ?> l);

    public long getV() {
        return v;
    }

    public abstract void check(Link lastLink, Activation act);

    public abstract boolean isDown();

    public abstract boolean isUp();
}
