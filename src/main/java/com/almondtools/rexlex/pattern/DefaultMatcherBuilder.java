package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.Automaton;
import com.almondtools.rexlex.automaton.AutomatonMatcher;
import com.almondtools.rexlex.automaton.AutomatonMatcherListener;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.ToAutomaton;
import net.amygdalum.stringsearchalgorithms.io.CharProvider;

public class DefaultMatcherBuilder implements MatcherBuilder {

	private ToAutomaton<GenericAutomaton, ?> builder;
	private Automaton automaton;

	public DefaultMatcherBuilder(ToAutomaton<GenericAutomaton, ?> builder) {
		this.builder = builder;
	}

	public DefaultMatcherBuilder() {
		this(new ToTabledAutomaton());
	}

	public static DefaultMatcherBuilder from(GenericAutomaton nfa) {
		DefaultMatcherBuilder builder = new DefaultMatcherBuilder();
		builder.initWith(nfa);
		return builder;
	}

	@Override
	public MatcherBuilder initWith(GenericAutomaton nfa) {
		automaton = nfa.toAutomaton(builder);
		return this;
	}

	@Override
	public com.almondtools.rexlex.pattern.Finder buildFinder(String input) {
		return new Finder(input, automaton.matcher());
	}
	
	@Override
	public com.almondtools.rexlex.pattern.Matcher buildMatcher(String input) {
		return new Matcher(input, automaton.matcher());
	}

	private static class Matcher extends com.almondtools.rexlex.pattern.Matcher implements AutomatonMatcherListener {
		
		private AutomatonMatcher matcher;
		private boolean matched;

		public Matcher(String text, AutomatonMatcher matcher) {
			super(text);
			this.matcher = matcher.withListener(this);
			this.matched = false;
		}
		
		@Override
		public boolean matches() {
			if (matched) {
				return true;
			} else {
				matcher.applyTo(chars);
				return matched;
			}
		}

		@Override
		public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
			if (chars.finished()) {
				matched = true;
				return true;
			}
			return false;
		}

		@Override
		public boolean recoverMismatch(CharProvider chars, long start) {
			return true;
		}

	}
	
	private static class Finder extends com.almondtools.rexlex.pattern.Finder implements AutomatonMatcherListener {

		private AutomatonMatcher matcher;
		private Match nextMatch;

		public Finder(String text, AutomatonMatcher matcher) {
			super(text);
			this.matcher = matcher;
			this.matcher.withListener(this);
		}

		@Override
		public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
			long end = chars.current();
			String text = chars.slice(start, end);
			if (match != null) {
				if (match.start() == start) {
					if (match.text().length() < text.length()) {
						match = new Match(start, text, accepted);
					}
					return false;
				} else {
					if (start >= match.end()) {
						nextMatch = new Match(start, text, accepted);
					}
					return true;
				}
			} else {
				match = new Match(start, text, accepted);
				return false;
			}
		}

		@Override
		public boolean recoverMismatch(CharProvider chars, long start) {
			chars.move(start + 1);
			return false;
		}

		@Override
		public boolean find() {
			match = nextMatch;
			nextMatch = null;
			matcher.applyTo(chars);
			if (match == null) {
				chars.finish();
				return false;
			} else if (nextMatch != null) {
				chars.move(nextMatch.start());
				return true;
			} else {
				return true;
			}
		}

	}
}
