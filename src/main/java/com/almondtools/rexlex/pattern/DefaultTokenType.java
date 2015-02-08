package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.TokenType;

public enum DefaultTokenType implements TokenType {
	IGNORE(), // state is acceptable, but match should be ignored
	ACCEPT(), // state is acceptable, and match may be returned
	ERROR(true) // state is not acceptable and indicates an error
	;

	private boolean error;

	private DefaultTokenType() {
		this(false);
	}

	private DefaultTokenType(boolean error) {
		this.error = error;
	}

	@Override
	public boolean error() {
		return error;
	}

	@Override
	public boolean accept() {
		return !error;
	}

	public static DefaultTokenType merge(DefaultTokenType type1, DefaultTokenType type2) {
		if (type1 == null) {
			return type2;
		} else if (type2 == null) {
			return type1;
		} else if (type1.compareTo(type2) > 0) {
			return type1;
		} else {
			return type2;
		}
	}

}