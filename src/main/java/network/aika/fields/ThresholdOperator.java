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

import java.util.List;

/**
 * @author Lukas Molzberger
 */
public class ThresholdOperator extends AbstractFunction {

    public enum Type {
        ABOVE,
        BELOW,
        ABOVE_ABS
    }

    private double threshold;
    private Type type;

    public ThresholdOperator(String label, double threshold, Type type) {
        super(label);
        this.threshold = threshold;
        this.type = type;
    }

    @Override
    protected double applyFunction(double x) {
        switch (type) {
            case ABOVE:
                return x > threshold ? 1.0 : 0.0;
            case BELOW:
                return x < threshold ? 1.0 : 0.0;
            case ABOVE_ABS:
                return Math.abs(x) > threshold ? 1.0 : 0.0;
            default:
                return 0.0;
        }
    }
}
