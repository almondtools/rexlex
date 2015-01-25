package com.almondtools.relex.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.almondtools.relex.Lexer;
import com.almondtools.relex.Token;
import com.almondtools.relex.TokenFactory;
import com.almondtools.relex.TokenType;
import com.almondtools.relex.automaton.Automaton;
import com.almondtools.relex.automaton.GenericAutomaton;
import com.almondtools.relex.automaton.GenericAutomatonBuilder;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.relex.io.StringCharProvider;
import com.almondtools.relex.pattern.DefaultTokenType;
import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.pattern.RemainderTokenType;

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
