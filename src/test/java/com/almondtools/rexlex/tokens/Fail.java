package com.almondtools.rexlex.tokens;

import com.almondtools.rexlex.TokenType;

public enum Fail implements TokenType {
	TESTERROR;

	@Override
	public boolean error() {
		return true;
	}
	
	@Override
	public boolean accept() {
		return false;
	}
}