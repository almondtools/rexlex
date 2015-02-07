package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.pattern.Match;

public interface MatchListener extends AutomatonMatcherListener{

	Match getMatch();

}
