package com.almondtools.rexlex.pattern;

public class PatternCompileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PatternCompileException(String pattern, int pos, String expectedChars) {
		super("error compiling pattern <" + pattern + "> at position " + pos + ", found: " + (pattern.length() <= pos ? "eof" : pattern.charAt(pos)) + ", expecting on of: " + expectedChars);
	}

}
