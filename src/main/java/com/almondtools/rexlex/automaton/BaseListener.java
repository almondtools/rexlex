package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.io.CharProvider;

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
