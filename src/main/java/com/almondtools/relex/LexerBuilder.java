package com.almondtools.relex;

public interface LexerBuilder<T extends Token> {

	Lexer<T> build();
	void matchRemainder(TokenType type);
	void matchPattern(String pattern, TokenType type);

}