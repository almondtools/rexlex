package com.almondtools.relex.tokens;

import com.almondtools.relex.TokenType;

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