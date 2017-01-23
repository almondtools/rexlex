package com.almondtools.rexlex.pattern;

import net.amygdalum.util.io.CharProvider;
import net.amygdalum.util.io.StringCharProvider;

public abstract class Matcher {

	protected CharProvider chars;

	public Matcher(String input) {
		this.chars = new StringCharProvider(input, 0);
	}

	public abstract boolean matches();

}
