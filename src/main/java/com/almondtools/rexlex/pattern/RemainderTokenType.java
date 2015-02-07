package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.TokenType;


public class RemainderTokenType implements PatternOption {
	
	private TokenType remainder;
	
	public RemainderTokenType(TokenType remainder) {
		this.remainder = remainder;
	}

	public TokenType getRemainder() {
		return remainder;
	}
}