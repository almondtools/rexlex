package com.almondtools.rexlex.tokens;

import com.almondtools.rexlex.TokenType;

public enum Accept implements TokenType {
	A,B,C,D,E,F,G,H,I,J,K,L,REMAINDER;

	@Override
	public boolean error() {
		return false;
	}
	
	@Override
	public boolean accept() {
		return true;
	}
}