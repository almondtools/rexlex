package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.pattern.Match;
import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public class LongestMatchListener implements MatchListener {

	private final Match match;
	private final Match nextMatch;
	
	public LongestMatchListener() {
		this.match = new Match();
		this.nextMatch = new Match();
	}

	@Override
	public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
		if (nextMatch != null) {
			match.moveFrom(nextMatch);
		}
		long end = chars.current();
		String text = chars.slice(start, end );
		if (end < start) {
			start = end;
		}
		if (!match.isMatch() || (match.start == start && match.text.length() < text.length())) {
			match.init(start, text , accepted);
			return false;
		} else {
			nextMatch.init(start, text , accepted);
			return true;
		}
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
