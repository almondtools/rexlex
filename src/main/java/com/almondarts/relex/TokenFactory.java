package com.almondarts.relex;

public interface TokenFactory<T extends Token> {

	T createToken(String literal, TokenType type);

}
