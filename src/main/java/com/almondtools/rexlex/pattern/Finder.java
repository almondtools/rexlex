package com.almondtools.rexlex.pattern;

import net.amygdalum.util.io.StringCharProvider;

public abstract class Finder {

	protected StringCharProvider chars;
	protected final Match match;

	public Finder(String input) {
		this.chars = new StringCharProvider(input, 0);
		this.match = new Match();
	}

	public abstract boolean find();

	public long start() {
		return match.start;
	}

	public long end() {
		return match.end;
	}

	public String group() {
		return match.text;
	}

}
