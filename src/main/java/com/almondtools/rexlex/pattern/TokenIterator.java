package com.almondtools.rexlex.pattern;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.almondtools.rexlex.Token;
import com.almondtools.rexlex.TokenFactory;
import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.Automaton;
import com.almondtools.rexlex.automaton.AutomatonMatcher;
import com.almondtools.rexlex.automaton.AutomatonMatcherListener;

import net.amygdalum.util.io.CharProvider;

public class TokenIterator<T extends Token> implements Iterator<T>, AutomatonMatcherListener {

	private static final TokenType STOP = new TokenType() {
		
		@Override
		public boolean error() {
			return false;
		}
		
		@Override
		public boolean accept() {
			return false;
		}
	};

	private final Match match;

	private AutomatonMatcher matcher;
	private TokenType error;
	private CharProvider chars;
	private TokenFactory<T> factory;
	private List<T> buffer;

	public TokenIterator(Automaton automaton, CharProvider chars, TokenFactory<T> factory) {
		this(automaton, chars, factory, Collections.<TokenType> emptySet());
	}

	public TokenIterator(Automaton automaton, CharProvider chars, TokenFactory<T> factory, Set<TokenType> ignored) {
		this.match = new Match();
		this.matcher = automaton.matcher().withListener(this);
		this.error = automaton.getErrorType();
		this.chars = chars;
		this.factory = factory;
		this.buffer = new LinkedList<T>();
	}

	@Override
	public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
		long end = chars.current();
		if (start == end) {
			return false;
		} else if (!match.isMatch()) { // new match
			if (start > 0) {
				bufferToken(factory.createToken(chars.slice(0, start), error));
			}
			match.init(start, chars.slice(start, end), accepted);
			return false;
		} else {
			long mstart = match.start;
			long mend = match.end;
			TokenType mtype = match.type;
			if (mend < start) {
				bufferToken(factory.createToken(chars.slice(mstart, mend), mtype));
				bufferToken(factory.createToken(chars.slice(mend, start), error));
				match.init(start, chars.slice(start, end), accepted);
				return true;
			} else if (mstart == start) { // exending match
				if (mend < end) {
					match.init(start, chars.slice(start, end), accepted);
				}
				return false;
			} else if (mend > start) { // subsumed match
				chars.move(mend);
				return false;
			} else { // next match
				bufferToken(factory.createToken(chars.slice(mstart, mend), mtype));
				match.init(start, chars.slice(start, end), accepted);
				return true;
			}
		}
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, long start) {
		long current = chars.current();
		long last = match.isMatch() ? match.end : 0;
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
		long current = chars.current();
		if (match.type == STOP) {
			return;
		} else if (!match.isMatch()) {
			long start = 0;
			long end = current;
			if (start < end) {
				bufferToken(factory.createToken(chars.slice(start, end), error));
			}
			match.type = STOP;
		} else {
			long mstart = match.start;
			long mend = match.end;
			TokenType mtype = match.type;
			bufferToken(factory.createToken(chars.slice(mstart, mend), mtype));
			if (mend < current) {
				bufferToken(factory.createToken(chars.slice(mend, current), error));
			}
			match.type = STOP;
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
