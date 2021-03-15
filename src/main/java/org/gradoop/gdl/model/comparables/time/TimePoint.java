/*
 * Copyright 2017 The GDL Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradoop.gdl.model.comparables.time;

import org.gradoop.gdl.model.comparables.ComparableExpression;

import java.util.Optional;

/**
 * Represents a timestamp
 */
public abstract class TimePoint implements ComparableExpression {

    /**
     * Calculates the value of the timestamp (UNIX epoch long), if possible.
     * E.g., a timestamp like v.VAL_FROM can not be assigned a unique long value
     *
     * @return UNIX epoch long, -1 if it can not be determined
     */
    public abstract Optional<Long> evaluate();
}
