package com.almondtools.relex;

public interface TokenFactory<T extends Token> {

	T createToken(String literal, TokenType type);

}
