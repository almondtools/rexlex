package com.almondtools.relex.automaton;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.io.CharProvider;
import com.almondtools.relex.pattern.Match;

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
