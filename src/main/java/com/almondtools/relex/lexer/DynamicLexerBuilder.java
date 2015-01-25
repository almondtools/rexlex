package com.almondtools.relex.lexer;

import java.util.LinkedHashMap;
import java.util.Map;

import com.almondtools.relex.Lexer;
import com.almondtools.relex.LexerBuilder;
import com.almondtools.relex.Token;
import com.almondtools.relex.TokenFactory;
import com.almondtools.relex.TokenType;

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
