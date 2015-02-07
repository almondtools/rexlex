package com.almondtools.rexlex.automaton;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.io.CharProvider;
import com.almondtools.rexlex.pattern.Match;

public class PrefixCollector implements AutomatonMatcherListener {

	private List<Match> matches;

	public PrefixCollector() {
		this.matches = new ArrayList<Match>();
	}
	
	@Override
	public boolean reportMatch(CharProvider chars, int start, TokenType accepted) {
		int end = chars.current();
		String text = chars.slice(start, end);
		matches.add(new Match(start, text, accepted));
		return false;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, int start) {
		return true;
	}

	public List<Match> getMatches() {
		return matches;
	}
	
	public List<String> getMatchedTexts() {
		List<String> texts = new ArrayList<String>(matches.size());
		for (Match match : matches) {
			texts.add(match.text());
		}
		return texts;
	}

}
