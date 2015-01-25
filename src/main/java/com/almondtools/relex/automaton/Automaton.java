package com.almondtools.relex.automaton;

import java.util.Iterator;

import com.almondtools.relex.Token;
import com.almondtools.relex.TokenFactory;
import com.almondtools.relex.TokenType;
import com.almondtools.relex.io.CharProvider;

public interface Automaton {
	
	String getId();
	TokenType getErrorType();
	
	AutomatonProperty getProperty();
	Iterable<String> getSamples(int length);
	
	<T extends Token> Iterator<T> tokenize(CharProvider chars, TokenFactory<T> factory);
	
	AutomatonMatcher matcher();
	
	Automaton revert();
	
	AutomatonExport store(String name);
	
}
