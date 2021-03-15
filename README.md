[![Build Status](https://github.com/dbs-leipzig/gdl/workflows/Java%20CI/badge.svg)](https://github.com/dbs-leipzig/gdl/actions?workflow=Java+CI)

## Temporal-GDL - Temporal Graph Definition Language

> Now with bitemporal modeling of vertices and edges.

Inspired by the popular graph query language [Cypher](http://neo4j.com/docs/stable/cypher-query-lang.html),
which is implemented in [Neo4j](http://neo4j.com/), I started developing an [ANTLR](http://www.antlr.org/)
grammar to define property graphs. I added the concept of _logical graphs_ into the language to support multiple, 
possible overlapping property graphs in one database. 

GDL is used for unit testing and graph definition in [Gradoop](https://github.com/dbs-leipzig/gradoop), a framework for 
distributed temporal graph analytics.

The project contains the grammar and a listener implementation which transforms GDL scripts into
property graph model elements (i.e. graphs, vertices and edges).

## Data model

The data model adapts concepts from the _Temporal Property Graph Model (TPGM)_ and contains three 
elements: graphs, vertices and edges. Any element has an optional
label and can have multiple attributes in the form of key-value pairs. Vertices and edges may 
be contained in an arbitrary number of graphs including zero graphs. Edges are binary and directed.
Each graph element is associated with 2 intervals, transaction- (tx) and valid-time (val) interval,
to achieve a bitemporal modeling.
Each interval is defined by its start (from) and end (to) timestamp. Thus, vertices and edges have
four additional timestamps (tx_from, tx_to, val_from, val_to).

## Language Examples

Define a vertex:

```
()
```

Define a vertex and assign it to variable `alice`:

```
(alice)
```

Define a vertex with label `User`:

```
(:User)
```

Define a vertex with label `User`, assign it to variable `alice` and give it some properties:

```
(alice:User {name : "Alice", age : 23})
```

Property values can also be null:

```
(alice:User {name : "Alice", age : 23, city : NULL})
```

Numeric property values can have specific data types:

```
(alice:User {name : "Alice", age : 23L, height : 1.82f, weight : 42.7d})
```

Property values can also be ordered lists:

```
(alice:User {name : "Alice", age : 23, codes: ["Java", "Rust", "Scala"]})
```

Define an outgoing edge:

```
(alice)-->()
```

Define an incoming edge:

```
(alice)<--()
```

Define an edge with label `knows`, assign it to variable `e1` and give it some properties:

```
(alice)-[e1:knows {since : 2014}]->(bob)
```

Define multiple outgoing edges from the same source vertex (i.e. `alice`):

```
(alice)-[e1:knows {since : 2014}]->(bob)
(alice)-[e2:knows {since : 2013}]->(eve)
```

Define paths (four vertices and three edges are created):

```
()-->()<--()-->()
```

Define a graph with one vertex (graphs can be empty):

```
[()]
```

Define a graph and assign it to variable `g`:

```
g[()]
```

Define a graph with label `Community`:

```
:Community[()]
```

Define a graph with label `Community`, assign it to variable `g` and give it some properties:

```
g:Community {title : "Graphs", memberCount : 42}[()]
```

Define mixed path and graph statements (elements in the paths don't belong to a specific graph):

```
()-->()<--()-->()
[()]
```

Define a fragmented graph with variable reuse:

```
g[(a)-->()]
g[(a)-->(b)]
g[(b)-->(c)]
```

Define three graphs with overlapping vertex sets (e.g. `alice` is in `g1` and `g2`):

```
g1:Community {title : "Graphs", memberCount : 23}[
    (alice:User)
    (bob:User)
    (eve:User)
]
g2:Community {title : "Databases", memberCount : 42}[
    (alice)
]
g2:Community {title : "Hadoop", memberCount : 31}[
    (bob)
    (eve)
]
```

Define three graphs with overlapping vertex and edge sets (`e` is in `g1` and `g2`):

```
g1:Community {title : "Graphs", memberCount : 23}[
    (alice:User)-[:knows]->(bob:User),
    (bob)-[e:knows]->(eve:User),
    (eve)
]
g2:Community {title : "Databases", memberCount : 42}[
    (alice)
]
g2:Community {title : "Hadoop", memberCount : 31}[
    (bob)-[e]->(eve)
]
```

## Query Expressions

As part of his thesis, [Max](https://github.com/DarthMax) extended the grammar to support `MATCH .. WHERE ..`
statements analogous to Cypher. Besides defining a graph it is now also possible to formulate a query including 
patterns, variable length paths and predicates:

```
MATCH (alice:Person)-[:knows]->(bob:Person)-[:knows*2..2]->(eve:Person)
WHERE (alice.name = "Alice" AND bob.name = "Bob") 
OR (alice.age > bob.age)
OR (alice.age > eve.age)
```

**Note** that queries always start with the `MATCH` keyword optionally followed by one or more
`WHERE` clauses. 

### Bitemporal extensions for querying a TPGM graph

Several GDL extensions were added to support TPGM graphs as defined by 
[Rost et al.](https://dbs.uni-leipzig.de/de/publication/title/temporal_graph_analysis_using_gradoop). 
Here, valid and transaction intervals are defined for every graph element.

#### Timestamps
###### From/To Selectors
Every graph element (vertex/edge) is associated with 2 intervals, transaction (tx) and valid (val) interval.
Each interval is defined by its start (from) and end (to) timestamp.

For a graph element with variable name `a`, these four timestamps can be accessed in a property selector-like syntax:
* `a.tx_from`
* `a.tx_to`
* `a.val_from`
* `a.val_to`

Furthermore, the "global" transaction and valid interval are defined as the intersections of all elements' transaction/valid intervals. Their from and to stamps can be accessed by omitting the variable name:
* `tx_from`
* `tx_to`
* `val_from`
* `val_to`

###### Literals
Timestamp literals can be created in the following formats:
* `Timestamp(YYYY-MM-DDTHH:MM:SS)` where `T` stands for the literal `T`
* `Timestamp(YYYY-MM-DD)` (time is set to 00:00:00)
* `Timestamp(Now)` (current timestamp)

###### Min/Max
Two types of "complex" timestamps can be created from a set of simple ones (selectors and literals):
* `MIN(t1,t2,...,tn)`
* `MAX(t1,t2,...,tn)`

Note that they can not be nested further, only selectors and literals are valid arguments for `MIN` and `MAX`.

#### Relations between Timestamps
###### Simple Comparisons
Timestamps can be compared using the usual comparators `<`, `<=`, `=`, `!=`, `>=`, and `>`
###### Syntactic Sugar
* `t1.before(t2)`: equal to `t1 < t2`
* `t1.after(t2)`: equal to `t1 > t2`

#### Intervals
###### Selectors for Transaction/Valid Intervals
For a graph element with variable name `a`, the two intervals can be accessed in a property selector-like syntax:
* `a.tx`
* `a.val`

Furthermore, the _global_ transaction and valid interval are defined as the intersections of all elements' transaction/valid intervals. These intervals can be accessed by omitting the variable name:
* `tx`
* `val`

###### Interval Literals
Custom intervals can be created from two timestamps denoting start and end of the interval:
* `Interval(t1,t2)` where `t1` and `t2` are timestamps

When such an interval is created, a constraint `t1 <= t2` is implicitly added.

###### Merge
Two intervals can be merged, i.e. intersected. This yields a new interval. The merge operation is only defined, if the two intervals overlap.
* `i1.merge(i2)` where `i1` and `i2` are intervals. Implicitly, constraints to ensure that `i1` and `i2` overlap are added.

###### Join
Two intervals can be merged, i.e. united. This yields a new interval. The join operation is only defined, if the two intervals overlap.
* `i1.join(i2)` where `i1` and `i2` are intervals. Implicitly, constraints to ensure that `i1` and `i2` overlap are added.

Note that `merge` and `join` operations can not be nested any further, i.e. something like `i1.merge(i2).join(a.tx)` is not possible.

#### Relations between Intervals
Binary relations between intervals can be stated. They are inspired by the interval relations [defined in SQL:2011](https://dl.acm.org/doi/abs/10.1145/2380776.2380786).
* `i1.overlaps(i2)`
* `i1.contains(i2)`
* `i1.precedes(i2)`
* `i1.succeeds(i2)`
* `i1.immediatelyPrecedes(i2)`
* `i1.immediatelySucceeds(i2)`
* `i1.equals(i2)`

These relations are syntactic sugar, as they can all be expressed as terms using only from and to selectors.

#### Relations between Intervals and Timestamps
Additionally, relations between an interval and one or two timestamps are possible. Here, `i` is an interval, `t`, `t1` and `t2` are timestamps:
* `i.fromTo(t1,t2)`
* `i.between(t1,t2)`
* `t.precedes(i)`
* `t.succeeds(i)`
* `i.contains(t)`

#### asOf
`asOf` is a special constraint that refers to the transaction interval of a graph element.
* `a.asOf(t)` is true iff `a.tx_from <= t` and `a.tx_to >= t`, where `a` refers to a graph element

#### Durations
###### Interval Durations
Durations of intervals can be referred to by simply referring to the interval, e.g. in the context of a duration predicate (see below) `a.tx` would denote the length of `a`'s transaction time.

###### Constant Durations
Duration constants can be created by the following keywords (`number` is an integer literal):
* `Millis(number)`
* `Seconds(number)`
* `Minutes(number)`
* `Hours(number)`
* `Days(number)`

#### Relations between Durations
Durations as defined above can be compared:
* `d1.longerThan(d2)`
* `d1.shorterThan(d2)`
* `d1.lengthAtLeast(d2)`
* `d1.lengthAtMost(d2)`

### Implementation Details
New temporal `ComparableExpression`s are added.

The processing of all the temporal constraints described above is encapsulated in a `GDLLoaderTemporal.java` that is used by the actual `GDLLoader`.

## Usage examples

Add dependency to your maven project:

```
<dependency>
    <groupId>com.github.s1ck</groupId>
    <artifactId>gdl</artifactId>
    <version>0.4.0-SNAPSHOT</version>
</dependency>
```

Create a database from a GDL string:

```java
GDLHandler handler = new GDLHandler.Builder().buildFromString("g[(alice)-[e1:knows {since : 2014}]->(bob)]");

for (Vertex v : handler.getVertices()) {
    // do something
}

// access elements by variable
Graph g = handler.getGraphCache().get("g");
Vertex alice = handler.getVertexCache().get("alice");
Edge e = handler.getEdgeCache().get("e1");
```

Read predicates from a Cypher query:

```java
GDLHandler handler = new GDLHandler.Builder().buildFromString("MATCH (a:Person)-[e:knows]->(b:Person) WHERE a.age > b.age");

// prints (((a.age > b.age AND a.__label__ = Person) AND b.__label__ = Person) AND e.__label__ = knows)
handler.getPredicates().ifPresent(System.out::println);
```

Create a database from an `InputStream` or an input file:

```java
GDLHandler handler1 = new GDLHandler.Builder().buildFromStream(stream);
GDLHandler handler2 = new GDLHandler.Builder().buildFromFile(fileName);
```

Append data to a given handler:

```java
GDLHandler handler = new GDLHandler.Builder().buildFromString("g[(alice)-[e1:knows {since : 2014}]->(bob)]");

handler.append("g[(alice)-[:knows]->(eve)]");
```

## License

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
