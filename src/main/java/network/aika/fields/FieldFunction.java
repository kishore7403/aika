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
package network.aika.fields;

import network.aika.neuron.activation.Element;

import java.util.function.DoubleFunction;

/**
 * @author Lukas Molzberger
 */
public class FieldFunction extends AbstractFunction {

    private DoubleFunction<Double> function;

    public FieldFunction(Element ref, String label, DoubleFunction<Double> f) {
        super(ref, label);
        this.function = f;
    }

    @Override
    protected double computeUpdate(FieldLink fl, double u) {
        return function.apply(fl.getInput().getNewValue()) - currentValue;
    }
}
