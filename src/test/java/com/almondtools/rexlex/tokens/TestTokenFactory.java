package com.almondtools.rexlex.tokens;

import com.almondtools.rexlex.TokenFactory;
import com.almondtools.rexlex.TokenType;

public class TestTokenFactory implements TokenFactory<TestToken>{

	@Override
	public TestToken createToken(String literal, TokenType type) {
		return new TestToken(literal, type);
	}
}