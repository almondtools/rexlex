package com.almondtools.rexlex;

public interface LexerBuilder<T extends Token> {

	Lexer<T> build();
	void matchRemainder(TokenType type);
	void matchPattern(String pattern, TokenType type);

}