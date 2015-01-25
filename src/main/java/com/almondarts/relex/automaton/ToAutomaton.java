package com.almondarts.relex.automaton;

public interface ToAutomaton<F extends Automaton,T extends Automaton> {

	T transform(F automaton);

}
