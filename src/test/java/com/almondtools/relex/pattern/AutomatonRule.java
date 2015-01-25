package com.almondtools.relex.pattern;

import org.junit.rules.TestRule;

import com.almondtools.relex.automaton.Automaton;
import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.pattern.PatternOption;

public class AutomatonRule extends PatternCompilationModeRule implements TestRule {
	
	public Automaton compile(String string, PatternOption... options) {
		return Pattern.compileAutomaton(string, mode.getAutomatonBuilder(), options);
	}
}
