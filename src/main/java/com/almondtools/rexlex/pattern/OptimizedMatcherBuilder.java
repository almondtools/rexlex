package com.almondtools.rexlex.pattern;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.AutomatonMatcher;
import com.almondtools.rexlex.automaton.AutomatonMatcherListener;
import com.almondtools.rexlex.automaton.AutomatonProperty;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.LongestMatchListener;
import com.almondtools.rexlex.automaton.MatchListener;
import com.almondtools.rexlex.automaton.ShortestMatchListener;
import com.almondtools.rexlex.automaton.TabledAutomaton;
import com.almondtools.rexlex.automaton.ToAutomaton;

import net.amygdalum.stringsearchalgorithms.io.CharProvider;
import net.amygdalum.stringsearchalgorithms.io.ReverseCharProvider;
import net.amygdalum.stringsearchalgorithms.search.StringMatch;
import net.amygdalum.stringsearchalgorithms.search.chars.Horspool;
import net.amygdalum.stringsearchalgorithms.search.chars.MultiStringSearchAlgorithmFactory;
import net.amygdalum.stringsearchalgorithms.search.chars.SetBackwardOracleMatching;
import net.amygdalum.stringsearchalgorithms.search.chars.StringSearchAlgorithm;
import net.amygdalum.stringsearchalgorithms.search.chars.StringSearchAlgorithmFactory;

public class OptimizedMatcherBuilder implements MatcherBuilder {

	private static final int MAX_SAMPLES = 4000;

	private ToAutomaton<GenericAutomaton, TabledAutomaton> builder;
	private TabledAutomaton completeAutomaton;
	private TabledAutomaton searchAutomaton;
	private StringSearchAlgorithmFactory wordSearch;
	private MultiStringSearchAlgorithmFactory multiwordSearch;
	private StringSearchAlgorithm stringSearchAlgorithm;


	public OptimizedMatcherBuilder() {
		this(new Horspool.Factory(), new SetBackwardOracleMatching.Factory());
	}

	public OptimizedMatcherBuilder(StringSearchAlgorithmFactory word, MultiStringSearchAlgorithmFactory multiword) {
		this.builder = new ToTabledAutomaton();
		this.wordSearch = word;
		this.multiwordSearch = multiword;
	}

	public static OptimizedMatcherBuilder from(GenericAutomaton nfa) {
		OptimizedMatcherBuilder builder = new OptimizedMatcherBuilder();
		builder.initWith(nfa);
		return builder;
	}

	@Override
	public MatcherBuilder initWith(GenericAutomaton nfa) {
		TabledAutomaton baseAutomaton = nfa.toAutomaton(builder);
		AutomatonProperty property = baseAutomaton.getProperty();
		if (property.isLinear()) {
			stringSearchAlgorithm = wordSearch.of(baseAutomaton.getSamples(1).iterator().next());
			return this;
		}
		if (property.isAcyclic()) {
			List<String> patterns = new ArrayList<String>();
			for (String pattern : baseAutomaton.getSamples(MAX_SAMPLES)) {
				patterns.add(pattern);
			}
			if (patterns.size() < MAX_SAMPLES) {
				stringSearchAlgorithm = multiwordSearch.of(patterns);
				return this;
			}
		} else {
			this.completeAutomaton = baseAutomaton;
			this.searchAutomaton = nfa.addInitialSelfLoop().toAutomaton(builder);
			TabledAutomaton reversePrefixAutomaton = nfa.revert().toAutomaton(builder);
			attach(searchAutomaton, reversePrefixAutomaton, completeAutomaton);
		}
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
	public com.almondtools.rexlex.pattern.Matcher buildMatcher(String input) {
		if (stringSearchAlgorithm != null) {
			return new StringMatcher(input, stringSearchAlgorithm);
		} else {
			return new Matcher(input, completeAutomaton.matcher());
		}
	}

	@Override
	public com.almondtools.rexlex.pattern.Finder buildFinder(String input) {
		if (stringSearchAlgorithm != null) {
			return new StringFinder(input, stringSearchAlgorithm);
		} else {
			return new Finder(input, searchAutomaton.matcher());
		}
	}

	private static class StringMatcher extends com.almondtools.rexlex.pattern.Matcher {

		private net.amygdalum.stringsearchalgorithms.search.StringFinder finder;

		public StringMatcher(String input, StringSearchAlgorithm stringSearchAlgorithm) {
			super(input);
			this.finder = stringSearchAlgorithm.createFinder(chars);
		}

		@Override
		public boolean matches() {
			StringMatch match = finder.findNext();
			return match != null && chars.finished();
		}
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

	private static class StringFinder extends com.almondtools.rexlex.pattern.Finder {

		private net.amygdalum.stringsearchalgorithms.search.StringFinder finder;

		public StringFinder(String input, StringSearchAlgorithm stringSearchAlgorithm) {
			super(input);
			this.finder = stringSearchAlgorithm.createFinder(chars);
		}

		@Override
		public boolean find() {
			long lastMatchEnd = 0;
			if (match != null) {
				lastMatchEnd = match.end();
			}
			do {
				match = findFirstMatch();
			} while (match != null && match.start() < lastMatchEnd);
			if (match == null) {
				chars.finish();
				return false;
			} else {
				finder.skipTo(match.end());
				return true;
			}
		}

		private Match findFirstMatch() {
			return wrap(finder.findNext());
		}

		private Match wrap(StringMatch match) {
			if (match == null) {
				return null;
			}
			return new Match(match.start(), match.text());
		}

	}

	private static class Finder extends com.almondtools.rexlex.pattern.Finder implements AutomatonMatcherListener {

		private AutomatonMatcher search;

		public Finder(String text, AutomatonMatcher search) {
			super(text);
			this.search = search.withListener(this);
		}

		@Override
		public boolean reportMatch(CharProvider chars, long start, TokenType accepted) {
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
		public boolean recoverMismatch(CharProvider chars, long start) {
			return false;
		}

		@Override
		public boolean find() {
			match = null;
			if (search.isSuspended()) {
				search.resume();
			} else {
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
			long current = chars.current();
			Match reverseMatch = ((MatchListener) reverse.applyTo(new ReverseCharProvider(chars))).getMatch();
			long start = reverseMatch.start();
			chars.move(current);
			Match forwardMatch = ((MatchListener) complete.applyTo(chars)).getMatch();
			long end = forwardMatch.end();
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
