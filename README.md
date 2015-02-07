Rexlex
======
Rexlex, short for (R)egular (Ex)pressions and (Lex)ers, provides configurable and scalable Regular Expression Matching, Searching and Lexing.

Starting with Rexlex Matching
=============================

Creating an Automaton from a Pattern
------------------------------------
```Java
	Pattern pattern = Pattern.compile("\\d{2}-\\d{2}-\\d{4}", new OptimizedMatcherBuilder());
```

Preparing a Regexp-Finder
-------------------------
```Java
	Finder matcher = pattern.finder("born on  04-07-1946");
```

Iterating Finder-Matches
-----------------------
```Java
	while (matcher.find()) {
		System.out.println("found text = " + matcher.match.text());
		System.out.println("at = " + matcher.match.start());
		System.out.println("to = " + matcher.match.end());
	}
```

Collecting all Finder-Matches
-----------------------------
```Java
	for (Match match : matcher.findAll()) {
		System.out.println("found text = " + match.text());
		System.out.println("at = " + match.start());
		System.out.println("to = " + match.end());
	}
```

Checking on Regexp-Full-Matches
-------------------------------
```Java
	Pattern pattern = Pattern.compile("\\d{2}-\\d{2}-\\d{4}", new OptimizedMatcherBuilder());
	Matcher matcher = builder.matcher(04-07-1946");
	System.out.prinltn("matches: " + matcher.matches());
```

Which MatcherBuilder to Choose
------------------------------
The method Pattern.compile accepts arguments implementing the interface MatcherBuilder. Calling it without any MatcherBuilder it uses the DefaultMatcherBuilder.
What are the differences?

