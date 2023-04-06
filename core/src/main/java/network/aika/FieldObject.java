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
package network.aika;

import network.aika.direction.Direction;
import network.aika.fields.AbstractFieldLink;
import network.aika.fields.Field;
import network.aika.sign.Sign;
import network.aika.utils.Bound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.sign.Sign.NEG;
import static network.aika.sign.Sign.POS;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class FieldObject {

    protected double frequency;
    protected double frequencyIPosOPos;
    protected double frequencyIPosONeg;
    protected double frequencyINegOPos;
    List<Field> fields = new ArrayList<>();

    public void register(Field field) {
        assert field != null;
        fields.add(field);
    }

    public void connect(Direction dir, boolean initialize, boolean borderCrossingOnly) {
        getFieldLinks(dir, borderCrossingOnly)
                .forEach(fl ->
                        fl.connect(initialize)
                );
    }

    public void disconnect(Direction dir, boolean deinitialize, boolean unlink, boolean borderCrossingOnly) {
        getFieldLinks(dir, borderCrossingOnly)
                .collect(Collectors.toList())
                .forEach(fl -> {
                    fl.disconnect(deinitialize);
                    if(unlink)
                        fl.unlink();
                });
    }

    private Stream<AbstractFieldLink> getFieldLinks(Direction dir, boolean borderCrossingOnly) {
        Stream<AbstractFieldLink> fieldLinks = fields.stream()
                .flatMap(f ->
                        (
                                dir == Direction.OUTPUT? f.getReceivers() : f.getInputs()
                        ).stream()
                );
        fieldLinks = fieldLinks.filter(Objects::nonNull);

        if(borderCrossingOnly)
            fieldLinks = fieldLinks.filter(fl -> fl.crossesBorder());

        return fieldLinks;
    }

    public void disconnect() {
        disconnect(Direction.INPUT, false, true, true);
        disconnect(Direction.OUTPUT, false, true, true);
    }

    public double getFrequency(Sign s, double n) {
        return s == POS ?
                frequency :
                n - frequency;
    }

    public double getProbability(Sign s, double n, boolean addCurrentInstance) {
        double f = getFrequency(s, n);

        if(addCurrentInstance) {
            f += 1.0;
            n += 1.0;
        }

        return Bound.UPPER.probability(f, n);
    }

    public double getFrequency(Sign inputSign, Sign outputSign, double n) {
        if(inputSign == POS && outputSign == POS) {
            return frequencyIPosOPos;
        } else if(inputSign == POS && outputSign == NEG) {
            return frequencyIPosONeg;
        } else if(inputSign == NEG && outputSign == POS) {
            return frequencyINegOPos;
        }

        //TODO:
        return Math.max(n - (frequencyIPosOPos + frequencyIPosONeg + frequencyINegOPos), 0);
    }

    public double getProbability(Sign inputSign, Sign outputSign, double n, boolean addCurrentInstance) {
        double frequency = getFrequency(inputSign, outputSign, n);

        // Add the current instance
        if(addCurrentInstance) {
            frequency += 1.0;
            n += 1.0;
        }

        return Bound.UPPER.probability(frequency, n);
    }
}
