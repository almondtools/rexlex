package com.almondtools.relex.automaton;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.io.CharProvider;

public class BaseListener implements AutomatonMatcherListener {

	@Override
	public boolean reportMatch(CharProvider chars, int start, TokenType accepted) {
		return false;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, int start) {
		chars.move(start + 1);
		return false;
	}

}
