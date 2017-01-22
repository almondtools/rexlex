package com.almondtools.rexlex.pattern;

import static com.almondtools.rexlex.pattern.PatternOptionUtil.list;
import static com.almondtools.rexlex.pattern.PatternOptionUtil.splitFirst;
import static com.almondtools.rexlex.pattern.PatternOptionUtil.splitOf;

import java.util.List;

import com.almondtools.rexlex.automaton.Automaton;
import com.almondtools.rexlex.automaton.AutomatonBuilder;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomatonBuilder;
import com.almondtools.rexlex.automaton.ToAutomaton;

import net.amygdalum.regexparser.RegexNode;
import net.amygdalum.regexparser.RegexParser;
import net.amygdalum.regexparser.RegexParserOption;

public class Pattern {

	private static final GenericAutomatonBuilder DEFAULT_AUTOMATON_BUILDER = new GenericAutomatonBuilder();

	private String pattern;
	private MatcherBuilder builder;

	private Pattern(String pattern, MatcherBuilder builder) {
		this.pattern = pattern;
		this.builder = builder;
	}

	private static DefaultMatcherBuilder defaultMatcherBuilder() {
		return new DefaultMatcherBuilder();
	}

	public String pattern() {
		return pattern;
	}

	public static GenericAutomaton compileGenericAutomaton(String pattern, List<PatternOption> options) {
		List<PatternFlag> patternFlags = splitOf(options, PatternFlag.class);
		RegexParserOption[] parserOptions = new RegexParserOption[patternFlags.size()];
		for (int i = 0; i < parserOptions.length; i++) {
			parserOptions[i] = patternFlags.get(i).getOption();
		}
		RegexNode node = new RegexParser(pattern, parserOptions).parse();
		RemainderTokenType tokenType = splitFirst(options, RemainderTokenType.class);
		if (tokenType == null) {
			return automatonBuilder(options).buildFrom(node);
		} else {
			return automatonBuilder(options).buildFrom(node, tokenType.getRemainder());
		}
	}

	public static GenericAutomaton compileGenericAutomaton(String pattern, PatternOption... options) {
		return compileGenericAutomaton(pattern, list(options));
	}

	public static <T extends Automaton> T compileAutomaton(String pattern, ToAutomaton<GenericAutomaton, T> transformer, List<PatternOption> list) {
		GenericAutomaton genericAutomaton = compileGenericAutomaton(pattern, list);
		return transformer.transform(genericAutomaton);
	}

	public static <T extends Automaton> T compileAutomaton(String pattern, ToAutomaton<GenericAutomaton, T> transformer, PatternOption... options) {
		return compileAutomaton(pattern, transformer, list(options));
	}

	public static Pattern compile(String pattern, List<PatternOption> options) {
		GenericAutomaton genericAutomaton = compileGenericAutomaton(pattern, options);
		return new Pattern(pattern, matcherBuilder(options).initWith(genericAutomaton));
	}

	public static Pattern compile(String pattern, PatternOption... options) {
		return compile(pattern, list(options));
	}

	private static AutomatonBuilder automatonBuilder(List<PatternOption> options) {
		AutomatonBuilder builder = splitFirst(options, AutomatonBuilder.class);
		if (builder == null) {
			return DEFAULT_AUTOMATON_BUILDER;
		}
		return builder;
	}

	private static MatcherBuilder matcherBuilder(List<PatternOption> options) {
		MatcherBuilder builder = splitFirst(options, MatcherBuilder.class);
		if (builder == null) {
			return defaultMatcherBuilder();
		}
		return builder;
	}
	
	public Finder finder(String input) {
		return builder.buildFinder(input);
	}

	public Matcher matcher(String input) {
		return builder.buildMatcher(input);
	}

}
