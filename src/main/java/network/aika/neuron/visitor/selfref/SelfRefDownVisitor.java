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
package network.aika.neuron.visitor.selfref;

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.BindingActivation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.PatternActivation;
import network.aika.neuron.visitor.DownVisitor;

/**
 * @author Lukas Molzberger
 */
public class SelfRefDownVisitor extends DownVisitor<BindingActivation> {

    BindingActivation oAct;

    boolean isSelfRef;

    public SelfRefDownVisitor(BindingActivation oAct) {
        super(oAct.getThought());
        this.oAct = oAct;
    }

    public boolean isSelfRef() {
        return isSelfRef;
    }

    @Override
    public void up(BindingActivation origin) {
        if(origin == oAct || origin == oAct.getTemplate() || origin == oAct.getTemplateInstance())
            isSelfRef = true;
    }

    protected void visitDown(Link l) {
        l.selfRefVisitDown(this);
    }

    protected void visitDown(Activation act, Link l) {
        act.selfRefVisitDown(this, l);
    }
}