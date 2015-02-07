package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.automaton.GenericAutomaton;

public interface MatcherBuilder extends PatternOption {

	MatcherBuilder initWith(GenericAutomaton nfa);

	Finder buildFinder(String input);

	Matcher buildMatcher(String input);

}
