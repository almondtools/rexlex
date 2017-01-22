package com.almondtools.rexlex.automaton;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.pattern.Match;

import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public class MatchCollector implements AutomatonMatcherListener {

	private List<Match> matches;

	public MatchCollector() {
		this.matches = new ArrayList<Match>();
	}
	
	@Override
	public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
		long end = chars.current();
		String text = chars.slice(start, end);
		if (chars.finished()) {
			matches.add(Match.create(start, text, accepted));
		}
		return false;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, long start) {
		return true;
	}

	public List<Match> getMatches() {
		return matches;
	}
	
	public List<String> getMatchedTexts() {
		List<String> texts = new ArrayList<String>(matches.size());
		for (Match match : matches) {
			texts.add(match.text);
		}
		return texts;
	}

}
