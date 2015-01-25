package com.almondtools.relex.pattern;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.relex.io.StringCharProvider;

public abstract class Finder {

	protected StringCharProvider chars;
	protected Match match;

	public Finder(String input) {
		this.chars = new StringCharProvider(input, 0);
	}

	public abstract boolean find();

	public Match findNext() {
		boolean found = find();
		if (found) {
			return match;
		} else {
			return null;
		}
		
	}
	public List<Match> findAll() {
		List<Match> matches = new ArrayList<Match>();
		while (find()) {
			matches.add(match);
		}
		return matches;
	}

	public int start() {
		if (match == null) {
			return -1;
		} else {
			return match.start();
		}
	}

	public int end() {
		if (match == null) {
			return -1;
		} else {
			return match.end();
		}
	}

	public String group() {
		if (match == null) {
			return null;
		} else {
			return match.text();
		}
	}

}
