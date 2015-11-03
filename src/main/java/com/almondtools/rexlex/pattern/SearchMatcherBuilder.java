package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.AutomatonMatcher;
import com.almondtools.rexlex.automaton.AutomatonMatcherListener;
import com.almondtools.rexlex.automaton.FromGenericAutomaton;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.LongestMatchListener;
import com.almondtools.rexlex.automaton.MatchListener;
import com.almondtools.rexlex.automaton.ShortestMatchListener;
import com.almondtools.rexlex.automaton.TabledAutomaton;
import com.almondtools.rexlex.automaton.ToAutomaton;
import com.almondtools.rexlex.io.CharProvider;
import com.almondtools.rexlex.io.ReverseCharProvider;

public class SearchMatcherBuilder implements MatcherBuilder {

	private ToAutomaton<GenericAutomaton, TabledAutomaton> builder;
	private TabledAutomaton searchAutomaton;
	private TabledAutomaton completeAutomaton;

	public SearchMatcherBuilder() {
		this.builder = new ToTabledAutomaton();
	}

	public static SearchMatcherBuilder from(GenericAutomaton nfa) {
		SearchMatcherBuilder builder = new SearchMatcherBuilder();
		builder.initWith(nfa);
		return builder;
	}

	@Override
	public MatcherBuilder initWith(GenericAutomaton nfa) {
		//TODO following line is there for performance, it should not change anything, but omitting it results in errors (see MatcherBenchmarktest)
		nfa = nfa.toAutomaton(new FromGenericAutomaton.ToMinimalDeterministicGenericAutomaton());
		this.completeAutomaton = nfa.toAutomaton(builder);
		GenericAutomaton addInitialSelfLoop = nfa.addInitialSelfLoop();
		this.searchAutomaton = addInitialSelfLoop.toAutomaton(builder);
		TabledAutomaton reversePrefixAutomaton = nfa.revert().toAutomaton(builder);
		attach(searchAutomaton, reversePrefixAutomaton, completeAutomaton);
		return this;
	}

	private void attach(TabledAutomaton searchAutomaton, TabledAutomaton reversePrefixAutomaton, TabledAutomaton completeAutomaton) {
		AutomatonMatcherListener firstMatch = new ShortestMatchListener();
		AutomatonMatcherListener lastMatch = new LongestMatchListener();
		TokenType[] accept = searchAutomaton.getAccept();
		for (int i = 0; i < accept.length; i++) {
			if (accept[i] != null && accept[i].accept()) {
				String path = searchAutomaton.findPathTo(i);
				int j = completeAutomaton.findState(path);
				accept[i] = new AttachedTokenType(accept[i], reversePrefixAutomaton.matcher().withListener(firstMatch), completeAutomaton.matcher(j).withListener(lastMatch));
			}
		}
	}

	@Override
	public Matcher buildMatcher(String input) {
		return new Matcher(input, completeAutomaton.matcher());
	}

	@Override
	public com.almondtools.rexlex.pattern.Finder buildFinder(String input) {
		return new Finder(input, searchAutomaton.matcher());
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
		public boolean reportMatch(CharProvider chars, int start, TokenType accepted) {
			if (chars.finished()) {
				matched = true;
				return true;
			}
			return false;
		}

		@Override
		public boolean recoverMismatch(CharProvider chars, int start) {
			return true;
		}

	}

	private static class Finder extends com.almondtools.rexlex.pattern.Finder implements AutomatonMatcherListener {

		private AutomatonMatcher search;

		public Finder(String text, AutomatonMatcher search) {
			super(text);
			this.search = search.withListener(this);
		}

		@Override
		public boolean reportMatch(CharProvider chars, int start, TokenType accepted) {
			if (match != null) {
				if (match.start() == start) {
					return false;
				} else {
					match = extend(chars, accepted);
					return true;
				}
			} else {
				match = extend(chars, accepted);
				return true;
			}
		}

		@Override
		public boolean recoverMismatch(CharProvider chars, int start) {
			return false;
		}

		@Override
		public boolean find() {
			if (search.isSuspended()) {
				if (match != null) {
					chars.move(match.end());
				}
				match = null;
				search.resume();
			} else {
				match = null;
				search.applyTo(chars);
			}
			if (match == null) {
				chars.finish();
				return false;
			} else {
				return true;
			}
		}

		private Match extend(CharProvider chars, TokenType accepted) {
			AttachedTokenType token = (AttachedTokenType) accepted;
			AutomatonMatcher reverse = token.getReverse();
			AutomatonMatcher complete = token.getComplete();
			int current = chars.current();
			Match reverseMatch = ((MatchListener) reverse.applyTo(new ReverseCharProvider(chars))).getMatch();
			int start = reverseMatch.start();
			chars.move(current);
			Match forwardMatch = ((MatchListener) complete.applyTo(chars)).getMatch();
			int end = forwardMatch.end();
			chars.move(end);
			return new Match(start, chars.slice(start, end), forwardMatch.getType());
		}


	}

	private static class AttachedTokenType implements TokenType {

		private TokenType wrapped;
		private AutomatonMatcher reverse;
		private AutomatonMatcher complete;

		public AttachedTokenType(TokenType wrapped, AutomatonMatcher reverse, AutomatonMatcher complete) {
			this.wrapped = wrapped;
			this.reverse = reverse;
			this.complete = complete;
		}

		public AutomatonMatcher getReverse() {
			return reverse;
		}

		public AutomatonMatcher getComplete() {
			return complete;
		}

		@Override
		public boolean error() {
			return wrapped.error();
		}

		@Override
		public boolean accept() {
			return wrapped.accept();
		}

	}
}
