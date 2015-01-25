package com.almondarts.relex.automaton;

import static com.almondarts.relex.automaton.Automatons.assertMatches;
import static com.almondarts.relex.automaton.Automatons.chars;
import static com.almondarts.relex.automaton.Automatons.invalid1;
import static com.almondarts.relex.automaton.Automatons.invalid2;
import static com.almondarts.relex.automaton.Automatons.invalid3;
import static com.almondarts.relex.automaton.Automatons.invalid4;
import static com.almondarts.relex.automaton.Automatons.invalid5;
import static com.almondarts.relex.automaton.Automatons.invalid8;
import static com.almondarts.relex.automaton.Automatons.invalid9;
import static com.almondarts.relex.automaton.Automatons.matchPrefixes;
import static com.almondarts.relex.automaton.Automatons.matchSamples;
import static com.almondarts.relex.automaton.Automatons.nfa1;
import static com.almondarts.relex.automaton.Automatons.nfa2;
import static com.almondarts.relex.automaton.Automatons.nfa3;
import static com.almondarts.relex.automaton.Automatons.nfa4;
import static com.almondarts.relex.automaton.Automatons.nfa5;
import static com.almondarts.relex.automaton.Automatons.nfa8;
import static com.almondarts.relex.automaton.Automatons.nfa9;
import static com.almondarts.relex.automaton.Automatons.valid1;
import static com.almondarts.relex.automaton.Automatons.valid2;
import static com.almondarts.relex.automaton.Automatons.valid3;
import static com.almondarts.relex.automaton.Automatons.valid4;
import static com.almondarts.relex.automaton.Automatons.valid5;
import static com.almondarts.relex.automaton.Automatons.valid8;
import static com.almondarts.relex.automaton.Automatons.valid9;
import static com.almondarts.relex.automaton.Automatons.withOutEmpty;
import static com.almondarts.relex.automaton.Automatons.withTokens;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.match;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchAlternatives;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchAnyChar;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchAnyOf;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchComplement;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchConcatenation;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchConjunctive;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchEmpty;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchFixedLoop;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchNothing;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchOptional;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchRangeLoop;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchStarLoop;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchUnlimitedLoop;
import static com.almondarts.relex.pattern.DefaultTokenType.ACCEPT;
import static com.almondarts.relex.pattern.DotGraphMatcher.startsWith;
import static com.almondarts.relex.tokens.Accept.A;
import static com.almondarts.relex.tokens.Accept.B;
import static com.almondarts.relex.tokens.Fail.TESTERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.almondarts.relex.TokenType;
import com.almondarts.relex.automaton.DeterministicAutomaton.State;
import com.almondarts.relex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondarts.relex.pattern.DefaultTokenType;
import com.almondarts.relex.pattern.Pattern;
import com.almondarts.relex.tokens.TestTokenFactory;

public class TabledAutomatonTest {

	private static final int START = 0;
	private static final int ERROR = 1;

	private TestTokenFactory factory;

	@Before
	public void before() {
		this.factory = new TestTokenFactory();
	}

