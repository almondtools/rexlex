package com.almondarts.relex.pattern;

import com.almondarts.relex.io.CharProvider;
import com.almondarts.relex.io.StringCharProvider;

public abstract class Matcher {

	protected CharProvider chars;

	public Matcher(String input) {
		this.chars = new StringCharProvider(input, 0);
	}

	public abstract boolean matches();

}
