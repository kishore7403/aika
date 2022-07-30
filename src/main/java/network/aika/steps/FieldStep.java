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
package network.aika.steps;

import network.aika.neuron.activation.Element;
import network.aika.fields.QueueField;

/**
 *
 * @author Lukas Molzberger
 */
public class FieldStep<E extends Element> extends Step<E> {

    private QueueField field;

    public FieldStep(E e, QueueField qf) {
        super(e);
        this.field = qf;
        this.field.setStep(this);
    }

    @Override
    public void process() {
        field.process();
    }

    @Override
    public Phase getPhase() {
        return Phase.PROCESSING;
    }

    public String toString() {
        return "Field: " + field;
    }
}
