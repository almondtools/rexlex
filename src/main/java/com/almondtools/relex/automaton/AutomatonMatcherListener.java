package com.almondtools.relex.automaton;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.io.CharProvider;

public interface AutomatonMatcherListener {

	/**
	 * reports a match
	 * @return true if process should suspend, false if process should resume
	 */
	boolean reportMatch(CharProvider chars, int start, TokenType accepted);

	/**
	 * reports a mismatch
	 * @return true if process should suspend, false if process should resume
	 */
	boolean recoverMismatch(CharProvider chars, int start);

}
