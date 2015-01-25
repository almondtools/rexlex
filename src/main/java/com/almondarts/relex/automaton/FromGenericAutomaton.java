package com.almondarts.relex.automaton;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.almondarts.collections.CollectionUtils;
import com.almondarts.relex.TokenType;
import com.almondarts.relex.automaton.DeterministicAutomaton.ExactTransition;
import com.almondarts.relex.automaton.DeterministicAutomaton.RangeTransition;
import com.almondarts.relex.automaton.DeterministicAutomaton.State;

public class FromGenericAutomaton {

	public static Map<com.almondarts.relex.automaton.GenericAutomaton.State, State> mapStates(Set<com.almondarts.relex.automaton.GenericAutomaton.State> allStates) {
		IdentityHashMap<com.almondarts.relex.automaton.GenericAutomaton.State, State> mapped = new IdentityHashMap<com.almondarts.relex.automaton.GenericAutomaton.State, State>();
		for (com.almondarts.relex.automaton.GenericAutomaton.State state : allStates) {
			mapped.put(state, new State(state.getType()));
		}
		for (Map.Entry<com.almondarts.relex.automaton.GenericAutomaton.State, State> entry : mapped.entrySet()) {
			com.almondarts.relex.automaton.GenericAutomaton.State key = entry.getKey();
			State value = entry.getValue();
			for (GenericAutomaton.EventTransition transition : key.getEventTransitions()) {
				if (transition instanceof GenericAutomaton.RangeTransition) {
					value.addTransition(new RangeTransition(transition.getFrom(), transition.getTo(), mapped.get(transition.getTarget())));
				} else if (transition instanceof GenericAutomaton.ExactTransition) {
					value.addTransition(new ExactTransition(((GenericAutomaton.ExactTransition) transition).getValue(), mapped.get(transition.getTarget())));
				}
			}
		}
		return mapped;
	}

	public static class ToCompactGenericAutomaton implements ToAutomaton<GenericAutomaton, GenericAutomaton>{

		@Override
		public GenericAutomaton transform(GenericAutomaton automaton) {
			return automaton.clone().eliminateEpsilons().eliminateDuplicateFinalStates().eliminateDuplicateTransitions();
		}
		
	}

	public static class ToDeterministicAutomaton implements ToAutomaton<GenericAutomaton, DeterministicAutomaton> {

		@Override
		public DeterministicAutomaton transform(GenericAutomaton automaton) {
			GenericAutomaton almostDeterministic = automaton.clone().eliminateEpsilons().determinize();
			Map<com.almondarts.relex.automaton.GenericAutomaton.State, State> mappedStates = mapStates(almostDeterministic.findAllStates());
			return new DeterministicAutomaton(mappedStates.get(almostDeterministic.getStart()), mappedStates.get(almostDeterministic.getError()));
		}
		
	}

	public static class ToMinimalDeterministicAutomaton implements ToAutomaton<GenericAutomaton, DeterministicAutomaton>{

		private TokenType remainder;

		public ToMinimalDeterministicAutomaton() {
		}

		public ToMinimalDeterministicAutomaton(TokenType remainder) {
			this.remainder = remainder;
		}

		@Override
		public DeterministicAutomaton transform(GenericAutomaton automaton) {
			GenericAutomaton almostDeterministic = automaton.clone().eliminateEpsilons().determinize();
			if (remainder != null) {
				GenericAutomaton almostMinimalDeterministic = almostDeterministic.totalizeAndClean(remainder).minimize();
				Map<com.almondarts.relex.automaton.GenericAutomaton.State, State> mappedStates = mapStates(almostMinimalDeterministic.findAllStates());
				return new DeterministicAutomaton(mappedStates.get(almostMinimalDeterministic.getStart()), mappedStates.get(almostMinimalDeterministic.getError()));
			} else {
				GenericAutomaton almostMinimalDeterministic = almostDeterministic.totalizeAndClean().minimize();
				Map<com.almondarts.relex.automaton.GenericAutomaton.State, State> mappedStates = mapStates(almostMinimalDeterministic.findAllStates());
				return new DeterministicAutomaton(mappedStates.get(almostMinimalDeterministic.getStart()), mappedStates.get(almostMinimalDeterministic.getError()));
			}
		}
		
	}
	
	public static class ToTabledAutomaton implements ToAutomaton<GenericAutomaton, TabledAutomaton> {

		private TokenType remainder;

		public ToTabledAutomaton() {
		}

		public ToTabledAutomaton(TokenType remainder) {
			this.remainder = remainder;
		}

		@Override
		public TabledAutomaton transform(GenericAutomaton automaton) {
			DeterministicAutomaton dfa = createDeterministicAutomaton(automaton);
			char[] relevantChars = CollectionUtils.toCharArray(dfa.computeRelevantCharacters());
			return new TabledAutomaton(relevantChars, dfa.getStart(), dfa.getError(), dfa.getProperty());
		}

		private DeterministicAutomaton createDeterministicAutomaton(GenericAutomaton automaton) {
			GenericAutomaton almostDeterministic = automaton.clone().eliminateEpsilons().determinize();
			if (remainder != null) {
				GenericAutomaton almostMinimalDeterministic = almostDeterministic.totalizeAndClean(remainder).minimize();
				Map<com.almondarts.relex.automaton.GenericAutomaton.State, State> mappedStates = mapStates(almostMinimalDeterministic.findAllStates());
				return new DeterministicAutomaton(mappedStates.get(almostMinimalDeterministic.getStart()), mappedStates.get(almostMinimalDeterministic.getError()));
			} else {
				GenericAutomaton almostMinimalDeterministic = almostDeterministic.totalizeAndClean().minimize();
				Map<com.almondarts.relex.automaton.GenericAutomaton.State, State> mappedStates = mapStates(almostMinimalDeterministic.findAllStates());
				return new DeterministicAutomaton(mappedStates.get(almostMinimalDeterministic.getStart()), mappedStates.get(almostMinimalDeterministic.getError()));
			}
		}

	}

}
