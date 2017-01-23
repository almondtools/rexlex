package com.almondtools.rexlex.io;

import com.almondtools.rexlex.automaton.CharClassMapper;

import net.amygdalum.util.io.CharProvider;

public class MappedCharClassProvider implements CharClassProvider {

	private CharProvider chars;
	private CharClassMapper charClassMapper;

	public MappedCharClassProvider(CharProvider chars, CharClassMapper charClassMapper) {
		this.chars = chars;
		this.charClassMapper = charClassMapper;
	}

	@Override
	public int next() {
		return charClassMapper.getIndex(chars.next());
	}

	@Override
	public int lookahead() {
		return charClassMapper.getIndex(chars.lookahead());
	}

	@Override
	public int lookahead(int i) {
		return charClassMapper.getIndex(chars.lookahead(i));
	}

	@Override
	public int prev() {
		return charClassMapper.getIndex(chars.prev());
	}

	@Override
	public int lookbehind() {
		return charClassMapper.getIndex(chars.lookbehind());
	}

	@Override
	public int lookbehind(int i) {
		return charClassMapper.getIndex(chars.lookbehind(i));
	}

	@Override
	public long current() {
		return chars.current();
	}
	
	@Override
	public void move(long i) {
		chars.move(i);
	}

	@Override
	public boolean finished() {
		return chars.finished();
	}

	@Override
	public char at(long i) {
		return chars.at(i);
	}
	
	@Override
	public String slice(long start, long end) {
		return chars.slice(start, end);
	}

}
