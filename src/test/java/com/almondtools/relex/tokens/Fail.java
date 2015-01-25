package com.almondtools.relex.tokens;

import com.almondtools.relex.TokenType;

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