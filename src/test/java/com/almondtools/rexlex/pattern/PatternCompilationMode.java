package com.almondtools.rexlex.pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToCompactGenericAutomaton;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToMinimalDeterministicAutomaton;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.ToAutomaton;

public enum PatternCompilationMode {
	NFA {
		@Override
		public ToAutomaton<GenericAutomaton, ?> getAutomatonBuilder() {
			return new ToCompactGenericAutomaton();
		}
	}, ODFA {
		@Override
		public ToAutomaton<GenericAutomaton, ?> getAutomatonBuilder() {
			return new ToMinimalDeterministicAutomaton();
		}
	}, TDFA {
		@Override
		public ToAutomaton<GenericAutomaton, ?> getAutomatonBuilder() {
			return new ToTabledAutomaton();
		}
	};
	
	public abstract ToAutomaton<GenericAutomaton, ?> getAutomatonBuilder();
	public MatcherBuilder getMatcherBuilder() {
		return new DefaultMatcherBuilder(getAutomatonBuilder());
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Exclude {
		
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Only {
		PatternCompilationMode[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Not {
		PatternCompilationMode[] value();
	}

}