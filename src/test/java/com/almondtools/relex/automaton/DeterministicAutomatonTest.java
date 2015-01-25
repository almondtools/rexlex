package com.almondtools.relex.automaton;

import static com.almondtools.relex.automaton.Automatons.assertMatches;
import static com.almondtools.relex.automaton.Automatons.chars;
import static com.almondtools.relex.automaton.Automatons.invalid1;
import static com.almondtools.relex.automaton.Automatons.invalid2;
import static com.almondtools.relex.automaton.Automatons.invalid3;
import static com.almondtools.relex.automaton.Automatons.invalid4;
import static com.almondtools.relex.automaton.Automatons.invalid5;
import static com.almondtools.relex.automaton.Automatons.invalid8;
import static com.almondtools.relex.automaton.Automatons.invalid9;
import static com.almondtools.relex.automaton.Automatons.matchPrefixes;
import static com.almondtools.relex.automaton.Automatons.matchSamples;
import static com.almondtools.relex.automaton.Automatons.nfa1;
import static com.almondtools.relex.automaton.Automatons.nfa2;
import static com.almondtools.relex.automaton.Automatons.nfa3;
import static com.almondtools.relex.automaton.Automatons.nfa4;
import static com.almondtools.relex.automaton.Automatons.nfa5;
import static com.almondtools.relex.automaton.Automatons.nfa8;
import static com.almondtools.relex.automaton.Automatons.nfa9;
import static com.almondtools.relex.automaton.Automatons.valid1;
import static com.almondtools.relex.automaton.Automatons.valid2;
import static com.almondtools.relex.automaton.Automatons.valid3;
import static com.almondtools.relex.automaton.Automatons.valid4;
import static com.almondtools.relex.automaton.Automatons.valid5;
import static com.almondtools.relex.automaton.Automatons.valid8;
import static com.almondtools.relex.automaton.Automatons.valid9;
import static com.almondtools.relex.pattern.DotGraphMatcher.startsWith;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.match;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchAlternatives;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchAnyChar;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchAnyOf;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchConcatenation;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchEmpty;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchFixedLoop;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchNothing;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchOptional;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchRangeLoop;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchStarLoop;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchUnlimitedLoop;
import static com.almondtools.relex.pattern.DefaultTokenType.ACCEPT;
import static com.almondtools.relex.pattern.DefaultTokenType.ERROR;
import static com.almondtools.relex.tokens.Fail.TESTERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.automaton.Automaton;
import com.almondtools.relex.automaton.DeterministicAutomaton;
import com.almondtools.relex.automaton.GenericAutomaton;
import com.almondtools.relex.automaton.DeterministicAutomaton.State;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToDeterministicAutomaton;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToMinimalDeterministicAutomaton;
import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.tokens.TestToken;
import com.almondtools.relex.tokens.TestTokenFactory;
import com.almondtools.util.text.StringUtils;

public class DeterministicAutomatonTest {

	private TestTokenFactory factory;

	@Before
	public void before() {
		this.factory = new TestTokenFactory();
	}

