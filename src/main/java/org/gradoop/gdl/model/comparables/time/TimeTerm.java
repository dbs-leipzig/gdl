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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for more abstract TimestampExpressions that may combine several timestamps,
 * e.g. MIN(p1,p2) is a timestamp, while p1 and p2 are timestamps, too
 * Allows to build complex functions of timestamps that yield timestamps
 */
public abstract class TimeTerm extends TimePoint {

    /**
     * List of arguments (i.g. more than one)
     */
    protected ArrayList<TimePoint> args;

    /**
     * String representation of the operator, e.g. "MIN", "MAX", ...
     */
    protected String operator = "";

    /**
     * Initialize a complex expression by its arguments (TimePoints)
     *
     * @param args the arguments
     */
    protected TimeTerm(TimePoint... args){
        if(args.length < 2){
            throw new IllegalArgumentException("At least two arguments are needed.");
        }
        this.args = new ArrayList<>();
        Collections.addAll(this.args, args);
    }

    /**
     * Get the arguments list
     *
     * @return list of arguments
     */
    public ArrayList<TimePoint> getArgs(){
        return args;
    }

    /**
     * Set the list of arguments
     *
     * @param args the desired list of arguments (not empty)
     */
    public void setArgs(ArrayList<TimePoint> args){
        if(args.size()==0){
            throw new IllegalArgumentException("There must be at least one argument");
        }
        this.args = args;
    }

    @Override
    public Set<String> getVariables(){
        HashSet<String> vars = new HashSet<>();
        for (TimePoint tp: args){
            vars.addAll(tp.getVariables());
        }
        return vars;
    }

    /**
     * String representation of the operator (e.g. "MIN", "MAX",...)
     *
     * @return operator string
     */
    public String getOperator(){
        return operator;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(operator+"(");
        sb.append(args.get(0).toString());
        for (int i=1; i<args.size(); i++){
            sb.append(", ");
            sb.append(args.get(i).toString());
        }
        sb.append(")");
        return new String(sb);
    }
}
