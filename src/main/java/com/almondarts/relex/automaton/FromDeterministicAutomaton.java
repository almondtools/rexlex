package com.almondarts.relex.automaton;

import java.util.IdentityHashMap;
import java.util.Map;

import com.almondarts.collections.CollectionUtils;
import com.almondarts.relex.automaton.DeterministicAutomaton.State;
import com.almondarts.relex.automaton.DeterministicAutomaton.StateVisitor;
import com.almondarts.relex.automaton.DeterministicAutomaton.Transition;

public class FromDeterministicAutomaton {

	public static class ToGenericAutomaton implements ToAutomaton<DeterministicAutomaton, GenericAutomaton>, StateVisitor<com.almondarts.relex.automaton.GenericAutomaton.State> {

		private Map<State, com.almondarts.relex.automaton.GenericAutomaton.State> states;

		public ToGenericAutomaton() {
			this.states = new IdentityHashMap<State, com.almondarts.relex.automaton.GenericAutomaton.State>();
		}

		@Override
		public GenericAutomaton transform(DeterministicAutomaton automaton) {
			return new GenericAutomaton(automaton.getStart().apply(this));
		}


		@Override
		public com.almondarts.relex.automaton.GenericAutomaton.State visitState(State state) {
			com.almondarts.relex.automaton.GenericAutomaton.State clonedstate = states.get(state);
			if (clonedstate == null) {
				clonedstate = state.toNFA();
				states.put(state, clonedstate);
				for (Transition transition : state.getTransitions()) {
					com.almondarts.relex.automaton.GenericAutomaton.State clonedTarget = transition.getTarget().apply(this);
					com.almondarts.relex.automaton.GenericAutomaton.Transition clonedtransition = transition.toNFA(clonedTarget);
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
