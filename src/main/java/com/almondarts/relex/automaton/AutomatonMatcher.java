package com.almondarts.relex.automaton;

import com.almondarts.relex.io.CharProvider;

public interface AutomatonMatcher {

	AutomatonMatcher withListener(AutomatonMatcherListener listener);

	AutomatonMatcherListener applyTo(CharProvider chars);
	boolean isSuspended();
	AutomatonMatcherListener resume();
}
