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

import java.util.List;
import java.util.Optional;

/**
 * Represents a MAX(p1,...,pn) term, where p1...pn are TimePoints
 */
public class MinTimePoint extends TimeTerm {

    /**
     * Creates a MIN(args[0],...,args[args.length-1]) term
     * @param args the arguments for the MIN term (TimePoints)
     */
    public MinTimePoint(TimePoint...args){
        super(args);
        operator = "MIN";
    }

    @Override
    public Optional<Long> evaluate(){
        long mn = Long.MAX_VALUE;
        for (TimePoint p:args){
            Optional<Long> eval = p.evaluate();
            if (!eval.isPresent()){
                return Optional.empty();
            }
            if (eval.get() < mn){
                mn = eval.get();
            }
        }
        return Optional.of(mn);
    }

    @Override
    public String getVariable() {
        return null;
    }

    @Override
    public boolean containsSelectorType(TimeSelector.TimeField type){
        for(TimePoint p: args){
            if(p.containsSelectorType(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinTimePoint that = (MinTimePoint) o;

        if(args.size()!=that.args.size()){
            return false;
        }

        for(TimePoint arg: args){
            boolean foundEq = false;
            for(TimePoint arg2: that.args){
                if(arg.equals(arg2)){
                    foundEq = true;
                    break;
                }
            }
            if(!foundEq){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isGlobal(){
        for(TimePoint arg: args){
            if(arg.isGlobal()){
                return true;
            }
        }
        return false;
    }

    @Override
    public ComparableExpression replaceGlobalByLocal(List<String> variables) {
        TimePoint[] newArgs = new TimePoint[args.size()];
        for(int i=0; i<args.size(); i++){
            newArgs[i] = (TimePoint) args.get(i).replaceGlobalByLocal(variables);
        }
        return new MinTimePoint(newArgs);
    }


}
