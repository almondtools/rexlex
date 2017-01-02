package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.pattern.Match;
import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public class ShortestMatchListener implements MatchListener {

	private final Match match;
	
	public ShortestMatchListener() {
		this.match = new Match();
	}

	@Override
	public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
		long end = chars.current();
		String text = chars.slice(start, end );
		if (end < start) {
			start = end;
		}
		match.init(start, text , accepted);
		return true;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, long start) {
		return true;
	}

	@Override
	public Match getMatch() {
		return match.consume();
	}
	
}
