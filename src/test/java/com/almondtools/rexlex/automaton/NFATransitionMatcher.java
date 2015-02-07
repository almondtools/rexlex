package com.almondtools.rexlex.automaton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.almondtools.rexlex.automaton.GenericAutomaton.Transition;

public class NFATransitionMatcher extends TypeSafeMatcher<Transition> {

	private Class<? extends Transition> clazz;
	
	public NFATransitionMatcher(Class<? extends Transition> clazz) {
		this.clazz = clazz;
	}
	
	public static Matcher<Transition> forClass(Class<? extends Transition> clazz) {
		return new NFATransitionMatcher(clazz);
	}

	@Override
	protected boolean matchesSafely(Transition transition) {
		return clazz.isInstance(transition);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("a transition of type ").appendValue(clazz.getName());
	}

}
