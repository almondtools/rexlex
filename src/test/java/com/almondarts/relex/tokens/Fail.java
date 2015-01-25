package com.almondarts.relex.tokens;

import com.almondarts.relex.TokenType;

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