	@Test
	public void testFindPathToStartState() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(a.findPathTo(a.getStart()), equalTo(""));
	}

	@Test
	public void testFindPathToInvalidState() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(a.findPathTo(new State()), nullValue());
	}

	@Test
	public void testFindPathToTransitiveState() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(a.findPathTo(a.getStart().next('a')), equalTo("a"));
	}

	@Test
	public void testFindPathToTransitiveStateShortest() throws Exception {
		DeterministicAutomaton a = matchUnlimitedLoop(match('a'), 1).toAutomaton(new ToMinimalDeterministicAutomaton());
		State start = a.getStart();
		State stateA = start.next('a');
		State stateAA = stateA.next('a');
		assertThat(a.findPathTo(stateA), equalTo("a"));
		assertThat(a.findPathTo(stateAA), equalTo("a"));
	}

	@Test
	public void testFindStateStartState() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(a.findState(""), equalTo(a.getStart()));
	}

	@Test
	public void testFindStateErrorState() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(a.findState("b"), equalTo(a.getError()));
	}

	@Test
	public void testFindStateAcceptState() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(a.findState("a").getType(), equalTo((TokenType) ACCEPT));
	}

	@Test
	public void testFindStateFindPath() throws Exception {
		DeterministicAutomaton a = matchAlternatives(match('a'), match("ab")).toAutomaton(new ToMinimalDeterministicAutomaton());
		State stateA = a.findState("a");
		State stateAB = a.findState("ab");
		assertThat(a.findPathTo(stateA), equalTo("a"));
		assertThat(a.findPathTo(stateAB), equalTo("ab"));
	}

	@Test
	public void testMatchChar() throws Exception {
		DeterministicAutomaton a = match('a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a, "a", "aa", "b", ""), contains("a"));
	}

	@Test
	public void testMatchString() throws Exception {
		DeterministicAutomaton abc = match("abc").toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, "abc", "ab", "bc", "ac", "", "abcd"), contains("abc"));
	}

	@Test
	public void testMatchCharRange() throws Exception {
		DeterministicAutomaton abc = match('a', 'c').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, "a", "b", "c", "ab", "bc", "ac", "", "abcd"), contains("a", "b", "c"));
	}

	@Test
	public void testMatchReverseCharRange() throws Exception {
		DeterministicAutomaton abc = match('c', 'a').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, "a", "b", "c", "ab", "bc", "ac", "", "abcd"), contains("a", "b", "c"));
	}

	@Test
	public void testMatchAnyChar() throws Exception {
		DeterministicAutomaton abc = matchAnyChar().toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, "a", "z", "1", "&", "\n"), contains("a", "z", "1", "&", "\n"));
		assertThat(matchSamples(abc, "ab", "aa", "a\n", "a&b", ""), empty());
	}

	@Test
	public void testMatchAnyCharOf() throws Exception {
		DeterministicAutomaton abc = matchAnyOf('a', 'b').toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, "a", "b"), contains("a", "b"));
		assertThat(matchSamples(abc, "c", "cc", ""), empty());
	}

	@Test
	public void testMatchNothing() throws Exception {
		DeterministicAutomaton abc = matchNothing().toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, "", "a", "ab"), empty());
	}

	@Test
	public void testMatchEmpty() throws Exception {
		DeterministicAutomaton abc = matchEmpty().toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(abc, ""), contains(""));
		assertThat(matchSamples(abc, "a", "ab"), empty());
	}

	@Test
	public void testOptional() throws Exception {
		DeterministicAutomaton ab_question = matchOptional(match("ab")).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(ab_question, "", "ab"), contains("", "ab"));
		assertThat(matchSamples(ab_question, "a", "b", "abc"), empty());
	}

	@Test
	public void testUnlimitedLoop0() throws Exception {
		DeterministicAutomaton a_star = matchUnlimitedLoop(match('a'), 0).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_star, "", "a", "aa", "aaaaaaaaaaaaaaaaaaaaa"), contains("", "a", "aa", "aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_star, "ab"), empty());
	}

	@Test
	public void testUnlimitedLoop1() throws Exception {
		DeterministicAutomaton a_plus = matchUnlimitedLoop(match('a'), 1).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_plus, "a", "aa", "aaaaaaaaaaaaaaaaaaaaa"), contains("a", "aa", "aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_plus, "", "ab"), empty());
	}

	@Test
	public void testUnlimitedLoopN() throws Exception {
		DeterministicAutomaton a_minN = matchUnlimitedLoop(match('a'), 4).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_minN, "aaaa", "aaaaaaaaaaaaaaaaaaaaa"), contains("aaaa", "aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_minN, "", "aaa", "ab"), empty());
	}

	@Test
	public void testRangeLoop() throws Exception {
		DeterministicAutomaton a_1_2 = matchRangeLoop(match('a'), 1, 2).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_1_2, "a", "aa"), contains("a", "aa"));
		assertThat(matchSamples(a_1_2, "", "aaaaaaaaaaaaaaaaaaaaa", "ab"), empty());
	}

	@Test
	public void testBroadRangeLoop() throws Exception {
		DeterministicAutomaton a_2_4 = matchRangeLoop(match('a'), 2, 4).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_2_4, "aa", "aaa", "aaaa"), contains("aa", "aaa", "aaaa"));
		assertThat(matchSamples(a_2_4, "", "aaaaaaaaaaaaaaaaaaaaa", "ab"), empty());
	}

	@Test
	public void testFixedRangeLoop() throws Exception {
		DeterministicAutomaton a_1_1 = matchRangeLoop(match('a'), 1, 1).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_1_1, "a"), contains("a"));
		assertThat(matchSamples(a_1_1, "aa", "", "aaaaaaaaaaaaaaaaaaaaa"), empty());
	}

	@Test
	public void testFixedLoop() throws Exception {
		DeterministicAutomaton a_2 = matchFixedLoop(match('a'), 2).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(a_2, "aa"), contains("aa"));
		assertThat(matchSamples(a_2, "", "a", "aaaaaaaaaaaaaaaaaaaaa", "ab"), empty());
	}

	@Test
	public void testMatchConcatenation() throws Exception {
		DeterministicAutomaton aAndb = matchConcatenation(match('a'), match('b')).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(aAndb, "ab"), contains("ab"));
		assertThat(matchSamples(aAndb, "", "a", "b", "abab"), empty());
	}

	@Test
	public void testMatchAlternatives() throws Exception {
		DeterministicAutomaton aOrb = matchAlternatives(match('a'), match('b')).toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(matchSamples(aOrb, "a", "b"), contains("a", "b"));
		assertThat(matchSamples(aOrb, "", "ab", "abab"), empty());
	}

	@Test
	public void testMinimize1() throws Exception {
		GenericAutomaton nondet1 = nfa1();
		DeterministicAutomaton det1 = nondet1.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det1, valid1(), invalid1());
		assertThat(det1.findAllStates().size(), equalTo(3));
		assertThat(det1.findAcceptStates().size(), equalTo(2));
	}

	@Test
	public void testMinimize2() throws Exception {
		GenericAutomaton nondet2 = nfa2();
		DeterministicAutomaton det2 = nondet2.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det2, valid2(), invalid2());
		assertThat(det2.findAllStates().size(), equalTo(3));
		assertThat(det2.findAcceptStates().size(), equalTo(1));
	}

	@Test
	public void testMinimize3() throws Exception {
		GenericAutomaton nondet3 = nfa3();
		DeterministicAutomaton det3 = nondet3.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det3, valid3(), invalid3());
		assertThat(det3.findAllStates().size(), equalTo(9));
		assertThat(det3.findAcceptStates().size(), equalTo(1));
	}

	@Test
	public void testMinimize4() throws Exception {
		GenericAutomaton nondet4 = nfa4();
		DeterministicAutomaton det4 = nondet4.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det4, valid4(), invalid4());
		assertThat(det4.findAllStates().size(), equalTo(4));
		assertThat(det4.findAcceptStates().size(), equalTo(1));
	}

	@Test
	public void testTokenize5() throws Exception {
		GenericAutomaton nondet5 = nfa5();
		assertThat(nondet5.findAcceptStates(), everyItem(NFAStateMatcher.accepts()));
		DeterministicAutomaton det5 = nondet5.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det5, valid5(), invalid5());
		Iterator<TestToken> tokens = det5.tokenize(chars("aaaabbaaaabaaaa"), factory);
		assertThat(tokens.next().getLiteral(), equalTo("aaaab"));
		assertThat(tokens.next().getLiteral(), equalTo("b"));
		assertThat(tokens.next().getLiteral(), equalTo("aaaabaaaa"));
	}

	@Test
	public void testRevert1() throws Exception {
		DeterministicAutomaton det1 = (DeterministicAutomaton) nfa1().toAutomaton(new ToMinimalDeterministicAutomaton()).revert();
		assertMatches(det1, reverse(valid1()), reverse(invalid1()));
		assertThat(det1.findAllStates().size(), equalTo(3));
		assertThat(det1.findAcceptStates().size(), equalTo(1));
	}

	@Test
	public void testRevert2() throws Exception {
		DeterministicAutomaton det2 = (DeterministicAutomaton) nfa2().toAutomaton(new ToMinimalDeterministicAutomaton()).revert();
		assertMatches(det2, reverse(valid2()), reverse(invalid2()));
		assertThat(det2.findAllStates().size(), equalTo(3));
		assertThat(det2.findAcceptStates().size(), equalTo(1));
	}

	@Test
	public void testRevert3() throws Exception {
		DeterministicAutomaton det3 = (DeterministicAutomaton) nfa3().toAutomaton(new ToMinimalDeterministicAutomaton()).revert();
		assertMatches(det3, reverse(valid3()), reverse(invalid3()));
		assertThat(det3.findAllStates().size(), equalTo(9));
		assertThat(det3.findAcceptStates().size(), equalTo(1));
	}

	@Test
	public void testRevert4() throws Exception {
		DeterministicAutomaton det4 = (DeterministicAutomaton) nfa4().toAutomaton(new ToMinimalDeterministicAutomaton()).revert();
		assertMatches(det4, reverse(valid4()), reverse(invalid4()));
		assertThat(det4.findAllStates().size(), equalTo(5));
		assertThat(det4.findAcceptStates().size(), equalTo(2));
	}

	@Test
	public void testFind8() throws Exception {
		GenericAutomaton nondet8 = nfa8();
		DeterministicAutomaton det8 = nondet8.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det8, valid8(), invalid8());
		assertThat(matchPrefixes(det8, "aab"), containsInAnyOrder("aa", "aab"));
	}

	@Test
	public void testFind9() throws Exception {
		GenericAutomaton nondet9 = nfa9();
		DeterministicAutomaton det9 = nondet9.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertMatches(det9, valid9(), invalid9());
		assertThat(matchPrefixes(det9, "aacaab"), containsInAnyOrder("aacaa", "aacaab", "aa"));
	}

	@Test
	public void testGetIdWithoutStart() throws Exception {
		assertThat(new DeterministicAutomaton(null).getId(), equalTo("null"));
	}

	@Test
	public void testGetIdWithStart() throws Exception {
		State start = new State();
		String id = start.getId();
		assertThat(new DeterministicAutomaton(start).getId(), equalTo(id));
	}

	@Test
	public void testGetStart() throws Exception {
		State start = new State();
		assertThat(new DeterministicAutomaton(start).getStart(), equalTo(start));
	}

	@Test
	public void testGetError() throws Exception {
		State start = new State();
		State error = new State(ERROR);
		DeterministicAutomaton a = new DeterministicAutomaton(start);
		a.setError(error);
		assertThat(a.getError(), equalTo(error));
	}

	@Test
	public void testGetErrorTypeWithMissingErrorState() throws Exception {
		State start = new State();
		DeterministicAutomaton a = new DeterministicAutomaton(start);
		assertThat(a.getErrorType(), equalTo((TokenType) ERROR));
	}

	@Test
	public void testGetErrorType() throws Exception {
		State start = new State();
		State error = new State(TESTERROR);
		DeterministicAutomaton a = new DeterministicAutomaton(start);
		a.setError(error);
		assertThat(a.getErrorType(), equalTo((TokenType) TESTERROR));
	}

	@Test
	public void testGetDefaultErrorType() throws Exception {
		State start = new State();
		State error = new State();
		DeterministicAutomaton a = new DeterministicAutomaton(start);
		a.setError(error);
		assertThat(a.getErrorType(), equalTo((TokenType) ERROR));
	}

	@Test
	public void testMatches() throws Exception {
		Automaton posix = Pattern.compileAutomaton("ab*c|a.*c", new ToDeterministicAutomaton());
		assertThat(matchSamples(posix, "abbbbcc"), contains("abbbbcc"));
	}

	@Test
	public void testMatchesNot() throws Exception {
		Automaton automaton = Pattern.compileAutomaton("a", new ToDeterministicAutomaton());
		assertThat(matchSamples(automaton, "b", "bc"), empty());
	}

	@Test
	public void testPrefixes() throws Exception {
		Automaton posix = Pattern.compileAutomaton("one(self)?(selfsufficient)?", new ToDeterministicAutomaton());
		assertThat(matchPrefixes(posix, "oneselfsufficient next"), containsInAnyOrder("one", "oneself", "oneselfsufficient"));
	}

	@Test
	public void testStoreA() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a').toAutomaton(new ToMinimalDeterministicAutomaton()).store("matchA").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchA\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a"));
	}

	@Test
	public void testStoreAB() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a', 'b').toAutomaton(new ToMinimalDeterministicAutomaton()).store("matchAB").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchAB\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a-b"));
	}

	@Test
	public void testStoreAStar() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		matchStarLoop(match('a')).toAutomaton(new ToMinimalDeterministicAutomaton()).store("matchAStar").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchAStar\"")
			.withNodes(0, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(0, "&epsilon;")
			.withArcs(1, "a"));
	}

	@Test
	public void testStoreUnprintableCharTransition() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match((char) 0x02FF).toAutomaton(new ToMinimalDeterministicAutomaton()).store("matchU02FF").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchU02FF\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "\\u02ff"));
	}

	private List<String> reverse(List<String> words) {
		List<String> reverse = new ArrayList<String>(words.size());
		for (String word : words) {
			reverse.add(StringUtils.reverse(word));
		}
		return reverse;
	}

}
