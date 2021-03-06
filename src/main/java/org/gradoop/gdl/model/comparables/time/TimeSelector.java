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
import org.gradoop.gdl.model.predicates.booleans.And;
import org.gradoop.gdl.model.predicates.booleans.Or;
import org.gradoop.gdl.model.predicates.expressions.Comparison;
import org.gradoop.gdl.utils.Comparator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.gradoop.gdl.utils.Comparator.*;

/**
 * Represents a timestamp selection of a graph variable, e.g. v.VAL_FROM selects the VAL_FROM value of a graph element v
 */
public class TimeSelector extends TimeAtom{

    /**
     * The variable name
     */
    private final String variable;

    /**
     * The time property selected (VAL_FROM, VAL_TO, TX_FROM, TX_TO)
     */
    private final TimeField timeProp;

    /**
     * Variable name that indicates a global time selector, referring to the whole pattern
     */
    public final static String GLOBAL_SELECTOR = "___global";

    /**
     *
     * All time properties defined by TPGM
     */
    public enum TimeField{
        VAL_FROM,
        VAL_TO,
        TX_FROM,
        TX_TO
    }

    /**
     * Initializes a TimeSelector given a variable and a time property (VAL_FROM, VAL_TO, TX_FROM, TX_TO)
     * @param variable the variable name
     * @param field the time property as defined by the TPGM
     */
    public TimeSelector(String variable, TimeField field){
        this.variable = variable;
        this.timeProp = field;
    }

    /**
     * Initializes a global TimeSelector given a time property (VAL_FROM, VAL_TO, TX_FROM, TX_TO)
     * @param field the time property as defined by the TPGM
     */
    public TimeSelector(TimeField field){
        this(GLOBAL_SELECTOR, field);
    }

    /**
     * Initializes a global TimeSelector given the string representation of a  time property
     * (VAL_FROM, VAL_TO, TX_FROM, TX_TO)
     * @param field the time property as defined by the TPGM. Must be one of "val_from", "val_to", "tx_from", "tx_to"
     *              (cases irrelevant)
     */
    public TimeSelector(String field){
        this(GLOBAL_SELECTOR, field);
    }

    /**
     * Initializes a TimeSelector given a variable and the string representation of a  time property
     * (VAL_FROM, VAL_TO, TX_FROM, TX_TO)
     * @param variable the variable name
     * @param field the time property as defined by the TPGM. Must be one of "val_from", "val_to", "tx_from", "tx_to"
     *              (cases irrelevant)
     */
    public TimeSelector(String variable, String field){
        this(variable, stringToField(field));
    }


