package com.almondtools.relex.io;

import com.almondtools.relex.automaton.CharClassMapper;

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
	public int current() {
		return chars.current();
	}
	
	@Override
	public void move(int i) {
		chars.move(i);
	}

	@Override
	public boolean finished() {
		return chars.finished();
	}

	@Override
	public char at(int i) {
		return chars.at(i);
	}
	
	@Override
	public String slice(int start, int end) {
		return chars.slice(start, end);
	}

}
