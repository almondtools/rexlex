package com.almondtools.relex.automaton;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.almondtools.relex.automaton.GenericAutomaton;
import com.almondtools.relex.automaton.GenericAutomaton.State;
import com.almondtools.relex.automaton.GenericAutomaton.Transition;

public class GenericAutomatonMatcher extends TypeSafeMatcher<GenericAutomaton> implements Matcher<GenericAutomaton> {

	private List<Matcher<Transition>> transitionMatchers;

	private GenericAutomatonMatcher() {
		this.transitionMatchers = new LinkedList<Matcher<Transition>>();
	}
	
	public static GenericAutomatonMatcher matchesAutomaton() {
		return new GenericAutomatonMatcher();
	}

	public GenericAutomatonMatcher containsTransition(Matcher<Transition> transition) {
		transitionMatchers.add(transition);
		return this;
	}

	@Override
	protected boolean matchesSafely(GenericAutomaton automaton) {
		if (!transitionMatchers.isEmpty()) {
			Set<Transition> alltransitions = new LinkedHashSet<Transition>();
			for (State state : automaton.findAllStates()) {
				alltransitions.addAll(state.getTransitions());
			}
			nextMatcher : for (Matcher<Transition> matcher : transitionMatchers) {
				for (Transition transition: alltransitions) {
					if (matcher.matches(transition)) {
						continue nextMatcher;
					}
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		for (Matcher<Transition> matcher : transitionMatchers) {
			description.appendText("containing a transition that: ").appendDescriptionOf(matcher);
		}
	}

}
