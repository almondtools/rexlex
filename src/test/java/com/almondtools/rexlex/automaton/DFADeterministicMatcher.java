package com.almondtools.rexlex.automaton;

import java.util.SortedSet;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.almondtools.rexlex.automaton.DeterministicAutomaton.State;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.Transition;

public class DFADeterministicMatcher extends TypeSafeMatcher<DeterministicAutomaton> implements Matcher<DeterministicAutomaton> {

	public static DFADeterministicMatcher isDeterministic() {
		return new DFADeterministicMatcher();
	}
	
	@Override
	protected boolean matchesSafely(DeterministicAutomaton automaton) {
		SortedSet<Character> samples = automaton.computeRelevantCharacters();
		for (State state : automaton.findAllStates()) {
			for (Character sample : samples) {
				int matching = 0;
				for (Transition transition : state.getTransitions()) {
					if (transition.matches(sample)) {
						matching++;
					}
				}
				if (matching > 1) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is deterministic");
	}

}
