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
package network.aika.debugger.activations.properties;

import network.aika.elements.links.FeedbackLink;
import network.aika.elements.links.NegativeFeedbackLink;
import network.aika.elements.links.PositiveFeedbackLink;

/**
 * @author Lukas Molzberger
 */
public class FeedbackLinkPropertyPanel<L extends FeedbackLink> extends ConjunctiveLinkPropertyPanel<L> {

    public FeedbackLinkPropertyPanel(L l) {
        super(l);
    }

    @Override
    public void initIdentitySection(L l) {
        super.initIdentitySection(l);
    }

    @Override
    public void initInferenceSection(L l) {
        super.initInferenceSection(l);
    }

    @Override
    public void initTrainingSection(L l) {
        super.initTrainingSection(l);
    }


    public static FeedbackLinkPropertyPanel create(FeedbackLink l) {
        if(l instanceof PositiveFeedbackLink) {
            return new PositiveFeedbackLinkPropertyPanel((PositiveFeedbackLink) l);
        } else if(l instanceof NegativeFeedbackLink) {
            return new NegativeFeedbackLinkPropertyPanel((NegativeFeedbackLink) l);
        }

        return new FeedbackLinkPropertyPanel(l);
    }
}
