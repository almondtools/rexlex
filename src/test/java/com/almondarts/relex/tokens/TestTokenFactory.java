package com.almondarts.relex.tokens;

import com.almondarts.relex.TokenFactory;
import com.almondarts.relex.TokenType;

public class TestTokenFactory implements TokenFactory<TestToken>{

	@Override
	public TestToken createToken(String literal, TokenType type) {
		return new TestToken(literal, type);
	}
}