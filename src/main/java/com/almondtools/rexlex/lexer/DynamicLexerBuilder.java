package com.almondtools.rexlex.lexer;

import java.util.LinkedHashMap;
import java.util.Map;

import com.almondtools.rexlex.Lexer;
import com.almondtools.rexlex.LexerBuilder;
import com.almondtools.rexlex.Token;
import com.almondtools.rexlex.TokenFactory;
import com.almondtools.rexlex.TokenType;

public class DynamicLexerBuilder<T extends Token> implements LexerBuilder<T> {

	private TokenFactory<T> factory;
	private Map<String, TokenType> patterns;
	private TokenType remainder;

	public DynamicLexerBuilder(TokenFactory<T> factory) {
		this.factory = factory;
		this.patterns = new LinkedHashMap<String, TokenType>();
	}

	@Override
	public Lexer<T> build() {
		return new DynamicLexer<T>(patterns, remainder, factory);
	}

	@Override
	public void matchRemainder(TokenType type) {
		this.remainder = type;
	}

	@Override
	public void matchPattern(String pattern, TokenType type) {
		this.patterns.put(pattern, type);
	}

}
