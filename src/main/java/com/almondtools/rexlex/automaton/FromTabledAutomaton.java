package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;

public class FromTabledAutomaton {

	public static class ToGenericAutomaton implements ToAutomaton<TabledAutomaton, GenericAutomaton> {

		@Override
		public GenericAutomaton transform(TabledAutomaton automaton) {
			CharClassMapper relevantChars = automaton.getCharClassMapper();
			int charClassCount = automaton.getCharClassCount();
			TokenType[] accept = automaton.getAccept();
			int[] transitions = automaton.getTransitions();
			com.almondtools.rexlex.automaton.GenericAutomaton.State[] states = new GenericAutomaton.State[transitions.length];
			for (int i = 0; i < states.length; i++) {
				states[i] = new com.almondtools.rexlex.automaton.GenericAutomaton.State(accept[i]);
			}
			for (int i = 0; i < states.length; i++) {
				int jfrom = i * charClassCount;
				int jto = jfrom + charClassCount;
				for (int j = jfrom; j < jto; j++) {
					int target = transitions[j];
					char from = relevantChars.lowerBound(j);
					char to = relevantChars.upperBound(j);
					if (from == to) {
						states[i].addTransition(new GenericAutomaton.ExactTransition(to, states[target]));
					} else {
						states[i].addTransition(new GenericAutomaton.RangeTransition(from, to, states[target]));
					}
				}
			}
			return new GenericAutomaton(states[automaton.getStartState()]);
		}


	}

}
