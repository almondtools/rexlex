package com.almondarts.relex.automaton;

import com.almondarts.relex.TokenType;
import com.almondarts.relex.io.CharProvider;

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
