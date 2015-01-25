package com.almondarts.relex.pattern;

import com.almondarts.relex.TokenType;

public class RemainderTokenFactory extends DefaultTokenType.Factory implements TokenTypeFactory {

	private TokenType remainder;
	
	public RemainderTokenFactory(TokenType remainder) {
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
