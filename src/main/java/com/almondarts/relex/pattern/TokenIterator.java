package com.almondarts.relex.pattern;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.almondarts.relex.Token;
import com.almondarts.relex.TokenFactory;
import com.almondarts.relex.TokenType;
import com.almondarts.relex.automaton.Automaton;
import com.almondarts.relex.automaton.AutomatonMatcher;
import com.almondarts.relex.automaton.AutomatonMatcherListener;
import com.almondarts.relex.io.CharProvider;

public class TokenIterator<T extends Token> implements Iterator<T>, AutomatonMatcherListener {

	private static final Match STOP = new Match(-1, "");

	private AutomatonMatcher matcher;
	private TokenType error;
	private CharProvider chars;
	private Match match;
	private TokenFactory<T> factory;
	private List<T> buffer;

	public TokenIterator(Automaton automaton, CharProvider chars, TokenFactory<T> factory) {
		this(automaton, chars, factory, Collections.<TokenType> emptySet());
	}

	public TokenIterator(Automaton automaton, CharProvider chars, TokenFactory<T> factory, Set<TokenType> ignored) {
		this.matcher = automaton.matcher().withListener(this);
		this.error = automaton.getErrorType();
		this.chars = chars;
		this.factory = factory;
		this.buffer = new LinkedList<T>();
	}

	@Override
	public boolean reportMatch(CharProvider chars, int start, TokenType accepted) {
		int end = chars.current();
		if (start == end) {
			return false;
		} else if (match == null) { // new match
			if (start > 0) {
				bufferToken(factory.createToken(chars.slice(0, start), error));
			}
			match = new Match(start, chars.slice(start, end), accepted);
			return false;
		} else {
			int mstart = match.start();
			int mend = match.end();
			TokenType mtype = match.getType();
			if (mend < start) {
				bufferToken(factory.createToken(chars.slice(mstart, mend), mtype));
				bufferToken(factory.createToken(chars.slice(mend, start), error));
				match = new Match(start, chars.slice(start, end), accepted);
				return true;
			} else if (mstart == start) { // exending match
				if (mend < end) {
					match = new Match(start, chars.slice(start, end), accepted);
				}
				return false;
			} else if (mend > start) { // subsumed match
				chars.move(mend);
				return false;
			} else { // next match
				bufferToken(factory.createToken(chars.slice(mstart, mend), mtype));
				match = new Match(start, chars.slice(start, end), accepted);
				return true;
			}
		}
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, int start) {
		int current = chars.current();
		int last = match != null ? match.end() : 0;
		if (start >= last) {
			chars.move(start);
			if (!chars.finished()) {
				chars.next();
			}
		} else if (current < last) {
			chars.move(last);
		} else {
			chars.move(current - 1);
		}
		return false;
	}

	private void completeBuffer() {
		int current = chars.current();
		if (match == STOP) {
			return;
		} else if (match == null) {
			int start = 0;
			int end = current;
			if (start < end) {
				bufferToken(factory.createToken(chars.slice(start, end), error));
			}
			match = STOP;
		} else {
			int mstart = match.start();
			int mend = match.end();
			TokenType mtype = match.getType();
			bufferToken(factory.createToken(chars.slice(mstart, mend), mtype));
			if (mend < current) {
				bufferToken(factory.createToken(chars.slice(mend, current), error));
			}
			match = STOP;
		}
		chars.finish();
	}

	@Override
	public boolean hasNext() {
		if (!buffer.isEmpty()) {
			return true;
		}
		if (matcher.isSuspended()) {
			matcher.resume();
		} else {
			matcher.applyTo(chars);
		}
		if (buffer.isEmpty()) {
			completeBuffer();
		}
		return !buffer.isEmpty();
	}

	private void bufferToken(T token) {
		buffer.add(token);
	}

	@Override
	public T next() {
		if (buffer.isEmpty()) {
			hasNext();
			if (buffer.isEmpty()) {
				throw new NoSuchElementException();
			}
		}
		return buffer.remove(0);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
