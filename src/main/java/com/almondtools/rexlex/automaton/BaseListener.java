package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public class BaseListener implements AutomatonMatcherListener {

	@Override
	public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
		return false;
	}

	@Override
	public boolean recoverMismatch(CharProvider chars, long start) {
		chars.move(start + 1);
		return false;
	}

}
