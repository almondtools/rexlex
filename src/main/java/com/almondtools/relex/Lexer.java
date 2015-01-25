package com.almondtools.relex;

import java.util.Iterator;

public interface Lexer<T extends Token> {

	Iterator<T> lex(String input);

}
