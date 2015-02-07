package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.io.CharProvider;
import com.almondtools.rexlex.pattern.Match;

public class ShortestMatchListener implements MatchListener {

	private Match match;

	@Override
	public boolean reportMatch(CharProvider chars, int start, TokenType accepted) {
		int end = chars.current();
		String text = chars.slice(start, end );
		if (end < start) {
			start = end;
		}
		match = new Match(start, text , accepted);
		return true;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, int start) {
		return true;
	}

	@Override
	public Match getMatch() {
		Match match = this.match;
		this.match = null;
		return match;
	}
	
}
