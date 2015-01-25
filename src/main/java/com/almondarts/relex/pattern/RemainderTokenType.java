package com.almondarts.relex.pattern;

import com.almondarts.relex.TokenType;


public class RemainderTokenType implements PatternOption {
	
	private TokenType remainder;
	
	public RemainderTokenType(TokenType remainder) {
		this.remainder = remainder;
	}

	public TokenType getRemainder() {
		return remainder;
	}
}