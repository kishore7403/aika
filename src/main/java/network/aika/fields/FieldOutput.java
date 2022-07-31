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

import network.aika.callbacks.UpdateListener;

import java.util.List;

import static network.aika.fields.FieldLink.createEventListener;
import static network.aika.fields.FieldLink.createUpdateListener;

/**
 * @author Lukas Molzberger
 */
public interface FieldOutput {

    String getLabel();

    String getValueString();

    boolean isInitialized();

    double getCurrentValue();

    static double getCurrentValue(FieldLink f) {
        return f != null ? f.getInput().getCurrentValue() : 0.0;
    }

    void addOutput(FieldLink l, boolean propagateInitValue);

    void removeOutput(FieldLink l, boolean propagateFinalValue);

    List<FieldLink> getReceivers();

    void disconnect();

    Object getReference();

    default void addUpdateListener(UpdateListener updateListener) {
        addOutput(createUpdateListener(updateListener), true);
    }

    default void addEventListener(FieldOnTrueEvent eventListener) {
        addOutput(createEventListener(eventListener), true);
    }
}
