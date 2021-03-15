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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a constant duration via a fixed number of milliseconds
 * Not really a timestamp, but needed for certain related computations (e.g. deltas)
 */
public class TimeConstant extends TimePoint {

    /**
     * The number of milliseconds wrapped by this class
     */
    private final Long millis;

    /**
     * Create a constant of size days+hours+minutes+seconds+millis (in millis)
     *
     * @param days number of days
     * @param hours number of hours [0-23]
     * @param minutes number of minutes [0-59]
     * @param seconds number of seconds [0-59]
     * @param millis number of millis [0-999]
     */
    public TimeConstant(int days, int hours, int minutes, int seconds, int millis){
        long sum = millis;
        sum +=1000L*(long)seconds;
        sum +=1000L*60L*(long)minutes;
        sum +=1000L*60L*60L*(long)hours;
        sum +=1000*60L*60L*24L*(long)days;
        this.millis = sum;
    }

    /**
     * Creates a constant from the given milliseconds
     *
     * @param millis size of the constant in milliseconds
     */
    public TimeConstant(long millis){
        this.millis = millis;
    }

    /**
     * Return the wrapped number of milliseconds
     *
     * @return number of milliseconds
     */
    public long getMillis(){
        return millis;
    }

    @Override
    public String toString(){
        return "Constant("+getMillis()+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeConstant that = (TimeConstant) o;
        return getMillis()==that.getMillis();
    }

    @Override
    public Optional<Long> evaluate() {
        return Optional.of(getMillis());
    }

    @Override
    public Set<String> getVariables() {
        return new HashSet<>();
    }

    @Override
    public String getVariable() {
        return null;
    }

    @Override
    public boolean containsSelectorType(TimeSelector.TimeField type) {
        return false;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public ComparableExpression replaceGlobalByLocal(List<String> variables) {
        return this;
    }

}
