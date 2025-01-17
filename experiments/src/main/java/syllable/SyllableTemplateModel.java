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
package syllable;

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.elements.synapses.*;

/**
 *
 * @author Lukas Molzberger
 */
public class SyllableTemplateModel {

    Model model;

    CategoryNeuron letterCategory;

    TokenNeuron letterPN;
    LatentRelationNeuron relPT;
    LatentRelationNeuron relNT;

    InhibitoryNeuron inhibitoryN;

    CategoryNeuron inhibCat;

    PatternNeuron syllablePatternN;

    CategoryNeuron syllableCategory;

    double letterPatternNetTarget;
    double letterPatternValueTarget;

    static double POS_MARGIN = 1.0;
    static double NEG_MARGIN = 1.1;


    public SyllableTemplateModel(Model m) {
        model = m;

        relPT = TokenPositionRelationNeuron.lookupRelation(model, -1, -1);
        relNT = TokenPositionRelationNeuron.lookupRelation(model, 1, 1);

        letterCategory = new PatternCategoryNeuron()
                .init(model, "Letter Category");

        letterPN = new TokenNeuron()
                .init(model, "Abstract Letter");

        CategoryInputSynapse pCatInputSyn = new PatternCategoryInputSynapse()
                .setWeight(1.0)
                .init(letterCategory, letterPN);

        letterPatternNetTarget = 4.0;
        letterPatternValueTarget = ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT
                .f(letterPatternNetTarget);

        System.out.println("Letter Pattern: netTarget:" + letterPatternNetTarget + " valueTarget:" + letterPatternValueTarget);

        letterPN.setBias(letterPatternNetTarget - pCatInputSyn.getWeight().getCurrentValue());
    }


    public void initMeta() {
        // Abstract
        syllablePatternN = new PatternNeuron()
                .init(model, "Syllable");

        syllableCategory = new PatternCategoryNeuron()
                .init(model, "Syllable Category");

        inhibitoryN =new InhibitoryNeuron()
                .init(model, "I");

        inhibCat = new InhibitoryCategoryNeuron()
                .init(model, "Inhib. Category");

        double patternNetTarget = 0.7;
        double patternValueTarget = ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT
                .f(patternNetTarget);

        System.out.println("Syllable Pattern: netTarget:" + patternNetTarget + " valueTarget:" + patternValueTarget);

        BindingNeuron sylBeginBN = createStrongBindingNeuron(
                patternValueTarget,
                0,
                null,
                null
        );

        expandContinueBindingNeurons(
                patternValueTarget,
                sylBeginBN,
                5,
                1
        );

        expandContinueBindingNeurons(
                patternValueTarget,
                sylBeginBN,
                5,
                -1
        );

        new PatternCategoryInputSynapse()
                .setWeight(1.0)
                .init(syllableCategory, syllablePatternN);

        syllablePatternN.setBias(patternNetTarget);

        new InhibitoryCategoryInputSynapse()
                .setWeight(1.0)
                .init(inhibCat, inhibitoryN);
    }

    private void expandContinueBindingNeurons(
            double patternValueTarget,
            BindingNeuron sylBeginBN,
            int length,
            int dir
    ) {
        BindingNeuron lastSylBN = sylBeginBN;
        int lastPos = 0;
        for(int pos = 1; pos <= length; pos++) {
            if(pos < 2) {
                lastSylBN = createStrongBindingNeuron(
                        patternValueTarget,
                        dir * pos,
                        dir * lastPos,
                        lastSylBN
                );
            } else {
                lastSylBN = createWeakBindingNeuron(
                        patternValueTarget,
                        dir * pos,
                        lastSylBN
                );
            }
            lastPos = pos;
        }
    }

