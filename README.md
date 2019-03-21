# DD2457 Programsemantik och Programanalys

## Lecture 1: Introduction

Semantics means "meaning"


Linguistics: Syntax, semantics, pragmatics


Computer Science: 
*programming languages (java, scheme)*
*database query languages (sql)*


Formal semantics
*meaning = behaviour*
as a mathematical object!

We're interested in the behaviour of the program

*Function: what is the computed object?*

*Time: how many computation steps?*

*Space: how much memory is needed?*

We're not talking about time and space, we're interested in the interaction behaviour

*what sequences of interactions, on-going behaviour, reactive systems*

Implementation, transformation and correctness

### Operational semantics: 
program behaviour: states and transitions
granularity: big-step, small-step

Configurations: "state of computation" initial and final configurations

Transitions between configurations relation, defined by rules

Computations: sequences of transitions from initial to final configurations

1. Big-step operational semantics: transitions relate initial and final configs

2. Small-step operational semantics: transitions relate internal configs

3. Abstract machines: execution strategy embedded into rules

### Denotational semantics:
programs as "state transformers"

Relates initial and final states

Meaning of program is a mapping! from initial to final configurations "state transformer"

Meaning of constructs: given by defining equations

Tricky for loops: recursive equations Fixed-point Theory, Domain Theory

### Axiomatic semantics:
programs as "predicate transformers"
properties of states as logic assertions

Relates properties of initial and final states
programs are "predicate transformers"
properties of states: state assertions

Meaning of programs: Hoare triples

Meaning of constructs: given by rules ("axiomatic")

## Lecture 3

Natural Semantics

*<S,s> -> s'*
