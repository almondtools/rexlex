package com.almondarts.relex.automaton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.almondarts.relex.automaton.DeterministicAutomaton.State;

public class DFAStateMatcher extends TypeSafeMatcher<State> implements Matcher<State> {

	private boolean tokenType;

	public static DFAStateMatcher hasTokenType() {
		return new DFAStateMatcher().withTokenType(true);
	}
	
	public DFAStateMatcher withTokenType(boolean tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	@Override
	protected boolean matchesSafely(State state) {
		if (tokenType && state.getType() == null) {
			return false;
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("has a non null token type");
	}

}
