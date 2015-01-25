package com.almondtools.relex.pattern;

import com.almondtools.relex.io.CharProvider;
import com.almondtools.relex.io.StringCharProvider;

public abstract class Matcher {

	protected CharProvider chars;

	public Matcher(String input) {
		this.chars = new StringCharProvider(input, 0);
	}

	public abstract boolean matches();

}
