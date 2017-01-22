package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;

import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public interface AutomatonMatcherListener {

	/**
	 * reports a match
	 * @return true if process should suspend, false if process should resume
	 */
	boolean reportMatch(CharProvider chars, long start, TokenType accepted);

	/**
	 * reports a mismatch
	 * @return true if process should suspend, false if process should resume
	 */
	boolean recoverMismatch(CharProvider chars, long start);

}