DefaultMatcherBuilder: 
- uses one dfa for matching and searching
- match is fast (O(n))
- search is naive (O(n^2) 
- no configuration options

SearchMatcherBuilder
- uses two dfa, one for matching, one for searching 
- match dfa is the same as in DefaultMatcherBuilder (O(n))
- search dfa uses two passes (search for a pattern match, search for the pattern boundaries), O(n)
- both dfa produce some overhead
- the second search pass produces some overhead
- no configuration options

OptimizedMatcherBuilder
- uses two dfa, one for matching, one for searching
- uses a string search engine for simple patterns
- match dfa is the same as in DefaultMatcherBuilder (O(n))
- search dfa is the same as in SearchMatcherBuilder (O(n))
- if the pattern is limited (i.e. constant or the number of possible words is less than 4000) it switches to string search/match
- the overhead for producing the two dfa is eliminated in case of string search/match
- string search/match is much faster than regex search
- confguration of string search algorithms for constant patterns: Knuth-Morris-Pratt, Horspool
- configuration of string search algorithms for limited word patterns: AhoCorasick, SetHorspool, SetBackwardOracleMatching, WuManber
- there is overhead for an initial dfa
- if you already know, that the pattern is constant or limited, than you should use string search directly, rather than this pattern search

 
Starting with Rexlex Lexing
===========================

Define the token types
----------------------

Define the tokens
-----------------
First the lexer tokens which are produced when finding a certain lexing idiom must be defined. To do this you must implement the class Token:
  
```Java
public class MyToken implements Token {

	private String literal;
	private TokenType type;

	public TestToken(String literal, TokenType type) {
		this.literal = literal;
		this.type = type;
	}
	
	@Override
	public String getLiteral() {
		return literal;
	}
	
	@Override
	public TokenType getType() {
		return type;
	}
	
	...
	
}
```

This default implementation should be sufficient in most cases, but be free to extend this type with methods you later need.

Optional: Extend the Token Types
================================
Rexlex has three default token types (in the enum DefaultTokenType). You may want to extend the token types. TokenTypes could be enums or classes. Note that in
latter case you should correctly implement the methods hashCode and equals.

```Java
public enum MyTokenType implements TokenType {
	A,B,REMAINDER;

	@Override
	public boolean error() {
		return false;
	}
	
	@Override
	public boolean accept() {
		return true;
	}
}
```

Create a Token Factory
======================

Then write the token factory.

```Java
public class MyTokenFactory implements TokenFactory<TestToken>{

	@Override
	public MyToken createToken(String literal, TokenType type) {
		return new MyToken(literal, type);
	}
}
```

Build the lexer
===============
Having the tokens and the token factory you can build a lexer. In the following code we assume that you have defined additional token types A, B and REMAINDER:

```Java
	Map<String, TokenType> patternToTypes = new HashMap();
	patternToTypes.put("a", A); //any match for 'a' will return token type A
	patternToTypes.put("b", B); //any match for 'b' will return token type B

	DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, REMAINDER, factory); // nonmatched strings will return REMAINDER
	Iterator<MyToken> tokens = lexer.lex("abc");
	MyToken a = tokens.next(); // == new MyToken("a", A)
	MyToken b = tokens.next(); // == new MyToken("b", B)
	MyToken c = tokens.next(); // == new MyToken("c", REMAINDER)
```

Scalable Regular Expressions
============================
Common regex packages use nondeterministic automatons (NFA) to capture the regular expression. Nondeterministic automatons are based on
backtracking to match a string. This has several advantages (e.g. group capturing, greedy/lazy/possesive matching, lookahead/lookbehind, backreferences).
The disadvantage is, that such implementations do not perform well - especially when the regular expression contains branches
(e.g. 'a|b') or captures an infinite number of chars (e.g. 'a*b'). The match time is dependent on the nodes in the automaton and the chars to match 
(O(m^2*n), where m = number of automaton nodes, n = number of chars to match)

In many cases regular expressions do not need to provide the upper features, instead they should perform well. Rexlex compiles a deterministic automaton
(DFA) from a given regular expression. The match time of such an automaton is linear dependent on the number of chars to match (O(n), where n = number 
of chars to match).

Dynamic Lexing with Regular Expressions
=======================================
Consider that you have a number of regular expressions available at runtime and you want to build a lexer from
this set. Typical lexer generators allow you to generate code. They are designed to be generated before you write/link the code using the Lexer.

Rexlex allows you to generate a lexer at runtime - no code generation, no class loading, no need to depend on code not generated yet. Creating and instantly using
a new Lexer from a variable set of Lexing Idioms with rexlex is much faster and easier than using a lexer generator. This allows you to generate families
of languages with differences only in the Lexing Idioms, as well as extendable languages (where you could add new lexing idioms at runtime).

Of course this comes with a price of less performance at lexing time and the language itself cannot be specified, but must be programmed. Whenever you have a
nonomodifiable DSL based on nonvariable lexing idioms you should prefer a lexer generator. Otherwise rexlex lexing could be a fine alternative.

Syntax
======
Groups, lookaheads and lookbehinds do not extend the set of regular languages, so there must be a DFA which supports these features.
However transforming these features to a DFA is tricky and costs performance. At some point we decided not to support these features,
so we cannot support a corresponding syntax. Parentheses are not marking groups and there are no zero-length lookarounds.

Java regexes also support variations of the kleene star: non-greedy-loops, possessive-loops. This version of Rexlex will not support
these variations. But perhaps a later version will include some of these features.

Having this in mind the syntax of rexlex regular expressions is like this:

| Syntax                  | Matches                                                              |
| ----------------------- |----------------------------------------------------------------------|
| Single Characters       |                                                                      |
| x                       | The character x, unless there exist special rules for this character |
| .                       | any character (newlines only in DOTALL-mode)                         |
| \\\\                    | backslash character                                                  |
| \n                      | newline character                                                    |
| \t                      | tab character                                                        |
| \r                      | carriage return character                                            |
| \f                      | form feed character                                                  |
| \a                      | alert/bell character                                                 |
| \e                      | escape character                                                     |
| *\uhhhh*                | *unicode character, not yet supported*                               |
| Character classes       |                                                                      |
| [...]                   | any of the contained characters                                      |
| [^...]                  | none of the contained characters                                     |
| [a-z]                   | char range (all chars from a to z)                                   |
| [a-zA-Z]                | char range, union of multiple ranges                                 |
| \s                      | white space                                                          |
| \S                      | non white space                                                      |
| \w                      | word characters                                                      |
| \W                      | non word charachters                                                 |
| \d                      | digits                                                               |
| \D                      | non digits                                                           |
| *\p{name}*              | *posix character class, not yet supported*                           |
| Sequences, alternatives |                                                                      |
| xy                      | match x followed by y                                                |
| x|y                     | match x or y                                                         |
| (x)                     | match inner expression x (grouping is not supported)                 |
| Repetitions             |                                                                      |
| x?                      | match x or nothing                                                   |
| x*                      | match a sequence of x's or nothing                                   |
| x+                      | match a sequence of x's (minimum one)                                |
| x{2}                    | match a sequence of 2 x's                                            |
| x{2,4}                  | match a 2 to 4 x's                                                   |
| x{,4}                   | match a up to 4 x's                                                  |
| x{2,}                   | match a minimum of 2 x's                                             |
|                         |                                                                      |
| *Groups*                | *not supported*                                                      |
| *References*            | *not supported*                                                      |
| *Anchors*               | *not supported*                                                      |
| *Flags*                 | *not supported*                                                      |


NFA-Expressions (java.util.Pattern) vs. DFA-Expressions
=======================================================
Java regular expressions (java.util.Pattern) are quickly created and optimized. Simple regular expressions are executed quite fast.

Rexlex regular expressions need a long creation and optimization time. After this initial effort the execution time is no longer
dependent on pattern complexity.

Use Java regular expressions:
- if the expression is short and simple
- if the expression is matched only a few times
- if the expression is often created (e.g. in a loop)

Use Rexlex regular expressions:
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
Yet Rexlex is hardly optimized. Searching is optimized in case that the regular expression is constant or length limited. In such a case string
matching/searching performs better and the searchers switch to string matching (instead of regexp matching).

Other Regex-Compilers
=========================
NFA-Compilers:
- http://jregex.sourceforge.net/

DFA-Compilers
- http://www.brics.dk/automaton/

Performance Comparison
======================
A performance benchmark for regex packages can be found at https://github.com/almondtools/regexbench.

This benchmark does not only check the performance but also the correctness of the results:
- each benchmark fails if the expected number matches is not found
- DFA packages cannot compute the same groups as NFA packages - accepted difference
- Non-Posix-NFA packages (as jregex and java.util.regex) do not always detect the longest leftmost match - accepted difference
