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
package org.s1ck.gdl.comparables.time;

import org.junit.Test;
import org.s1ck.gdl.model.comparables.PropertySelector;
import org.s1ck.gdl.model.comparables.time.MaxTimePoint;
import org.s1ck.gdl.model.comparables.time.MinTimePoint;
import org.s1ck.gdl.model.comparables.time.TimeLiteral;
import org.s1ck.gdl.model.comparables.time.TimeSelector;
import org.s1ck.gdl.model.predicates.expressions.Comparison;
import org.s1ck.gdl.utils.Comparator;

import static org.junit.Assert.*;
import static org.s1ck.gdl.utils.Comparator.GT;
import static org.s1ck.gdl.utils.Comparator.LT;

public class ComparisonTest {
    @Test
    public void testIsTemporal(){
        TimeSelector ts = new TimeSelector("var", TimeSelector.TimeField.VAL_TO);
        TimeLiteral tl = new TimeLiteral("1970-01-01T01:01:01");
        Comparison timeComp = new Comparison(ts, Comparator.NEQ, tl);
        assertTrue(timeComp.isTemporal());

        PropertySelector ps1 = new PropertySelector("p", "prop");
        PropertySelector ps2 = new PropertySelector("q", "prop");
        Comparison propertyComp = new Comparison(ps1, LT, ps2);
        assertFalse(propertyComp.isTemporal());
    }

    @Test
    public void testSwitchSides(){
        TimeSelector ts = new TimeSelector("var", TimeSelector.TimeField.VAL_TO);
        TimeLiteral tl = new TimeLiteral("1970-01-01T01:01:01");

        Comparison timeComp = new Comparison(ts, Comparator.EQ, tl);
        assertEquals(timeComp.switchSides(), new Comparison(tl, Comparator.EQ, ts));

        timeComp = new Comparison(ts, Comparator.NEQ, tl);
        assertEquals(timeComp.switchSides(), new Comparison(tl, Comparator.NEQ, ts));

        timeComp = new Comparison(ts, LT, tl);
        assertEquals(timeComp.switchSides(), new Comparison(tl, GT, ts));

        timeComp = new Comparison(ts, Comparator.LTE, tl);
        assertEquals(timeComp.switchSides(), new Comparison(tl, Comparator.GTE, ts));

        timeComp = new Comparison(ts, Comparator.GTE, tl);
        assertEquals(timeComp.switchSides(), new Comparison(tl, Comparator.LTE, ts));

        timeComp = new Comparison(ts, GT, tl);
        assertEquals(timeComp.switchSides(), new Comparison(tl, LT, ts));
    }

    @Test
    public void testGlobal(){
        TimeSelector ts = new TimeSelector("var", TimeSelector.TimeField.VAL_TO);
        TimeLiteral tl = new TimeLiteral("1970-01-01T01:01:01");
        TimeSelector global = new TimeSelector(TimeSelector.GLOBAL_SELECTOR,
                TimeSelector.TimeField.TX_FROM);
        assertFalse(new Comparison(ts, Comparator.LTE, tl).isGlobal());
        assertTrue(new Comparison(ts, Comparator.GTE, global).isGlobal());
        assertFalse(new Comparison(new MinTimePoint(ts, tl), LT, new MaxTimePoint(ts,tl)).isGlobal());
        assertTrue(new Comparison(new MinTimePoint(ts, global), GT, new MaxTimePoint(ts, tl)).isGlobal());
    }

}
