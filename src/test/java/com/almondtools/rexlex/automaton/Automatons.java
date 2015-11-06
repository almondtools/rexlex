package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.tokens.Accept.A;
import static com.almondtools.rexlex.tokens.Accept.B;
import static com.almondtools.rexlex.tokens.Accept.REMAINDER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.almondtools.rexlex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.io.CharProvider;
import com.almondtools.rexlex.io.StringCharProvider;
import com.almondtools.rexlex.pattern.DefaultTokenType;

public class Automatons {

	public static List<String> withOutEmpty(List<String> list) {
		List<String> result = new LinkedList<String>(list);
		result.remove("");
		return result;
	}

	public static GenericAutomaton withTokens(GenericAutomaton a) {
		for (State state : a.findAllStates()) {
			if (state.getType() == DefaultTokenType.ACCEPT || state.getType() == DefaultTokenType.IGNORE) {
				state.setType(REMAINDER);
			}
		}
		return a;
	}

	public static GenericAutomaton nfa1() {
		State state0 = new State(DefaultTokenType.ACCEPT);
		State state1 = new State(DefaultTokenType.ACCEPT);
		State state2 = new State();
		State state3 = new State();

		state0.addTransition(new ExactTransition('a', state1));
		state0.addTransition(new ExactTransition('a', state2));

		state1.addTransition(new ExactTransition('a', state1));
		state1.addTransition(new ExactTransition('a', state2));

		state2.addTransition(new ExactTransition('b', state1));
		state2.addTransition(new ExactTransition('b', state3));

		state3.addTransition(new ExactTransition('a', state1));
		state3.addTransition(new ExactTransition('a', state2));

		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa2() {
		State state0 = new State();
		State state1 = new State(DefaultTokenType.ACCEPT);
		State state2 = new State();
		State state3 = new State();
		State state4 = new State();

		state0.addTransition(new ExactTransition('a', state1));
		state0.addTransition(new RangeTransition('a', 'b', state2));
		state0.addTransition(new ExactTransition('b', state3));

		state1.addTransition(new ExactTransition('a', state1));
		state1.addTransition(new ExactTransition('a', state2));

		state2.addTransition(new ExactTransition('b', state2));
		state2.addTransition(new ExactTransition('b', state3));
		state2.addTransition(new ExactTransition('b', state4));

		state3.addTransition(new ExactTransition('b', state3));
		state3.addTransition(new ExactTransition('b', state3));
		state3.addTransition(new RangeTransition('a', 'b', state4));

		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa3() {
		State state1 = new State();
		State state2 = new State();
		State state3 = new State();
		State state4 = new State();
		State state6 = new State();
		State state7 = new State();
		State state8 = new State();
		State state9 = new State();
		State state10 = new State(DefaultTokenType.ACCEPT);

		state1.addTransition(new ExactTransition('0', state2));
		state1.addTransition(new ExactTransition('1', state6));

		state2.addTransition(new ExactTransition('0', state7));
		state2.addTransition(new ExactTransition('1', state3));

		state3.addTransition(new ExactTransition('0', state8));
		state3.addTransition(new ExactTransition('1', state4));

		state4.addTransition(new ExactTransition('0', state9));
		state4.addTransition(new ExactTransition('1', state10));

		state6.addTransition(new RangeTransition('0', '1', state7));

		state7.addTransition(new RangeTransition('0', '1', state8));

		state8.addTransition(new RangeTransition('0', '1', state10));

		return new GenericAutomaton(state1);
	}

	public static GenericAutomaton nfa4() {
		State state0 = new State();
		State state1 = new State();
		State state2 = new State();
		State state3 = new State(DefaultTokenType.ACCEPT);

		state0.addTransition(new ExactTransition('a', state1));
		state0.addTransition(new RangeTransition('a', 'b', state2));

		state1.addTransition(new ExactTransition('a', state2));
		state1.addTransition(new ExactTransition('b', state3));

		state2.addTransition(new ExactTransition('a', state1));
		state2.addTransition(new ExactTransition('a', state2));
		state2.addTransition(new ExactTransition('b', state3));

		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa5() {
		State state0 = new State(A);
		State state1 = new State(B);
		State state2 = new State();
		State state3 = new State();

		state0.addTransition(new ExactTransition('a', state1));
		state0.addTransition(new ExactTransition('a', state2));

		state1.addTransition(new ExactTransition('a', state1));
		state1.addTransition(new ExactTransition('a', state2));

		state2.addTransition(new ExactTransition('b', state1));
		state2.addTransition(new ExactTransition('b', state3));

		state3.addTransition(new ExactTransition('a', state1));
		state3.addTransition(new ExactTransition('a', state2));

		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa6() {
		State state0 = new State();
		State state1 = new State();
		State state2 = new State();
		State state3 = new State(DefaultTokenType.ACCEPT);

		state0.addTransition(new ExactTransition('a', state1));

		state1.addTransition(new ExactTransition('b', state2));

		state2.addTransition(new ExactTransition('b', state2));
		state2.addTransition(new ExactTransition('c', state3));
		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa7() {
		State state0 = new State(DefaultTokenType.ACCEPT);
		State state1 = new State(DefaultTokenType.ACCEPT);

		state0.addTransition(new ExactTransition('a', state1));

		state1.addTransition(new ExactTransition('b', state1));

		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa8() {
		State state0 = new State();
		State state1 = new State();
		State state2 = new State(DefaultTokenType.ACCEPT);
		State state3 = new State();
		State state4 = new State(DefaultTokenType.ACCEPT);

		state0.addTransition(new ExactTransition('a', state1));
		state0.addTransition(new ExactTransition('a', state3));

		state1.addTransition(new ExactTransition('a', state2));

		state3.addTransition(new ExactTransition('a', state3));
		state3.addTransition(new ExactTransition('b', state4));

		return new GenericAutomaton(state0);
	}

	public static GenericAutomaton nfa9() {
		State state0 = new State();
		State state1 = new State();
		State state2 = new State(DefaultTokenType.ACCEPT);
		State state3 = new State();
		State state4 = new State(DefaultTokenType.ACCEPT);

		state0.addTransition(new ExactTransition('a', state1));
		state0.addTransition(new ExactTransition('a', state3));

		state1.addTransition(new ExactTransition('a', state2));

		state2.addTransition(new ExactTransition('c', state0));

		state3.addTransition(new ExactTransition('a', state3));
		state3.addTransition(new ExactTransition('b', state4));

		return new GenericAutomaton(state0);
	}

	public static List<String> valid1() {
		return Arrays.asList(
			"",
			"a",
			"aa",
			"aaa",
			"aaab",
			"aaaab",
			"aaaba",
			"aaaaba",
			"aaabab",
			"aaaabab",
			"aaababab",
			"aaaababab",
			"aaaabaaabaaaaaab"
			);
	}

	public static List<String> valid2() {
		return Arrays.asList(
			"a",
			"aa",
			"aaa",
			"aaaa",
			"aaaaa"
			);
	}

	public static List<String> valid3() {
		List<String> input = new ArrayList<String>(16);
		for (int i = 0; i < 16; i++) {
			StringBuilder buffer = new StringBuilder();
			buffer.append((i & 0x08) / 0x08);
			buffer.append((i & 0x04) / 0x04);
			buffer.append((i & 0x02) / 0x02);
			buffer.append((i & 0x01) / 0x01);
			input.add(buffer.toString());
		}
		input.remove("0110");
		return input;
	}

	public static List<String> valid4() {
		return Arrays.asList(
			"bb",
			"aab",
			"aaaaaaab",
			"bab",
			"baaaab"
			);
	}

	public static List<String> valid5() {
		return Arrays.asList(
			"",
			"a",
			"aa",
			"aaa",
			"aaab",
			"aaaab",
			"aaaba",
			"aaaaba",
			"aaabab",
			"aaaabab",
			"aaababab",
			"aaaababab",
			"aaaabaaabaaaaaab"
			);
	}

	public static List<String> valid6() {
		return Arrays.asList(
			"abc",
			"abbc",
			"abbbc"
			);
	}

	public static List<String> valid7() {
		return Arrays.asList(
			"a",
			"ab",
			"abb"
			);
	}

	public static List<String> valid8() {
		return Arrays.asList(
			"aa",
			"aab"
			);
	}

	public static List<String> valid9() {
		return Arrays.asList(
			"aa",
			"aab",
			"aacaa",
			"aacaab",
			"aacaacaa",
			"aacaacaab"
			);
	}

	public static List<String> invalid1() {
		return Arrays.asList(
			"b",
			"abb",
			"aaaabb",
			"aaaabaabb"
			);
	}

	public static List<String> invalid2() {
		return Arrays.asList(
			"",
			"b",
			"ab",
			"baaa"
			);
	}

	public static List<String> invalid3() {
		return Arrays.asList(
			"0110",
			"abcd"
			);
	}

	public static List<String> invalid4() {
		return Arrays.asList(
			"",
			"b",
			"bbb",
			"baaaaba"
			);
	}

	public static List<String> invalid5() {
		return Arrays.asList(
			"b",
			"abb",
			"aaaabb",
			"aaaabaabb"
			);
	}

	public static List<String> invalid6() {
		return Arrays.asList(
			"ac",
			"ab",
			"bc"
			);
	}

	public static List<String> invalid7() {
		return Arrays.asList(
			"",
			"b",
			"ba"
			);
	}

	public static List<String> invalid8() {
		return Arrays.asList(
			"",
			"a"
			);
	}

	public static List<String> invalid9() {
		return Arrays.asList(
			"",
			"a"
			);
	}

	public static void assertMatches(Automaton automaton, List<String> valid, List<String> invalid) {
		String[] validStrings = valid.toArray(new String[0]);
		String[] invalidStrings = invalid.toArray(new String[0]);
		assertThat(matchSamples(automaton, validStrings), containsInAnyOrder(validStrings));
		assertThat(matchSamples(automaton, invalidStrings), empty());
	}

	public static Set<String> matchSamples(Automaton a, String... samples) {
		MatchCollector collector = new MatchCollector();
		AutomatonMatcher matcher = a.matcher().withListener(collector);
		for (String sample : samples) {
			matcher.applyTo(chars(sample));
		}
		return new LinkedHashSet<String>(collector.getMatchedTexts());
	}

	public static Set<String> matchPrefixes(Automaton a, String... samples) {
		PrefixCollector collector = new PrefixCollector();
		AutomatonMatcher matcher = a.matcher().withListener(collector);
		for (String sample : samples) {
			matcher.applyTo(chars(sample));
		}
		return new LinkedHashSet<String>(collector.getMatchedTexts());
	}

	public static CharProvider chars(String sample) {
		return new StringCharProvider(sample, 0);
	}

	public static void store(com.almondtools.rexlex.automaton.GenericAutomaton.State state) {
		store(new GenericAutomaton(state), 2);
	}

	public static void store(com.almondtools.rexlex.automaton.DeterministicAutomaton.State state) {
		store(new DeterministicAutomaton(state), 2);
	}

	public static void store(Automaton automaton) {
		store(automaton, 2);
	}

	private static void store(Automaton automaton, int stackIndex) {
		try {
			StackTraceElement caller = new Exception().getStackTrace()[stackIndex];
			String name = (caller.getMethodName() + caller.getLineNumber()).replaceAll("\\W", "_");
			automaton.store(name).to(new FileOutputStream(name + ".dot"));
		} catch (IOException e) {
		}
	}

}
