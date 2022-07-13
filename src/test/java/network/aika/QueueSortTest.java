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

import network.aika.neuron.activation.Timestamp;
import network.aika.steps.Phase;
import network.aika.steps.QueueKey;
import network.aika.steps.Step;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static network.aika.neuron.activation.Timestamp.NOT_SET;

/**
 *
 * @author Lukas Molzberger
 */
public class QueueSortTest {

    @Test
    public void testQueueSorting() {
        TreeMap<QueueKey, Integer> testQueue = new TreeMap<>(QueueKey.COMPARATOR);
/*
        testQueue.put(new TestQueueKey(null, 321, 0.0, 385), 1);
        testQueue.put(new TestQueueKey(337l, 316, 0.0, 384), 2);
*/

        TestQueueKey tqk3 = new TestQueueKey(null, 36, -1802, 78);

        testQueue.put(tqk3, 3);

        testQueue.put(new TestQueueKey(null, 386, 0, 393), 2);
        testQueue.put(new TestQueueKey(6l, 2, 0, 339), 1);

        System.out.println();

        Integer removedStep =testQueue.remove(tqk3);

        System.out.println();
    }

    private static class TestQueueKey implements QueueKey {

        private Timestamp fired;
        private Timestamp created;
        private int sortValue;
        private Timestamp currentTimestamp;

        public TestQueueKey(Long fired, long created, int sortValue, long currentTimestamp) {
            this.fired = fired != null ? new Timestamp(fired) : NOT_SET;
            this.created = new Timestamp(created);
            this.sortValue = sortValue;
            this.currentTimestamp = new Timestamp(currentTimestamp);
        }

        @Override
        public String getStepName() {
            return "";
        }

        @Override
        public Phase getPhase() {
            return Phase.PROCESSING;
        }

        @Override
        public Timestamp getFired() {
            return fired;
        }

        @Override
        public Timestamp getCreated() {
            return created;
        }

        @Override
        public int getSortValue() {
            return sortValue;
        }

        @Override
        public Timestamp getCurrentTimestamp() {
            return currentTimestamp;
        }
    }
}