    private BindingNeuron createStrongBindingNeuron(
            double patternValueTarget,
            int pos,
            Integer lastPos,
            BindingNeuron lastBN
    ) {
        double netTarget = 2.5;
        double valueTarget = ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT
                .f(netTarget);

        System.out.println("Strong Binding-Neuron: netTarget:" + netTarget + " valueTarget:" + valueTarget);

        CategoryNeuron catN = new BindingCategoryNeuron()
                .init(model, "Cat. Pos:" + pos);

        BindingNeuron bn = new BindingNeuron()
                .init(model, "Abstract (S) Pos:" + pos);

        new PrimaryInhibitorySynapse()
                .setWeight(1.0)
                .init(letterPN, inhibitoryN);

        new InhibitorySynapse(Scope.INPUT)
                .setWeight(1.0)
                .init(bn, inhibitoryN);

        new NegativeFeedbackSynapse()
                .setWeight(NEG_MARGIN * -netTarget)
                .init(inhibitoryN, bn);

        if(lastPos == null || lastBN == null) {
            bn.setCallActivationCheckCallback(true);
        } else {
            double prevNetTarget = lastBN.getBias().getCurrentValue();
            double prevValueTarget = ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT
                    .f(prevNetTarget);

            if (pos > 0) {
                new RelationInputSynapse()
                        .setWeight(5.0)
                        .init(relPT, bn)
                        .adjustBias();
            } else {
                new RelationInputSynapse()
                        .setWeight(5.0)
                        .init(relNT, bn)
                        .adjustBias();
            }

            SamePatternSynapse spSyn = new SamePatternSynapse()
                    .setWeight(10.0)
                    .init(lastBN, bn)
                    .adjustBias(prevValueTarget);

            System.out.println("  " + spSyn + " targetNetContr:" + -spSyn.getSynapseBias().getCurrentValue());
        }

        PatternSynapse pSyn = new PatternSynapse()
                .setWeight(2.5)
                .setOptional(pos != 0)
                .init(bn, syllablePatternN)
                .adjustBias(valueTarget);

        System.out.println("  " + pSyn + " targetNetContr:" + -pSyn.getSynapseBias().getCurrentValue());


        PositiveFeedbackSynapse posFeedSyn = new PositiveFeedbackSynapse()
                .setWeight(POS_MARGIN * (netTarget / patternValueTarget))
                .init(syllablePatternN, bn)
                .adjustBias(patternValueTarget);

        System.out.println("  " + posFeedSyn + " targetNetContr:" + -posFeedSyn.getSynapseBias().getCurrentValue());


        new InputPatternSynapse()
                .setWeight(10.0)
                .init(letterPN, bn)
                .adjustBias(letterPatternValueTarget);

        new BindingCategoryInputSynapse()
                .setWeight(1.0)
                .init(catN, bn);

        bn.setBias(netTarget);

        System.out.println();
        System.out.println();

        return bn;
    }

    private BindingNeuron createWeakBindingNeuron(
            double patternValueTarget,
            int pos,
            BindingNeuron lastBN
    ) {
        double netTarget = 0.5;
        double valueTarget = ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT
                .f(netTarget);

        System.out.println("Weak Binding-Neuron: netTarget:" + netTarget + " valueTarget:" + valueTarget);


        CategoryNeuron catN = new BindingCategoryNeuron()
                .init(model, "Cat. Pos:" + pos);

        BindingNeuron bn = new BindingNeuron()
                .init(model, "Abstract (W) Pos:" + pos);

        new PrimaryInhibitorySynapse()
                .setWeight(1.0)
                .init(letterPN, inhibitoryN);

        new InhibitorySynapse(Scope.INPUT)
                .setWeight(1.0)
                .init(bn, inhibitoryN);

        new NegativeFeedbackSynapse()
                .setWeight(NEG_MARGIN * -netTarget)
                .init(inhibitoryN, bn)
                .adjustBias();

        if (pos > 0) {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .init(relPT, bn)
                    .adjustBias();
        } else {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .init(relNT, bn)
                    .adjustBias();
        }

        double prevNetTarget = lastBN.getBias().getCurrentValue();
        double prevValueTarget = ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT
                .f(prevNetTarget);

        SamePatternSynapse spSyn = new SamePatternSynapse()
                .setWeight(5.0)
                .init(lastBN, bn)
                .adjustBias(prevValueTarget);

        System.out.println("  " + spSyn + " targetNetContr:" + -spSyn.getSynapseBias().getCurrentValue());


        new PatternSynapse()
                .setWeight(0.5)
                .setOptional(true)
                .init(bn, syllablePatternN)
                .adjustBias(valueTarget);

        PositiveFeedbackSynapse posFeedSyn = new PositiveFeedbackSynapse()
                .setWeight(POS_MARGIN * (netTarget / patternValueTarget))
                .init(syllablePatternN, bn)
                .adjustBias(patternValueTarget);

        System.out.println("  " + posFeedSyn + " targetNetContr:" + -posFeedSyn.getSynapseBias().getCurrentValue());


        new InputPatternSynapse()
                .setWeight(10.0)
                .init(letterPN, bn)
                .adjustBias(letterPatternValueTarget);

        new BindingCategoryInputSynapse()
                .setWeight(1.0)
                .init(catN, bn);

        bn.setBias(netTarget);

        return bn;
    }

    public TokenNeuron lookupInputToken(String label) {
        return model.lookupNeuronByLabel(label, l -> {
                    TokenNeuron n = letterPN.instantiateTemplate();

                    n.setTokenLabel(l);
                    n.setLabel(l);
                    n.setAllowTraining(false);

                    return n;
                }
        );
    }
}
