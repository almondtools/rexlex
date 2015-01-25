package com.almondtools.relex.lexer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.almondtools.relex.Token;

public abstract class TokenFilter<T extends Token> implements Iterator<T> {

	private Iterator<T> tokens;
	private T buffer;

	public TokenFilter(Iterator<T> tokens) {
		this.tokens = tokens;
	}

	@Override
	public boolean hasNext() {
		if (buffer != null) {
			return true;
		}
		if (!tokens.hasNext()) {
			return false;
		}
		while (tokens.hasNext()) {
			T token = tokens.next();
			if (isValid(token)) {
				buffer = token;
				return true;
			}
		}
		return false;
	}

	public abstract boolean isValid(T token);

	@Override
	public T next() {
		if (buffer == null) {
			hasNext();
			if (buffer == null) {
				throw new NoSuchElementException();
			}
		}
		T token = buffer;
		buffer = null;
		return token;
	}

	@Override
	public void remove() {
		tokens.remove();
	}

}
