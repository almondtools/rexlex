package com.almondtools.relex.automaton;

import com.almondtools.relex.pattern.Match;

public interface MatchListener extends AutomatonMatcherListener{

	Match getMatch();

}
