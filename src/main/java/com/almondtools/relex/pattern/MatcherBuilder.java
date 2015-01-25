package com.almondtools.relex.pattern;

import com.almondtools.relex.automaton.GenericAutomaton;

public interface MatcherBuilder extends PatternOption {

	MatcherBuilder initWith(GenericAutomaton nfa);

	Finder buildFinder(String input);

	Matcher buildMatcher(String input);

}
