package com.almondtools.rexlex.automaton;

import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public interface AutomatonMatcher {

	AutomatonMatcher withListener(AutomatonMatcherListener listener);

	AutomatonMatcherListener applyTo(CharProvider chars);
	boolean isSuspended();
	AutomatonMatcherListener resume();
}
