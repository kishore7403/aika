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
package network.aika.neuron.activation;


import network.aika.Document;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class Position {

    public static final Position MIN = new Position(null, Integer.MIN_VALUE);
    public static final Position MAX = new Position(null, Integer.MAX_VALUE);


    private SortedMap<ActKey, Activation> activations = new TreeMap<>();


    private Document doc;
    private int id;
    private Integer finalPosition;


    public Position(Document doc) {
        this(doc, null);
    }


    public Position(Document doc, Integer pos) {
        this.doc = doc;
        this.id = doc != null ? doc.positionIdCounter++ : -1;
        finalPosition = pos;
    }

    public int getId() {
        return id;
    }

    public Document getDocument() {
        return doc;
    }


    public static int compare(Position a, Position b) {
        if(a == null && b != null) return -1;
        else if(a != null && b == null) return 1;
        else if(a == null && b == null) return 0;
        else return a.compare(b);
    }

    public int compare(Position pos) {
        if(finalPosition != null && pos.finalPosition != null) {
            return Integer.compare(finalPosition, pos.finalPosition);
        }

        return 0; // TODO:
    }


    public String toString() {
        return finalPosition != null ? "" + finalPosition : "<" + id + ">";
    }


    public Integer getDistance(Position pos) {
        if(finalPosition != null && pos.finalPosition != null) {
            return pos.finalPosition - finalPosition;
        }
        return null;
    }


    public Integer getFinalPosition() {
        return finalPosition;
    }


    public void setFinalPosition(int finalPos) {
        finalPosition = finalPos;
    }


    public void addActivation(Integer slot, Activation act) {
        activations.put(new ActKey(slot, act.getId()), act);
    }


    public Stream<Activation> getActivations(int slot) {
        return activations.subMap(new ActKey(slot, Integer.MIN_VALUE), new ActKey(slot, Integer.MAX_VALUE))
                .values()
                .stream();
    }

    public Collection<Activation> getActivations() {
        return activations.values();
    }


    private static class ActKey implements Comparable<ActKey> {
        int slot;
        int actId;

        public ActKey(int slot, int actId) {
            this.slot = slot;
            this.actId = actId;
        }

        @Override
        public int compareTo(ActKey ak) {
            int r = Integer.compare(slot, ak.slot);
            if(r != 0) return r;
            return Integer.compare(actId, ak.actId);
        }
    }

}
