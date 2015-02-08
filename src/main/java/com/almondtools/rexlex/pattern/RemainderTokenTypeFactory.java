package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.TokenType;

public class RemainderTokenTypeFactory extends DefaultTokenTypeFactory implements TokenTypeFactory {

	private TokenType remainder;
	
	public RemainderTokenTypeFactory(TokenType remainder) {
		this.remainder = remainder;
	}
	
	@Override
	public TokenType union(TokenType type1, TokenType type2) {
		if (remainder.equals(type1)|| remainder.equals(type2)) {
			return remainder;
		}
		return super.union(type1, type2);
	}

	@Override
	public TokenType errorType() {
		return remainder;
	}

}
