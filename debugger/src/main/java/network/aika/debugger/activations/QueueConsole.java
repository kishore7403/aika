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
package network.aika.debugger.activations;

import network.aika.Thought;
import network.aika.debugger.AbstractConsole;
import network.aika.debugger.activations.renderer.QueueRenderer;
import network.aika.elements.Element;
import network.aika.steps.Step;

import javax.swing.text.StyledDocument;

/**
 * @author Lukas Molzberger
 */
public class QueueConsole extends AbstractConsole {

    public void renderQueue(StyledDocument sDoc, Thought t, Step currentQE, Element selectedElement) {
        QueueRenderer queueRenderer = new QueueRenderer(currentQE, selectedElement);

        queueRenderer.render(sDoc, t);
    }
}
