Relex
========
 Relex, short for (R)gular (E)xpressions and (Lex)ers, provides configurable and scalable Regular Expression Matching, Searching and Lexing.
 
 Scalable Regular Expressions
 ============================
 Common regex packages use nondeterministic automatons (NFA) to capture the regular expression. Nondeterministic automatons are based on
backtracking to match a string. This has several advantages (e.g. group capturing, greedy/lazy/possesive matching, lookahead/lookbehind, backreferences).
The disadvantage is, that such implementations do not perform well - especially when the regular expression contains branches
(e.g. 'a|b') or captures an infinite number of chars (e.g. 'a*b'). The match time is dependent on the nodes in the automaton and the chars to match 
(O(m^2*n), where m = number of automaton nodes, n = number of chars to match)

In many cases regular expressions do not need to provide the upper features, instead they should perform well. Relex compiles a deterministic automaton
(DFA) from a given regular expression. The match time of such an automaton is linear dependent on the number of chars to match (O(n), where n = number 
of chars to match).

Dynamic Lexing with Regular Expressions
=======================================
Consider that you have a number of regular expressions available at runtime and you want to build a lexer from
this set. Typical lexer generators allow you to generate code. They are designed to be generated before you write/link the code using the Lexer.

Relex allows you to generate a lexer at runtime - no code generation, no class loading, no need to depend on code not generated yet. Creating and instantly using
a new Lexer from a variable set of Lexing Idioms with relex is much faster and easier than using a lexer generator. This allows you to generate families
of languages with differences only in the Lexing Idioms, as well as extendable languages (where you could add new lexing idioms at runtime).

Of course this comes with a price of less performance at lexing time and the language itself cannot be specified, but must be programmed. Whenever you have a
nonomodifiable DSL based on nonvariable lexing idioms you should prefer a lexer generator. Otherwise relex lexing could be a fine alternative.

Syntax
======
Groups, lookaheads and lookbehinds do not extend the set of regular languages, so there must be a DFA which supports these features.
However transforming these features to a DFA is tricky and costs performance. At some point we decided not to support these features,
so we cannot support a corresponding syntax. Parentheses are not marking groups and there are no zero-length lookarounds.

Java regexes also support variations of the kleene star: non-greedy-loops, possessive-loops. This version of Relex will not support
these variations. But perhaps a later version will include some of these features.

Having this in mind the syntax of relex regular expressions is like this:


NFA-Expressions (java.util.Pattern) vs. DFA-Expressions
=======================================================
Java regular expressions (java.util.Pattern) are quickly created and optimized. Simple regular expressions are executed quite fast.

Relex regular expressions need a long creation and optimization time. After this initial effort the execution time is no longer
dependent on pattern complexity.

Use Java regular expressions:
- if the expression is short and simple
- if the expression is matched only a few times
- if the expression is often created (e.g. in a loop)

Use Relex regular expressions:
- if the expression is long or complex
- if the expression is matched many times
- if the expression is once created and often applied


Features
========
- Regular Expression Lexing (one can build the lexer programmatically instead of generating lexer code)
- Regular Expression Matching (on one and multiple patterns)
- Regular Expression Search (for one and multiple patterns)

Optimizations
=============
Yet Relex is hardly optimized. Searching is optimized in case that the regular expression is constant or length limited. In such a case string
matching/searching performs better and the searchers switch to string matching (instead of regexp matching).

Other Regex-Compilers
=========================

Some yet existing NFA-Compilers:
- http://jregex.sourceforge.net/ (more features than java.util.pattern)

And DFA-Compilers
- http://www.brics.dk/automaton/ (better optimized for matching, less performant for search, no support for lexing)   

Performance Comparison
======================
We yet tested relex against some of the available Regexp-Packages.

We are working on making the benchmarking program public. The problem here is, that the regexp syntax of all regexp packages is not following a standard.
Benchmarks known to me 
- do compare the same regexp in each benchmarked tool, instead of applying the tool to regexps with the same semantics (but probably other syntax).
- do assert that matches in java.util.Pattern should be also matched with other frameworks (ok), but do not assert that the additional information
(e.g. grouping) is the same (not ok)