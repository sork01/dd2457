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
Executing statment S and initial statement s result in statement s'

Subscript ns means natural semantics.

### Termination

*S terminates from s <=> backE s'eSkle:<S,s>->s'*

### Equivalence between statements

*S_1 ~ S_2 <=> V s_1,s_2 e State. <S_1,s_1> -> s_2 <=> <S_2,s_1> -> s_2*

if b then S_1 else ~ if not b then S_2 else S_1
S_1 ~ S'_1
    ~ S^2_1
    ~
    ~ S_2


(=>) Let s_1 s' e State
Let <if b then S_1 else S_2,s> -> s'
Show <if not b then S_2 else S_1,s> -> s'

Lemma 2.5
while b do S ~ if b then (s; while b do S) else skip
proof in book: page 29++

### Theorem 2.9 The natural semantics of While is deterministic

V SeStm. Vs,s',s''eState.
<S,s> -> s' ^ <S,s> -> s'' => s' = s''

S_ns: Stm -> (State -\ State)

S_ns[[S]](s) = s' if <S,s> -> s' 
              undef otherwise
              
S_ns = S_sos = S_am = S_ds

### Small step operational semantics / structural 
S = statement s = state
initial/intermediate: <S,s>
final : s'
Execution <S_0,s_0> => <S_1,s_1> => ... => s_n

Granularity: Next control point

<skip,s> => s
<x:=a,s> => s[x|-> A[[a]](s)]
<S_1,S_2,s> => <S_2,s'>
<S_1,s> => <S_1',s'>
<S_1,S_2,s> => > <S_1',S_2,s'>





