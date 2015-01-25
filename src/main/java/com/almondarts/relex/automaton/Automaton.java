package com.almondarts.relex.automaton;

import java.util.Iterator;

import com.almondarts.relex.Token;
import com.almondarts.relex.TokenFactory;
import com.almondarts.relex.TokenType;
import com.almondarts.relex.io.CharProvider;

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
