package org.gradoop.gdl.predicates.booleans;

import org.junit.Test;
import org.gradoop.gdl.model.comparables.ElementSelector;
import org.gradoop.gdl.model.comparables.Literal;
import org.gradoop.gdl.model.comparables.PropertySelector;
import org.gradoop.gdl.model.predicates.booleans.Or;
import org.gradoop.gdl.model.predicates.expressions.Comparison;
import org.gradoop.gdl.utils.Comparator;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class OrTest {

  @Test
  public void extractVariablesTest() {
    Comparison a = new Comparison(
            new ElementSelector("a"),
            Comparator.EQ,
            new ElementSelector("b")
    );

    Comparison b = new Comparison(
            new PropertySelector("a","label"),
            Comparator.EQ,
            new Literal("Person")
    );

    Or or = new Or(a,b);

    Set<String> reference = new HashSet<>();
    reference.add("a");
    reference.add("b");

    assertEquals(reference,or.getVariables());
  }
}
