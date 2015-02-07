package com.almondtools.rexlex.tokens;

import com.almondtools.rexlex.TokenType;

public enum Info implements TokenType {
	INFO;

	@Override
	public boolean error() {
		return false;
	}
	
	@Override
	public boolean accept() {
		return false;
	}
}