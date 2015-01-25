package com.almondarts.relex.pattern;

import com.almondarts.relex.automaton.GenericAutomaton;

public interface MatcherBuilder extends PatternOption {

	MatcherBuilder initWith(GenericAutomaton nfa);

	Finder buildFinder(String input);

	Matcher buildMatcher(String input);

}
