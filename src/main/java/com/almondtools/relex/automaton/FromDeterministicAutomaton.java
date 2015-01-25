package com.almondtools.relex.automaton;

import java.util.IdentityHashMap;
import java.util.Map;

import com.almondtools.relex.automaton.DeterministicAutomaton.State;
import com.almondtools.relex.automaton.DeterministicAutomaton.StateVisitor;
import com.almondtools.relex.automaton.DeterministicAutomaton.Transition;
import com.almondtools.util.collections.CollectionUtils;

public class FromDeterministicAutomaton {

	public static class ToGenericAutomaton implements ToAutomaton<DeterministicAutomaton, GenericAutomaton>, StateVisitor<com.almondtools.relex.automaton.GenericAutomaton.State> {

		private Map<State, com.almondtools.relex.automaton.GenericAutomaton.State> states;

		public ToGenericAutomaton() {
			this.states = new IdentityHashMap<State, com.almondtools.relex.automaton.GenericAutomaton.State>();
		}

		@Override
		public GenericAutomaton transform(DeterministicAutomaton automaton) {
			return new GenericAutomaton(automaton.getStart().apply(this));
		}


		@Override
		public com.almondtools.relex.automaton.GenericAutomaton.State visitState(State state) {
			com.almondtools.relex.automaton.GenericAutomaton.State clonedstate = states.get(state);
			if (clonedstate == null) {
				clonedstate = state.toNFA();
				states.put(state, clonedstate);
				for (Transition transition : state.getTransitions()) {
					com.almondtools.relex.automaton.GenericAutomaton.State clonedTarget = transition.getTarget().apply(this);
					com.almondtools.relex.automaton.GenericAutomaton.Transition clonedtransition = transition.toNFA(clonedTarget);
					clonedstate.addTransition(clonedtransition);
				}
			}
			return clonedstate;
		}

	}

	public static class ToTabledAutomaton implements ToAutomaton<DeterministicAutomaton, TabledAutomaton> {

		@Override
		public TabledAutomaton transform(DeterministicAutomaton automaton) {
			char[] relevantChars = CollectionUtils.toCharArray(automaton.computeRelevantCharacters());
			return new TabledAutomaton(relevantChars, automaton.getStart(), automaton.getError());
		}

	}
}
