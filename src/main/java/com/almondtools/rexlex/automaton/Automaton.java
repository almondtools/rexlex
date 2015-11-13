package com.almondtools.rexlex.automaton;

import java.util.Iterator;

import com.almondtools.rexlex.Token;
import com.almondtools.rexlex.TokenFactory;
import com.almondtools.rexlex.TokenType;
import com.almondtools.stringsandchars.io.CharProvider;

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
