package com.almondtools.rexlex;

public interface TokenFactory<T extends Token> {

	T createToken(String literal, TokenType type);

}
