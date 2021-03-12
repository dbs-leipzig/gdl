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
import org.s1ck.gdl.model.comparables.time.Duration;
import org.s1ck.gdl.model.comparables.time.TimeLiteral;
import org.s1ck.gdl.model.comparables.time.TimeSelector;

import static org.junit.Assert.*;
import static org.s1ck.gdl.model.comparables.time.TimeSelector.TimeField.TX_FROM;
import static org.s1ck.gdl.model.comparables.time.TimeSelector.TimeField.TX_TO;

public class DurationTest {

    @Test
    public void simpleDurationTest(){
        TimeLiteral l1 = new TimeLiteral("1970-01-01T00:00:00");
        TimeLiteral l2 = new TimeLiteral("1970-01-01T00:00:01");
        Duration duration = new Duration(l1, l2);
        assertEquals((long) duration.evaluate().get(), 1000L);
    }

    @Test
    public void selectorDurationTest(){
        TimeLiteral l1 = new TimeLiteral("1979-04-11T00:12:12");
        TimeSelector s1 = new TimeSelector("a", TX_TO);
        Duration duration = new Duration(l1, s1);
        assertFalse(duration.evaluate().isPresent());
        assertEquals(duration.getVariables().size(), 1);
        assertEquals(duration.getVariables().toArray()[0], "a");
        assertFalse(duration.isGlobal());

        TimeSelector global = new TimeSelector(TimeSelector.GLOBAL_SELECTOR, TX_FROM);
        duration = new Duration(global, s1);
        assertTrue(duration.isGlobal());
    }
}
