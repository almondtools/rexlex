package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.pattern.Match;
import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public class ShortestMatchListener implements MatchListener {

	private Match match;

	@Override
	public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
		long end = chars.current();
		String text = chars.slice(start, end );
		if (end < start) {
			start = end;
		}
		match = new Match(start, text , accepted);
		return true;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, long start) {
		return true;
	}

	@Override
	public Match getMatch() {
		Match match = this.match;
		this.match = null;
		return match;
	}
	
}
