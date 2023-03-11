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
package network.aika.debugger.activations.layout;


import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.elements.links.*;
import org.graphstream.graph.Edge;

/**
 * @author Lukas Molzberger
 */
public class BindingParticleLink<L extends BindingNeuronLink> extends ParticleLink<L> {

    public BindingParticleLink(L l, Edge e, ActivationGraphManager gm) {
        super(l, e, gm);
    }

    public static ParticleLink create(BindingNeuronLink l, Edge e, ActivationGraphManager gm) {
        if(l instanceof InputPatternLink) {
            return InputPatternParticleLink.create((InputPatternLink) l, e, gm);
        } else if (l instanceof FeedbackLink<?, ?>) {
            return FeedbackParticleLink.create((FeedbackLink) l, e, gm);
        } else if (l instanceof SamePatternLink) {
            return SamePatternParticleLink.create((SamePatternLink) l, e, gm);
        } else if (l instanceof RelationInputLink) {
            return RelationInputParticleLink.create((RelationInputLink) l, e, gm);
        }
        return new BindingParticleLink(l, e, gm);
    }
}