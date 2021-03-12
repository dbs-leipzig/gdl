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
package org.s1ck.gdl.model.comparables.time;

import org.s1ck.gdl.model.comparables.ComparableExpression;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the duration of an interval
 */
public class Duration extends TimePoint {

    /**
     * The from value of the interval
     */
    private final TimePoint from;

    /**
     * The to value of the interval
     */
    private final TimePoint to;

    /**
     * Creates duration of an interval represented by {@code from} and {@code to}
     * @param from from value of the interval
     * @param to to value of the interval
     */
    public Duration(TimePoint from, TimePoint to){
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the from value of the interval
     * @return from value of the interval
     */
    public TimePoint getFrom(){
        return from;
    }

    /**
     * Returns the to value of the interval
     * @return to value of the interval
     */
    public TimePoint getTo(){
        return to;
    }

    @Override
    public Set<String> getVariables() {
        Set<String> vars = from.getVariables();
        vars.addAll(to.getVariables());
        return vars;
    }

    @Override
    public String getVariable() {
        return null;
    }

    @Override
    public boolean containsSelectorType(TimeSelector.TimeField type) {
        return from.containsSelectorType(type) || to.containsSelectorType(type);
    }

    @Override
    public boolean isGlobal() {
        return from.isGlobal() || to.isGlobal();
    }

    @Override
    public ComparableExpression replaceGlobalByLocal(List<String> variables) {
        return new Duration((TimePoint)from.replaceGlobalByLocal(variables),
                (TimePoint)to.replaceGlobalByLocal(variables));
    }

    @Override
    public Optional<Long> evaluate() {
        Optional<Long> evalFrom = from.evaluate();
        Optional<Long> evalTo = to.evaluate();
        if(!(evalFrom.isPresent() && evalTo.isPresent())){
            return Optional.empty();
        }
        else{
            return Optional.of(evalTo.get() - evalFrom.get());
        }
    }

    @Override
    public String toString(){
        return "Duration("+from.toString()+", "+to.toString()+")";
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration that = (Duration) o;
        return that.from.equals(from) && that.to.equals(to);
    }
}
