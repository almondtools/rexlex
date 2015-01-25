package com.almondtools.relex.automaton;

import com.almondtools.relex.io.CharProvider;

public interface AutomatonMatcher {

	AutomatonMatcher withListener(AutomatonMatcherListener listener);

	AutomatonMatcherListener applyTo(CharProvider chars);
	boolean isSuspended();
	AutomatonMatcherListener resume();
}
