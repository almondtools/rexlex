package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.io.CharProvider;
import com.almondtools.rexlex.io.StringCharProvider;

public abstract class Matcher {

	protected CharProvider chars;

	public Matcher(String input) {
		this.chars = new StringCharProvider(input, 0);
	}

	public abstract boolean matches();

}
