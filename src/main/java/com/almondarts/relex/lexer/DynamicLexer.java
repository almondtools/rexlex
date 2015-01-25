package com.almondarts.relex.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.almondarts.relex.Lexer;
import com.almondarts.relex.Token;
import com.almondarts.relex.TokenFactory;
import com.almondarts.relex.TokenType;
import com.almondarts.relex.automaton.Automaton;
import com.almondarts.relex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondarts.relex.automaton.GenericAutomaton;
import com.almondarts.relex.automaton.GenericAutomatonBuilder;
import com.almondarts.relex.io.StringCharProvider;
import com.almondarts.relex.pattern.DefaultTokenType;
import com.almondarts.relex.pattern.Pattern;
import com.almondarts.relex.pattern.RemainderTokenType;

public class DynamicLexer<T extends Token> implements Lexer<T> {

	private Automaton automaton;
	private TokenFactory<T> factory;
	
	public DynamicLexer(Map<String, TokenType> patternToTypes, TokenFactory<T> factory) {
		this(patternToTypes, null, factory);
	}

	public DynamicLexer(Map<String, TokenType> patternToTypes, TokenType remainder, TokenFactory<T> factory) {
		this.automaton = createAutomaton(patternToTypes, remainder);
		this.factory = factory;
	}

	private static Automaton createAutomaton(Map<String, TokenType> patternToTypes, TokenType remainder) {
		List<GenericAutomaton> as = new ArrayList<GenericAutomaton>(patternToTypes.size());
		for (Map.Entry<String, TokenType> entry : patternToTypes.entrySet()) {
			String pattern = entry.getKey();
			TokenType type = entry.getValue();
			if (type == null) {
				type = DefaultTokenType.IGNORE;
			}
			as.add(Pattern.compileGenericAutomaton(pattern, new RemainderTokenType(type)));
		}
		return GenericAutomatonBuilder.matchAlternatives(as).toAutomaton(new ToTabledAutomaton(remainder));
	}

	@Override
	public Iterator<T> lex(String input) {
		return new TokenFilter<T>(automaton.tokenize(new StringCharProvider(input, 0), factory)) {

			@Override
			public boolean isValid(T token) {
				if (token.getType() == null || token.getType() == DefaultTokenType.IGNORE || token.getType() == DefaultTokenType.ERROR) {
					return false;
				} else {
					return true;
				}
			}
			
		};
	}

}