	@Test
	public void testFindPathToStartState() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(a.findPathTo(a.getStartState()), equalTo(""));
	}

	@Test
	public void testFindPathToInvalidState() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(a.findPathTo(-2), nullValue());
	}

	@Test
	public void testFindPathToTransitiveState() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(a.findPathTo(a.next(a.getStartState(), 'a')), equalTo("a"));
	}
	
	@Test
	public void testFindPathToTransitiveStateShortest() throws Exception {
		TabledAutomaton a = matchUnlimitedLoop(match('a'), 1).toAutomaton(new ToTabledAutomaton());
		int start = a.getStartState();
		int stateA = a.next(start, 'a');
		int stateAA = a.next(stateA, 'a');
		assertThat(a.findPathTo(stateA), equalTo("a"));
		assertThat(a.findPathTo(stateAA), equalTo("a"));
	}
	
	@Test
	public void testFindStateStartState() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(a.findState(""), equalTo(a.getStartState()));
	}

	@Test
	public void testFindStateErrorState() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(a.findState("b"), equalTo(a.getErrorState()));
	}

	@Test
	public void testFindStateAcceptState() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(a.getType(a.findState("a")), equalTo((TokenType)ACCEPT));
	}

	@Test
	public void testFindStateFindPath() throws Exception {
		TabledAutomaton a = matchAlternatives(match('a'), match("ab")).toAutomaton(new ToTabledAutomaton());
		int stateA = a.findState("a");
		int stateAB = a.findState("ab");
		assertThat(a.findPathTo(stateA), equalTo("a"));
		assertThat(a.findPathTo(stateAB), equalTo("ab"));
	}

	@Test
	public void testMatchChar() throws Exception {
		TabledAutomaton a = match('a').toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a,"a"), contains("a"));
		assertThat(matchSamples(a,"aa"), empty());
		assertThat(matchSamples(a,"b"), empty());
		assertThat(matchSamples(a,""), empty());
	}

	@Test
	public void testMatchString() throws Exception {
		TabledAutomaton abc = match("abc").toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,"abc"), contains("abc"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"bc"), empty());
		assertThat(matchSamples(abc,"ac"), empty());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"abcd"), empty());
	}

	@Test
	public void testMatchCharRange() throws Exception {
		TabledAutomaton abc = match('a', 'c').toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"b"), contains("b"));
		assertThat(matchSamples(abc,"c"), contains("c"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"bc"), empty());
		assertThat(matchSamples(abc,"ac"), empty());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"abcd"), empty());
	}

	@Test
	public void testMatchReverseCharRange() throws Exception {
		TabledAutomaton abc = match('c', 'a').toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"b"), contains("b"));
		assertThat(matchSamples(abc,"c"), contains("c"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"bc"), empty());
		assertThat(matchSamples(abc,"ac"), empty());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"abcd"), empty());
	}

	@Test
	public void testMatchAnyChar() throws Exception {
		TabledAutomaton abc = matchAnyChar().toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"z"), contains("z"));
		assertThat(matchSamples(abc,"1"), contains("1"));
		assertThat(matchSamples(abc,"&"), contains("&"));
		assertThat(matchSamples(abc,"\n"), contains("\n"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"aa"), empty());
		assertThat(matchSamples(abc,"a\n"), empty());
		assertThat(matchSamples(abc,"a&b"), empty());
		assertThat(matchSamples(abc,""), empty());
	}

	@Test
	public void testMatchAnyCharOf() throws Exception {
		TabledAutomaton abc = matchAnyOf('a', 'b').toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"b"), contains("b"));
		assertThat(matchSamples(abc,"c"), empty());
		assertThat(matchSamples(abc,"cc"), empty());
		assertThat(matchSamples(abc,""), empty());
	}

	@Test
	public void testMatchNothing() throws Exception {
		TabledAutomaton abc = matchNothing().toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"a"), empty());
		assertThat(matchSamples(abc,"ab"), empty());
	}

	@Test
	public void testMatchEmpty() throws Exception {
		TabledAutomaton abc = matchEmpty().toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(abc,""), contains(""));
		assertThat(matchSamples(abc,"a"), empty());
		assertThat(matchSamples(abc,"ab"), empty());
	}

	@Test
	public void testOptional() throws Exception {
		TabledAutomaton ab_question = matchOptional(match("ab")).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(ab_question,""), contains(""));
		assertThat(matchSamples(ab_question,"ab"), contains("ab"));
		assertThat(matchSamples(ab_question,"a"), empty());
		assertThat(matchSamples(ab_question,"b"), empty());
		assertThat(matchSamples(ab_question,"abc"), empty());
	}

	@Test
	public void testUnlimitedLoop0() throws Exception {
		TabledAutomaton a_star = matchUnlimitedLoop(match('a'), 0).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_star,""), contains(""));
		assertThat(matchSamples(a_star,"a"), contains("a"));
		assertThat(matchSamples(a_star,"aa"), contains("aa"));
		assertThat(matchSamples(a_star,"aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_star,"ab"), empty());
	}

	@Test
	public void testUnlimitedLoop1() throws Exception {
		TabledAutomaton a_plus = matchUnlimitedLoop(match('a'), 1).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_plus,"a"), contains("a"));
		assertThat(matchSamples(a_plus,"aa"), contains("aa"));
		assertThat(matchSamples(a_plus,"aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_plus,""), empty());
		assertThat(matchSamples(a_plus,"ab"), empty());
	}

	@Test
	public void testUnlimitedLoopN() throws Exception {
		TabledAutomaton a_minN = matchUnlimitedLoop(match('a'), 4).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_minN,"aaaa"), contains("aaaa"));
		assertThat(matchSamples(a_minN,"aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_minN,""), empty());
		assertThat(matchSamples(a_minN,"aaa"), empty());
		assertThat(matchSamples(a_minN,"ab"), empty());
	}

	@Test
	public void testRangeLoop() throws Exception {
		TabledAutomaton a_1_2 = matchRangeLoop(match('a'), 1, 2).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_1_2,"a"), contains("a"));
		assertThat(matchSamples(a_1_2,"aa"), contains("aa"));
		assertThat(matchSamples(a_1_2,""), empty());
		assertThat(matchSamples(a_1_2,"aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_1_2,"ab"), empty());
	}

	@Test
	public void testBroadRangeLoop() throws Exception {
		TabledAutomaton a_2_4 = matchRangeLoop(match('a'), 2, 4).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_2_4,"aa"), contains("aa"));
		assertThat(matchSamples(a_2_4,"aaa"), contains("aaa"));
		assertThat(matchSamples(a_2_4,"aaaa"), contains("aaaa"));
		assertThat(matchSamples(a_2_4,""), empty());
		assertThat(matchSamples(a_2_4,"aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_2_4,"ab"), empty());
	}

	@Test
	public void testFixedRangeLoop() throws Exception {
		TabledAutomaton a_1_1 = matchRangeLoop(match('a'), 1, 1).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_1_1,"a"), contains("a"));
		assertThat(matchSamples(a_1_1,"aa"), empty());
		assertThat(matchSamples(a_1_1,""), empty());
		assertThat(matchSamples(a_1_1,"aaaaaaaaaaaaaaaaaaaaa"), empty());
	}

	@Test
	public void testFixedLoop() throws Exception {
		TabledAutomaton a_2 = matchFixedLoop(match('a'), 2).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(a_2,"aa"), contains("aa"));
		assertThat(matchSamples(a_2,""), empty());
		assertThat(matchSamples(a_2,"a"), empty());
		assertThat(matchSamples(a_2,"aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_2,"ab"), empty());
	}

	@Test
	public void testMatchConcatenation() throws Exception {
		TabledAutomaton aAndb = matchConcatenation(match('a'), match('b')).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(aAndb,"ab"), contains("ab"));
		assertThat(matchSamples(aAndb,""), empty());
		assertThat(matchSamples(aAndb,"a"), empty());
		assertThat(matchSamples(aAndb,"b"), empty());
		assertThat(matchSamples(aAndb,"abab"), empty());
	}

	@Test
	public void testMatchAlternatives() throws Exception {
		TabledAutomaton aOrb = matchAlternatives(match('a'), match('b')).toAutomaton(new ToTabledAutomaton());
		assertThat(matchSamples(aOrb,"a"), contains("a"));
		assertThat(matchSamples(aOrb,"b"), contains("b"));
		assertThat(matchSamples(aOrb,""), empty());
		assertThat(matchSamples(aOrb,"ab"), empty());
		assertThat(matchSamples(aOrb,"abab"), empty());
	}

	@Test
	public void testTabled1() throws Exception {
		TabledAutomaton det1 = withTokens(nfa1()).toAutomaton(new ToTabledAutomaton());
		assertMatches(det1, valid1(), invalid1());
		assertTokenizes(det1, valid1());
	}

	@Test
	public void testTabled2() throws Exception {
		TabledAutomaton det2 = withTokens(nfa2()).toAutomaton(new ToTabledAutomaton());
		assertMatches(det2, valid2(), invalid2());
		assertTokenizes(det2, valid2());
	}

	@Test
	public void testTabled3() throws Exception {
		TabledAutomaton det3 = withTokens(nfa3()).toAutomaton(new ToTabledAutomaton());
		assertMatches(det3, valid3(), invalid3());
		assertTokenizes(det3, valid3());
	}

	@Test
	public void testTabled4() throws Exception {
		TabledAutomaton det4 = withTokens(nfa4()).toAutomaton(new ToTabledAutomaton());
		assertMatches(det4, valid4(), invalid4());
		assertTokenizes(det4, valid4());
	}

	@Test
	public void testTabled5() throws Exception {
		TabledAutomaton det5 = withTokens(nfa5()).toAutomaton(new ToTabledAutomaton());
		assertMatches(det5, valid5(), invalid5());
		assertTokenizes(det5, valid5());
	}

	@Test
	public void testFind8() throws Exception {
		GenericAutomaton nondet8 = nfa8();
		TabledAutomaton det8 = nondet8.toAutomaton(new ToTabledAutomaton());
		assertMatches(det8, valid8(), invalid8());
		assertThat(matchPrefixes(det8, "aab"), containsInAnyOrder("aab", "aa"));
	}
	
	@Test
	public void testFind9() throws Exception {
		GenericAutomaton nondet9 = nfa9();
		TabledAutomaton det9 = nondet9.toAutomaton(new ToTabledAutomaton());
		assertMatches(det9, valid9(), invalid9());
		assertThat(matchPrefixes(det9, "aacaab"), containsInAnyOrder("aacaa", "aacaab", "aa"));
	}

	@Test
	public void testGetStartStateOnOrdinaryState() throws Exception {
		State start = new State();
		State next = new State(A);
		State error = new State(TESTERROR);
		start.addTransition(new DeterministicAutomaton.ExactTransition('a', next));
		start.addErrorTransitions(error);
		TabledAutomaton automaton = new TabledAutomaton(new char[] { 'a', 'b' }, start, error);
		assertThat(automaton.getStartState(), equalTo(START));
	}

	@Test
	public void testGetStartStateOnNonmatching() throws Exception {
		State error = new State(TESTERROR);
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, error, error);
		assertThat(automaton.getStartState(), equalTo(ERROR));
	}

	@Test
	public void testGetTransitionsOnOrdinaryState() throws Exception {
		State start = new State(); // -> 0
		State next = new State(A); // -> 2
		State error = new State(TESTERROR); // -> 1
		start.addTransition(new DeterministicAutomaton.ExactTransition('a', next));
		start.addErrorTransitions(error);
		TabledAutomaton automaton = new TabledAutomaton(new char[] { 'a', 'b' }, start, error);
		assertThat(automaton.getTransitions().length, equalTo(3 * 2));
		assertThat(automaton.getCharClassCount(), equalTo(2));
		assertThat(automaton.getTarget(0,0), equalTo(2));
		assertThat(automaton.getTarget(0,1), equalTo(1));
		assertThat(automaton.getTarget(1,0), equalTo(1));
		assertThat(automaton.getTarget(1,1), equalTo(1));
		assertThat(automaton.getTarget(2,0), equalTo(1));
		assertThat(automaton.getTarget(2,1), equalTo(1));
	}

	@Test
	public void testGetTransitionsOnOrdinaryStateWithMoreTransitions() throws Exception {
		State start = new State(); // -> 0
		State nexta = new State(A); // -> 2
		State nextb = new State(B); // -> 3
		State error = new State(TESTERROR); // -> 1
		start.addTransition(new DeterministicAutomaton.ExactTransition('a', nexta));
		start.addTransition(new DeterministicAutomaton.ExactTransition('b', nextb));
		start.addErrorTransitions(error);
		TabledAutomaton automaton = new TabledAutomaton(new char[] { 'a', 'b', 'c' }, start, error);
		assertThat(automaton.getTransitions().length, equalTo(4 * 3));
		assertThat(automaton.getCharClassCount(), equalTo(3));
		assertThat(automaton.getTarget(0,0), equalTo(2));
		assertThat(automaton.getTarget(0,1), equalTo(3));
		assertThat(automaton.getTarget(0,2), equalTo(1));
		assertThat(automaton.getTarget(1,0), equalTo(1));
		assertThat(automaton.getTarget(1,1), equalTo(1));
		assertThat(automaton.getTarget(2,0), equalTo(1));
		assertThat(automaton.getTarget(2,1), equalTo(1));
		assertThat(automaton.getTarget(3,0), equalTo(1));
		assertThat(automaton.getTarget(3,1), equalTo(1));
	}

	@Test
	public void testGetTransitionsOnNonmatching() throws Exception {
		State error = new State(TESTERROR);
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, error, error);
		assertThat(automaton.getTransitions().length, equalTo(1 * 0));
		assertThat(automaton.getCharClassCount(), equalTo(0));
	}

	@Test
	public void testGetAccept() throws Exception {
		State start = new State();
		State next = new State(A);
		State error = new State(TESTERROR);
		start.addTransition(new DeterministicAutomaton.ExactTransition('a', next));
		start.addErrorTransitions(error);
		TabledAutomaton automaton = new TabledAutomaton(new char[] { 'a', 'b' }, start, error);
		TokenType[] accept = automaton.getAccept();
		assertThat(accept[0], nullValue());
		assertThat(accept[1], equalTo((TokenType) TESTERROR));
		assertThat(accept[2], equalTo((TokenType) A));
	}

	@Test
	public void testGetRelevantCharsEmpty() throws Exception {
		State error = new State(TESTERROR);
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, error, error);
		assertThat(automaton.getCharClassCount(), equalTo(0));
	}

	@Test
	public void testGetRelevantChars() throws Exception {
		State error = new State(TESTERROR);
		TabledAutomaton automaton = new TabledAutomaton(new char[] { 'a' }, error, error);
		assertThat(automaton.getCharClassCount(), equalTo(1));
	}

	@Test
	public void testGetErrorType() throws Exception {
		State start = new State();
		State error = new State(TESTERROR);
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, start, error);
		assertThat(automaton.getErrorType(), equalTo((TokenType) TESTERROR));
	}

	@Test
	public void testGetDefaultErrorType() throws Exception {
		State start = new State();
		State error = new State();
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, start, error);
		assertThat(automaton.getErrorType(), equalTo((TokenType) DefaultTokenType.ERROR));
	}

	@Test
	public void testGetDefaultErrorTypeNonmatching() throws Exception {
		State error = new State();
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, error, error);
		assertThat(automaton.getErrorType(), equalTo((TokenType) DefaultTokenType.ERROR));
	}

	@Test
	public void testGetIdOrdinary() throws Exception {
		State start = new State();
		State error = new State();
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, start, error);
		assertThat(automaton.getId(), equalTo("0"));
	}

	@Test
	public void testGetIdNonmatching() throws Exception {
		State error = new State();
		TabledAutomaton automaton = new TabledAutomaton(new char[] {}, error, error);
		assertThat(automaton.getId(), equalTo("1"));
	}

	@Test
	public void testMatches() throws Exception {
		Automaton posix = Pattern.compileAutomaton("ab*c|a.*c", new ToTabledAutomaton());
		assertThat(matchSamples(posix, "abbbbcc"), contains("abbbbcc"));
	}

	@Test
	public void testMatchesNot() throws Exception {
		Automaton automaton = Pattern.compileAutomaton("a", new ToTabledAutomaton());
		assertThat(matchSamples(automaton, "b", "bc"), empty());
	}

	@Test
	public void testPrefixes() throws Exception {
		Automaton posix = Pattern.compileAutomaton("one(self)?(selfsufficient)?", new ToTabledAutomaton());
		assertThat(matchPrefixes(posix, "oneselfsufficient next"), containsInAnyOrder("one", "oneself", "oneselfsufficient"));
	}

	@Test
	public void testStoreA() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a').toAutomaton(new ToTabledAutomaton()).store("matchA").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchA\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a"));
	}

	@Test
	public void testStoreAB() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a', 'b').toAutomaton(new ToTabledAutomaton()).store("matchAB").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchAB\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a-b"));
	}

	@Test
	public void testStoreABcomp() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		matchConjunctive(matchComplement(match('a', 'b')), matchAnyChar()).toAutomaton(new ToTabledAutomaton()).store("matchABcomp").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchABcomp\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "\\u0000-" + (char) ('a' - 1))
			.withArcs(1, "c-\\uffff"));
	}

	@Test
	public void testStoreAStar() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		matchStarLoop(match('a')).toAutomaton(new ToTabledAutomaton()).store("matchAStar").to(out);
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
		match((char) 0x02FF).toAutomaton(new ToTabledAutomaton()).store("matchU02FF").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchU02FF\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "\\u02ff"));
	}

	public void assertTokenizes(TabledAutomaton det, List<String> valid) {
		valid = withOutEmpty(valid);
		assertThat(det.tokenize(chars(valid.get(0)), factory).next().getLiteral(), equalTo(valid.get(0)));
	}

}
