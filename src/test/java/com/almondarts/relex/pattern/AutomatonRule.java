package com.almondarts.relex.pattern;

import org.junit.rules.TestRule;

import com.almondarts.relex.automaton.Automaton;

public class AutomatonRule extends PatternCompilationModeRule implements TestRule {
	
	public Automaton compile(String string, PatternOption... options) {
		return Pattern.compileAutomaton(string, mode.getAutomatonBuilder(), options);
	}
}
