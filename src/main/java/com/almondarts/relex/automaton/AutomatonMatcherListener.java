package com.almondarts.relex.automaton;

import com.almondarts.relex.TokenType;
import com.almondarts.relex.io.CharProvider;

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
