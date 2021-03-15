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
package org.gradoop.gdl.comparables.time;

import org.junit.Test;
import org.gradoop.gdl.model.comparables.time.TimeConstant;

import static org.junit.Assert.assertEquals;

public class TimeConstantTest {

    @Test
    public void constantTest(){
        TimeConstant c = new TimeConstant(1000);
        assertEquals(c.getMillis(), 1000);

        int days = 23;
        int hours = 11;
        int minutes = 7;
        int seconds = 42;
        int millis = 1;
        TimeConstant c2 = new TimeConstant(days, hours, minutes, seconds, millis);

        int expected_millis = millis + 1000*seconds + (1000*60)*minutes + (1000*60*60)*hours +
                (1000*60*60*24)*days;
        assertEquals(expected_millis, c2.getMillis());

        assertEquals(c2.getMillis(), (long)c2.evaluate().get());
    }
}
