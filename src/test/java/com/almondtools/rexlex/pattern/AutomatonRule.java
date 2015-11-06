package com.almondtools.rexlex.pattern;

import org.junit.rules.TestRule;

import com.almondtools.rexlex.automaton.Automaton;

public class AutomatonRule extends PatternCompilationModeRule implements TestRule {
	
	public Automaton compile(String string, PatternOption... options) {
		return Pattern.compileAutomaton(string, mode.getAutomatonBuilder(), options);
	}
}
