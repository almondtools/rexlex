package com.almondtools.relex.tokens;

import com.almondtools.relex.TokenFactory;
import com.almondtools.relex.TokenType;

public class TestTokenFactory implements TokenFactory<TestToken>{

	@Override
	public TestToken createToken(String literal, TokenType type) {
		return new TestToken(literal, type);
	}
}