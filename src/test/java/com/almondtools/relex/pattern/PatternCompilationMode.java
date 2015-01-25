package com.almondtools.relex.pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.almondtools.relex.automaton.GenericAutomaton;
import com.almondtools.relex.automaton.ToAutomaton;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToCompactGenericAutomaton;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToMinimalDeterministicAutomaton;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.relex.pattern.DefaultMatcherBuilder;
import com.almondtools.relex.pattern.MatcherBuilder;

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