    @Override
    public Set<String> getVariables(){
        Set<String> ls = new HashSet<>();
        ls.add(variable);
        return ls;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    /**
     * Returns the TimeField (VAL_FROM, VAL_TO, TX_FROM, TX_TO)
     *
     * @return the TimeField
     */
    public TimeField getTimeProp(){
        return timeProp;
    }

    @Override
    public Optional<Long> evaluate(){
        return Optional.empty();
    }

    @Override
    public boolean containsSelectorType(TimeSelector.TimeField type){
        return timeProp.equals(type);
    }

    @Override
    public Predicate unfoldGlobal(Comparator comp, ComparableExpression rhs, List<String> variables) {
        if(!variable.equals(GLOBAL_SELECTOR)){
            return new Comparison(this, comp, rhs);
        }
        if(comp.equals(EQ)){
            return unfoldGlobalEQ(rhs, variables);
        }
        else if(comp.equals(Comparator.NEQ)){
            return unfoldGlobalNEQ(rhs, variables);
        }
        else if(comp.equals(Comparator.LT)){
            return unfoldGlobalLT(rhs, variables);
        }
        else if(comp.equals(Comparator.LTE)){
            return unfoldGlobalLTE(rhs, variables);
        }
        else if(comp.equals(Comparator.GT)){
            return unfoldGlobalGT(rhs, variables);
        }
        else if(comp.equals(Comparator.GTE)){
            return unfoldGlobalGTE(rhs, variables);
        }
        return null;
    }

    /**
     * Translates a comparison {@code (this == rhs)} into an equivalent predicate that does not contain
     * global time selectors/intervals anymore
     *
     * @param rhs the right hand side of the comparison to translate
     * @param variables all query variables
     * @return translated comparison
     */
    private Predicate unfoldGlobalEQ(ComparableExpression rhs, List<String> variables){
        // exists var: var.from==rhs

        Predicate exists = existsVariable(EQ, rhs, variables);

        if(timeProp.equals(TimeField.TX_FROM) || timeProp.equals(TimeField.VAL_FROM)){
            //globalfrom==rhs <=> (exists var: var.from==rhs) AND (forall var: var.from<=rhs)
            return new And(exists,forAllVariables(LTE, rhs, variables));
        }

        else{
            //globalto == rhs <=> (exists var: var.to ==rhs) AND (forall var: var.to>=rhs)
            return new And(exists,forAllVariables(GTE, rhs, variables));
        }
    }

    /**
     * Translates a comparison {@code (this != rhs)} into an equivalent predicate that does not contain
     * global time selectors/intervals anymore.
     *
     * @param rhs the right hand side of the comparison to translate
     * @param variables all query variables
     * @return translated comparison
     */
    private Predicate unfoldGlobalNEQ(ComparableExpression rhs, List<String> variables){
        return forAllVariables(NEQ, rhs, variables);
    }

    /**
     * Translates a comparison {@code (this < rhs)} into an equivalent predicate that does not contain
     * global time selectors/intervals anymore.
     *
     * @param rhs the right hand side of the comparison to translate
     * @param variables all query variables
     * @return translated comparison
     */
    private Predicate unfoldGlobalLT(ComparableExpression rhs, List<String> variables){
        if(timeProp.equals(TimeField.TX_FROM) || timeProp.equals(TimeField.VAL_FROM)){
            // globalfrom < rhs   <=>   forall var: var.from < rhs
            return forAllVariables(LT, rhs, variables);
        }
        else{
            //globalto < rhs   <=>   exists var: var.to < rhs
            return existsVariable(LT, rhs, variables);
        }
    }

    /**
     * Translates a comparison {@code (this <= rhs)} into an equivalent predicate that does not contain
     * global time selectors/intervals anymore.
     *
     * @param rhs the right hand side of the comparison to translate
     * @param variables all query variables
     * @return translated comparison
     */
    private Predicate unfoldGlobalLTE(ComparableExpression rhs, List<String> variables){
        if(timeProp.equals(TimeField.TX_FROM) || timeProp.equals(TimeField.VAL_FROM)){
            // globalfrom <= rhs   <=>   forall var: var.from <= rhs
            return forAllVariables(LTE, rhs, variables);
        }
        else{
            //globalto <= rhs   <=>   exists var: var.to <= rhs
            return existsVariable(LTE, rhs, variables);
        }
    }

    /**
     * Translates a comparison {@code (this > rhs)} into an equivalent predicate that does not contain
     * global time selectors/intervals anymore.
     *
     * @param rhs the right hand side of the comparison to translate
     * @param variables all query variables
     * @return translated comparison
     */
    private Predicate unfoldGlobalGT(ComparableExpression rhs, List<String> variables){
        if(timeProp.equals(TimeField.TX_FROM) || timeProp.equals(TimeField.VAL_FROM)){
            // globalfrom > rhs   <=>   exists var: var.from > rhs
            return existsVariable(GT, rhs, variables);
        }
        else{
            //globalto > rhs   <=>   forall var: var.to > rhs
            return forAllVariables(GT, rhs, variables);
        }
    }

    /**
     * Translates a comparison {@code (this >= rhs)} into an equivalent predicate that does not contain
     * global time selectors/intervals anymore.
     *
     * @param rhs the right hand side of the comparison to translate
     * @param variables all query variables
     * @return translated comparison
     */
    private Predicate unfoldGlobalGTE(ComparableExpression rhs, List<String> variables){
        if(timeProp.equals(TimeField.TX_FROM) || timeProp.equals(TimeField.VAL_FROM)){
            // globalfrom >= rhs   <=>   exists var: var.from >= rhs
            return existsVariable(GTE, rhs, variables);
        }
        else{
            //globalto >= rhs   <=>   forall var: var.to >= rhs
            return forAllVariables(GTE, rhs, variables);
        }
    }

    /**
     * Returns a predicate equivalent to {@code exists v in variables s.t. (v comp rhs) holds}
     *
     * @param comp the comparator
     * @param rhs the rhs in the comparison
     * @param variables the query variables to "iterate" over (the domain)
     * @return predicate equivalent to {@code exists v in variables s.t. (v comp rhs) holds}
     */
    private Predicate existsVariable(Comparator comp, ComparableExpression rhs, List<String> variables){
        Comparison c0 = new Comparison(new TimeSelector(variables.get(0),timeProp), comp, rhs);
        if(variables.size()==1){
            return c0;
        }
        Or exists = new Or(c0, new Comparison(new TimeSelector(variables.get(1),timeProp), comp, rhs));
        for(int i=2; i<variables.size(); i++){
            exists = new Or(exists, new Comparison(new TimeSelector(variables.get(i),timeProp), comp, rhs));
        }
        return exists;
    }

    /**
     * Returns a predicate equivalent to {@code forall v in variables: (v comp rhs) holds}
     *
     * @param comp the comparator
     * @param rhs the rhs in the comparison
     * @param variables the query variables to "iterate" over (the domain)
     * @return predicate equivalent to {@code forall v in variables: (v comp rhs) holds}
     */
    private Predicate forAllVariables(Comparator comp, ComparableExpression rhs, List<String> variables){
        Comparison c0 = new Comparison(new TimeSelector(variables.get(0),timeProp), comp, rhs);
        if(variables.size()==1){
            return c0;
        }

        And forall = new And(c0, new Comparison(new TimeSelector(variables.get(1),timeProp), comp, rhs));
        for(int i=2; i<variables.size(); i++){
            forall = new And(forall,
                    new Comparison(new TimeSelector(variables.get(i),timeProp),comp,rhs));
        }

        return forall;
    }

    @Override
    public boolean isGlobal(){
        return variable.equals(GLOBAL_SELECTOR);
    }

    @Override
    public ComparableExpression replaceGlobalByLocal(List<String> variables) {
        if(!variable.equals(GLOBAL_SELECTOR)){
            return this;
        }

        TimeSelector[] selectors = new TimeSelector[variables.size()];
        for(int i=0; i<variables.size(); i++){
            selectors[i] = new TimeSelector(variables.get(i), timeProp);
        }

        if(selectors.length == 1){
            return selectors[0];
        }

        if(timeProp.equals(TimeField.TX_FROM) || timeProp.equals(TimeField.VAL_FROM)){
            return new MaxTimePoint(selectors);
        }
        else{
            return new MinTimePoint(selectors);
        }
    }

    /**
     * Parses a string to a TimeField
     *
     * @param field a string equal to "tx_from", "tx_to", "val_from", "val_to" (cases irrelevant)
     * @return the corresponding TimeField
     */
    private static TimeField stringToField(String field){
        field = field.trim().toLowerCase();
        TimeField time;
        switch (field) {
            case "val_from":
                time = TimeField.VAL_FROM;
                break;
            case "val_to":
                time = TimeField.VAL_TO;
                break;
            case "tx_from":
                time = TimeField.TX_FROM;
                break;
            case "tx_to":
                time = TimeField.TX_TO;
                break;
            default:
                throw new IllegalArgumentException("The given string [" + field +
                  "] can not be parsed to a time field.");
        }
        return time;
    }

    @Override
    public String toString() {
        return variable + "." + timeProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSelector that = (TimeSelector) o;

        if (!Objects.equals(variable, that.variable)) {
            return false;
        }
        return Objects.equals(timeProp, that.timeProp);

    }

    @Override
    public int hashCode() {
        int result = variable != null ? variable.hashCode() : 0;
        result = 31 * result + (timeProp != null ? timeProp.hashCode() : 0);
        return result;
    }
}
