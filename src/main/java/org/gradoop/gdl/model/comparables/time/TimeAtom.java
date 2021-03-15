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
import org.gradoop.gdl.model.predicates.Predicate;
import org.gradoop.gdl.utils.Comparator;

import java.util.List;

/**
 * Base class for atoms in a {@link TimeTerm}, e.g. simple timestamps
 */
public abstract class TimeAtom extends TimePoint {

    /**
     * Translates a simple comparison described by {@code this comp rhs} to a equivalent
     * comparison that does not contain global time selectors / intervals anymore.
     *
     * @param comp the comparator of the comparison
     * @param rhs the right hand side of the comparison
     * @param variables all the variables in the query
     * @return equivalent containing only local selectors/intervals
     */
    public abstract Predicate unfoldGlobal(Comparator comp, ComparableExpression rhs, List<String> variables);
}
