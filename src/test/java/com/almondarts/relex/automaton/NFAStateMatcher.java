package com.almondarts.relex.automaton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.almondarts.relex.automaton.GenericAutomaton.State;

public class NFAStateMatcher extends TypeSafeMatcher<State> implements Matcher<State> {

	private Boolean accepting;

	public static NFAStateMatcher accepts() {
		return new NFAStateMatcher().setAccepting(true);
	}
	
	public NFAStateMatcher setAccepting(boolean accepting) {
		this.accepting = accepting;
		return this;
	}

	@Override
	protected boolean matchesSafely(State state) {
		if (accepting != null && accepting != state.accept()) {
			return false;
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is accepting");
	}

}
