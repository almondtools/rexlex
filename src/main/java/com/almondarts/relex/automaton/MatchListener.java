package com.almondarts.relex.automaton;

import com.almondarts.relex.pattern.Match;

public interface MatchListener extends AutomatonMatcherListener{

	Match getMatch();

}
