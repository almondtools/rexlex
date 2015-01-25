package com.almondtools.relex.automaton;

import static com.almondtools.relex.automaton.Automatons.assertMatches;
import static com.almondtools.relex.automaton.Automatons.chars;
import static com.almondtools.relex.automaton.Automatons.invalid1;
import static com.almondtools.relex.automaton.Automatons.invalid2;
import static com.almondtools.relex.automaton.Automatons.invalid3;
import static com.almondtools.relex.automaton.Automatons.invalid4;
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
import static com.almondtools.relex.automaton.Automatons.valid8;
import static com.almondtools.relex.automaton.Automatons.valid9;
import static com.almondtools.relex.automaton.DFADeterministicMatcher.isDeterministic;
import static com.almondtools.relex.automaton.GenericAutomatonMatcher.matchesAutomaton;
import static com.almondtools.relex.pattern.DotGraphMatcher.startsWith;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.match;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchConcatenation;
import static com.almondtools.relex.automaton.GenericAutomatonBuilder.matchStarLoop;
import static com.almondtools.relex.pattern.DefaultTokenType.ACCEPT;
import static com.almondtools.relex.pattern.DefaultTokenType.ERROR;
import static com.almondtools.relex.tokens.Accept.A;
import static com.almondtools.relex.tokens.Accept.REMAINDER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import com.almondtools.relex.automaton.GenericAutomatonBuilder;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToCompactGenericAutomaton;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToDeterministicAutomaton;
import com.almondtools.relex.automaton.FromGenericAutomaton.ToMinimalDeterministicAutomaton;
import com.almondtools.relex.automaton.GenericAutomaton.EpsilonTransition;
import com.almondtools.relex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.relex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.relex.automaton.GenericAutomaton.State;
import com.almondtools.relex.automaton.GenericAutomaton.Transition;
import com.almondtools.relex.pattern.DefaultTokenType;
import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.pattern.RemainderTokenFactory;
import com.almondtools.relex.tokens.Accept;
import com.almondtools.relex.tokens.TestToken;
import com.almondtools.relex.tokens.TestTokenFactory;

public class GenericAutomatonTest {

	private TestTokenFactory factory;

	@Before
	public void before() {
		this.factory = new TestTokenFactory();
	}

	@Test
	public void testSimpleClosure() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new EpsilonTransition(s2));
		s1.addTransition(new EpsilonTransition(s3));
		assertThat(s1.getClosure(), hasItems(s2, s3));
	}

	@Test
	public void testTransitiveClosure() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new EpsilonTransition(s2));
		s2.addTransition(new EpsilonTransition(s3));
		assertThat(s1.getClosure(), hasItems(s2, s3));
	}

	@Test
	public void testCyclicClosure() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new EpsilonTransition(s2));
		s2.addTransition(new EpsilonTransition(s3));
		s3.addTransition(new EpsilonTransition(s1));
		assertThat(s1.getClosure(), hasItems(s1, s2, s3));
	}

	@Test
	public void testSimpleTransitionClosure() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new EpsilonTransition(s2));
		s2.addTransition(new EpsilonTransition(s3));
		ExactTransition t3 = new ExactTransition('a', s1);
		s3.addTransition(t3);
		assertThat(s1.nexts('a'), hasItem(t3));
	}

	@Test
	public void testSimpleMakeEpsilonFree() throws Exception {
		State s1 = new State();
		State s2 = new State(DefaultTokenType.ACCEPT);
		s1.addTransition(new EpsilonTransition(s2));
		GenericAutomaton a = new GenericAutomaton(s1);
		assertThat(a, matchesAutomaton().containsTransition(NFATransitionMatcher.forClass(EpsilonTransition.class)));
		GenericAutomaton efree = a.toAutomaton(new ToCompactGenericAutomaton());
		assertTrue(efree.getStart().accept());
		assertThat(efree, not(matchesAutomaton().containsTransition(NFATransitionMatcher.forClass(EpsilonTransition.class))));
	}

	@Test
	public void testComplexMakeEpsilonFree() throws Exception {
		GenericAutomaton a = match('a');
		GenericAutomaton b = match('b');
		GenericAutomaton ab = matchConcatenation(a, b);
		assertThat(ab, matchesAutomaton().containsTransition(NFATransitionMatcher.forClass(EpsilonTransition.class)));
		GenericAutomaton efree = ab.toAutomaton(new ToCompactGenericAutomaton());
		assertThat(efree, not(matchesAutomaton().containsTransition(NFATransitionMatcher.forClass(EpsilonTransition.class))));
	}

	@Test
	public void testComplexWithDoubleEpsilonConcatenatedStarLoopMakeEpsilonFree() throws Exception {
		GenericAutomaton a = match('a');
		GenericAutomaton space = createEpsilonAutomaton();
		GenericAutomaton astar = matchStarLoop(match('a'));
		GenericAutomaton aastar = matchConcatenation(a, space, astar);
		assertThat(matchSamples(aastar,"a"), contains("a"));
		assertThat(matchSamples(aastar,"aa"), contains("aa"));
		assertThat(matchSamples(aastar,"aaa"), contains("aaa"));
		GenericAutomaton efree = aastar.toAutomaton(new ToCompactGenericAutomaton());
		assertThat(matchSamples(efree,"a"), contains("a"));
		assertThat(matchSamples(efree,"aa"), contains("aa"));
		assertThat(matchSamples(efree,"aaa"), contains("aaa"));
	}

	private GenericAutomaton createEpsilonAutomaton() {
		State startspace = new State();
		State endspace = new State(DefaultTokenType.IGNORE);
		startspace.addTransition(new EpsilonTransition(endspace));
		GenericAutomaton space = new GenericAutomaton(startspace);
		return space;
	}

	@Test
	public void testVeryComplexMakeEpsilonFree() throws Exception {
		State s7486844 = new State();
		State s605645 = new State();
		State s12097592 = new State();
		State s17933228 = new State();
		State s11025290 = new State();
		State s17870931 = new State();
		State s14491894 = new State();
		State s27196165 = new State();
		State s2279771 = new State(DefaultTokenType.ACCEPT);
		State s30518483 = new State();
		State s11108810 = new State(DefaultTokenType.ACCEPT);

		s7486844.addTransition(new EpsilonTransition(s605645));
		s605645.addTransition(new EpsilonTransition(s12097592));
		s605645.addTransition(new EpsilonTransition(s17933228));
		s12097592.addTransition(new EpsilonTransition(s17933228));
		s12097592.addTransition(new EpsilonTransition(s17870931));
		s17933228.addTransition(new ExactTransition('a', s11025290));
		s11025290.addTransition(new EpsilonTransition(s12097592));
		s17870931.addTransition(new ExactTransition('a', s14491894));
		s14491894.addTransition(new EpsilonTransition(s27196165));
		s27196165.addTransition(new EpsilonTransition(s2279771));
		s2279771.addTransition(new EpsilonTransition(s30518483));
		s30518483.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, s11108810));
		s11108810.addTransition(new EpsilonTransition(s2279771));

		GenericAutomaton a = new GenericAutomaton(s7486844);
		assertThat(matchSamples(a,"a"), contains("a"));
		assertThat(matchSamples(a,"aa"), hasItem("aa"));
		GenericAutomaton efree = a.toAutomaton(new ToCompactGenericAutomaton());
		assertThat(matchSamples(efree,"a"), contains("a"));
		assertThat(matchSamples(efree,"aa"), hasItem("aa"));
	}

	@Test
	public void testNexts() throws Exception {
		State s1 = new State(Accept.A);
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new ExactTransition('a', s2));
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('a', s1));
		s3.addTransition(new ExactTransition('b', s1));
		assertThat(targets(s1.nexts('a')), hasItems(s2, s3));
		assertThat(targets(s2.nexts('a')), hasItems(s1));
		assertThat(targets(s3.nexts('a')).size(), equalTo(0));
	}

	@Test
	public void testNextsWithEpsilon() throws Exception {
		State s1 = new State(Accept.A);
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new ExactTransition('a', s2));
		s1.addTransition(new EpsilonTransition(s3));
		s2.addTransition(new ExactTransition('a', s1));
		s3.addTransition(new ExactTransition('b', s1));
		assertThat(targets(s1.nexts('a')), hasItems(s2));
		assertThat(targets(s1.nexts('b')), hasItems(s1));
		assertThat(targets(s2.nexts('a')), hasItems(s1));
		assertThat(targets(s3.nexts('b')), hasItems(s1));
	}

	@Test
	public void testNextsWithCollidingEpsilon() throws Exception {
		State s1 = new State(Accept.A);
		State s2 = new State();
		State s3 = new State();
		s1.addTransition(new ExactTransition('a', s2));
		s1.addTransition(new EpsilonTransition(s3));
		s2.addTransition(new ExactTransition('a', s1));
		s3.addTransition(new ExactTransition('a', s3));
		s3.addTransition(new ExactTransition('b', s1));
		assertThat(targets(s1.nexts('a')), hasItems(s2, s3));
		assertThat(targets(s1.nexts('b')), hasItems(s1));
		assertThat(targets(s2.nexts('a')), hasItems(s1));
		assertThat(targets(s3.nexts('b')), hasItems(s1));
	}

	@Test
	public void testToDeterministicAutomaton1() throws Exception {
		GenericAutomaton nondet1 = nfa1();
		DeterministicAutomaton det1 = nondet1.toAutomaton(new ToDeterministicAutomaton());
		assertThat(det1, isDeterministic());
		assertMatches(det1, valid1(), invalid1());
	}

	@Test
	public void testToDeterministicAutomaton2() throws Exception {
		GenericAutomaton nondet2 = nfa2();
		DeterministicAutomaton det2 = nondet2.toAutomaton(new ToDeterministicAutomaton());
		assertThat(det2, isDeterministic());
		assertMatches(det2, valid2(), invalid2());
	}

	@Test
	public void testToDeterministicAutomaton3() throws Exception {
		GenericAutomaton nondet3 = nfa3();
		DeterministicAutomaton det3 = nondet3.toAutomaton(new ToDeterministicAutomaton());
		assertThat(det3, isDeterministic());
		assertMatches(det3, valid3(), invalid3());
	}

	@Test
	public void testToDeterministicAutomaton4() throws Exception {
		GenericAutomaton nondet4 = nfa4();
		DeterministicAutomaton det4 = nondet4.toAutomaton(new ToDeterministicAutomaton());
		assertThat(det4, isDeterministic());
		assertMatches(det4, valid4(), invalid4());
	}

	@Test
	public void testNFATokenize5Posix() throws Exception {
		GenericAutomaton nondet5 = nfa5().totalize();
		Iterator<TestToken> tokens = nondet5.tokenize(chars("aaaabbaaaabaaaa"), factory);
		assertThat(tokens.next().getLiteral(), equalTo("aaaab"));
		assertThat(tokens.next().getLiteral(), equalTo("b"));
		assertThat(tokens.next().getLiteral(), equalTo("aaaabaaaa"));
	}

	@Test
	public void testPosix8() throws Exception {
		GenericAutomaton nondet8 = nfa8();
		assertMatches(nondet8, valid8(), invalid8());
		assertThat(matchPrefixes(nondet8, "aab"), hasItems("aab", "aa"));
	}

	@Test
	public void testPosix9() throws Exception {
		GenericAutomaton nondet9 = nfa9();
		assertMatches(nondet9, valid9(), invalid9());
		assertThat(matchPrefixes(nondet9, "aacaab"), hasItems("aacaab", "aa"));
	}

	@Test
	public void testSimpleRevert() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(DefaultTokenType.ACCEPT);
		s1.addTransition(new ExactTransition('a', s2));
		s2.addTransition(new ExactTransition('b', s3));

		GenericAutomaton a = new GenericAutomaton(s1);
		assertThat(matchSamples(a,"ab"), contains("ab"));
		assertThat(matchSamples(a,"ba"), empty());

		GenericAutomaton r = a.revert();
		assertThat(matchSamples(r,"ab"), empty());
		assertThat(matchSamples(r,"ba"), contains("ba"));
	}

	@Test
	public void testComplexRevert() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(DefaultTokenType.ACCEPT);
		State s4 = new State(DefaultTokenType.ACCEPT);
		s1.addTransition(new ExactTransition('a', s2));
		s1.addTransition(new ExactTransition('b', s3));
		s2.addTransition(new ExactTransition('b', s3));
		s2.addTransition(new EpsilonTransition(s4));

		GenericAutomaton a = new GenericAutomaton(s1);
		assertThat(matchSamples(a,"a"), contains("a"));
		assertThat(matchSamples(a,"b"), contains("b"));
		assertThat(matchSamples(a,"ab"), contains("ab"));
		assertThat(matchSamples(a,""), empty());

		GenericAutomaton r = a.revert();
		assertThat(matchSamples(r,"a"), contains("a"));
		assertThat(matchSamples(r,"b"), contains("b"));
		assertThat(matchSamples(r,"ba"), contains("ba"));
		assertThat(matchSamples(r,""), empty());
	}

	@Test
	public void testSimpleCyclicRevert() throws Exception {
		State s1 = new State(DefaultTokenType.ACCEPT);
		s1.addTransition(new ExactTransition('a', s1));

		GenericAutomaton a = new GenericAutomaton(s1);
		assertThat(matchSamples(a,""), contains(""));
		assertThat(matchSamples(a,"a"), contains("a"));
		assertThat(matchSamples(a,"aa"), contains("aa"));
		assertThat(matchSamples(a,"b"), empty());

		GenericAutomaton r = a.revert();
		assertThat(matchSamples(r,""), contains(""));
		assertThat(matchSamples(r,"a"), contains("a"));
		assertThat(matchSamples(r,"aa"), contains("aa"));
		assertThat(matchSamples(r,"b"), empty());
	}

	@Test
	public void testComplexCyclicRevert() throws Exception {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(DefaultTokenType.ACCEPT);
		State s4 = new State(DefaultTokenType.ACCEPT);
		s1.addTransition(new ExactTransition('a', s2));
		s1.addTransition(new ExactTransition('b', s3));
		s2.addTransition(new ExactTransition('b', s3));
		s2.addTransition(new EpsilonTransition(s4));
		s4.addTransition(new EpsilonTransition(s1));

		GenericAutomaton a = new GenericAutomaton(s1);
		assertThat(matchSamples(a,"a"), contains("a"));
		assertThat(matchSamples(a,"aa"), contains("aa"));
		assertThat(matchSamples(a,"aaa"), contains("aaa"));
		assertThat(matchSamples(a,"aaab"), contains("aaab"));
		assertThat(matchSamples(a,""), empty());
		assertThat(matchSamples(a,"aaaba"), empty());

		GenericAutomaton r = a.revert();
		assertThat(matchSamples(r,"a"), contains("a"));
		assertThat(matchSamples(r,"aa"), contains("aa"));
		assertThat(matchSamples(r,"aaa"), contains("aaa"));
		assertThat(matchSamples(r,"baaa"), contains("baaa"));
		assertThat(matchSamples(r,""), empty());
		assertThat(matchSamples(r,"abaaa"), empty());
	}

	@Test
	public void testGetIdWithoutStart() throws Exception {
		assertThat(new GenericAutomaton().getId(), equalTo("null"));
	}

	@Test
	public void testGetIdWithStart() throws Exception {
		State start = new State();
		String id = start.getId();
		assertThat(new GenericAutomaton(start).getId(), equalTo(id));
	}

	@Test
	public void testGetErrorTypeDefault() throws Exception {
		assertThat(new GenericAutomaton().getErrorType(), equalTo((TokenType) ERROR));
	}

	@Test
	public void testGetErrorTypeAfterTotalize() throws Exception {
		GenericAutomaton automaton = new GenericAutomaton(new State(), new RemainderTokenFactory(REMAINDER)).totalize();
		assertThat(automaton.getErrorType(), equalTo((TokenType) REMAINDER));
	}

	@Test
	public void testFailsInErrorState() throws Exception {
		GenericAutomaton automaton = new GenericAutomaton(new State(ERROR));
		assertThat(automaton.fails(), is(true));
	}

	@Test
	public void testFailsInOrphanState() throws Exception {
		GenericAutomaton automaton = new GenericAutomaton(new State());
		assertThat(automaton.fails(), is(true));
	}

	@Test
	public void testFailsNotInOrdinaryAutomaton() throws Exception {
		GenericAutomaton automaton = GenericAutomatonBuilder.match('a');
		assertThat(automaton.fails(), is(false));
	}

	@Test
	public void testFailsNotInTrivialAcceptor() throws Exception {
		GenericAutomaton automaton = new GenericAutomaton(new State(ACCEPT));
		assertThat(automaton.fails(), is(false));
	}

	@Test
	public void testAccepts() throws Exception {
		GenericAutomaton automaton = new GenericAutomaton(new State(ACCEPT));
		assertThat(automaton.accepts(), is(true));
	}

	@Test
	public void testAcceptsNotIfFails() throws Exception {
		assertThat(new GenericAutomaton(new State()).accepts(), is(false));
		assertThat(new GenericAutomaton(new State(ERROR)).accepts(), is(false));
	}

	@Test
	public void testTotalize() throws Exception {
		GenericAutomaton automaton = GenericAutomatonBuilder.match('a');
		assertThat(automaton.getStart().findReachableStates(), hasSize(2));
		GenericAutomaton totalized = automaton.totalize();
		assertThat(totalized.getErrorType(), equalTo((TokenType) ERROR));
		assertThat(totalized.getStart().findReachableStates(), hasSize(3));
	}

	@Test
	public void testTotalizeTokenType() throws Exception {
		GenericAutomaton automaton = GenericAutomatonBuilder.match('a');
		assertThat(automaton.getStart().findReachableStates(), hasSize(2));
		GenericAutomaton totalized = automaton.totalize(new RemainderTokenFactory(REMAINDER));
		assertThat(totalized.getErrorType(), equalTo((TokenType) REMAINDER));
		assertThat(totalized.getStart().findReachableStates(), hasSize(3));
	}

	@Test
	public void testToMinimalDeterministicAutomaton() throws Exception {
		GenericAutomaton automaton = GenericAutomatonBuilder.match('a');
		DeterministicAutomaton minimalDet = automaton.toAutomaton(new ToMinimalDeterministicAutomaton());
		assertThat(minimalDet.getErrorType(), equalTo((TokenType) ERROR));
	}

	@Test
	public void testToMinimalDeterministicAutomatonTokenType() throws Exception {
		GenericAutomaton automaton = GenericAutomatonBuilder.match('a');
		DeterministicAutomaton minimalDet = automaton.toAutomaton(new ToMinimalDeterministicAutomaton(REMAINDER));
		assertThat(minimalDet.getErrorType(), equalTo((TokenType) REMAINDER));
	}

	@Test
	public void testToCompactAutomaton() throws Exception {
		GenericAutomaton automaton = GenericAutomatonBuilder.match('a');
		GenericAutomaton compact = automaton.toAutomaton(new ToCompactGenericAutomaton());
		assertThat(compact.getErrorType(), equalTo((TokenType) ERROR));
		assertThat(matchSamples(compact,"a"), contains("a"));
		assertThat(matchSamples(compact,"b"), empty());
	}

	@Test
	public void testEliminateDuplicateFinalStates() throws Exception {
		State a1 = new State(A);
		State a2 = new State(A);
		State s = new State();
		s.addTransition(new ExactTransition('a', a1));
		s.addTransition(new ExactTransition('b', a2));
		GenericAutomaton automaton = new GenericAutomaton(s);
		GenericAutomaton compact = automaton.toAutomaton(new ToCompactGenericAutomaton());
		assertThat(compact.getErrorType(), equalTo((TokenType) ERROR));
		assertThat(compact.getStart().findReachableStates(), hasSize(2));
		assertThat(compact.getStart().findAcceptStates(), hasSize(1));
	}

	@Test
	public void testMatches() throws Exception {
		Automaton posix = Pattern.compileGenericAutomaton("ab*c|a.*c");
		assertThat(matchSamples(posix, "abbbbcc"), contains("abbbbcc"));
	}

	@Test
	public void testMatchesNot() throws Exception {
		Automaton automaton = Pattern.compileGenericAutomaton("a");
		assertThat(matchSamples(automaton, "b", "bc"), empty());
	}

	@Test
	public void testPrefixes() throws Exception {
		Automaton posix = Pattern.compileGenericAutomaton("one(self)?(selfsufficient)?");
		assertThat(matchPrefixes(posix, "oneselfsufficient next"), containsInAnyOrder("one", "oneself", "oneselfsufficient"));
	}

	@Test
	public void testStoreA() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a').store("matchA").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchA\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a"));
	}

	@Test
	public void testStoreAB() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a', 'b').store("matchAB").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchAB\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a-b"));
	}

	@Test
	public void testStoreAStar() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		matchStarLoop(match('a')).store("matchAStar").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchAStar\"")
			.withNodes(1, "circle")
			.withNodes(2, "doublecircle")
			.withArcs(2, "&epsilon;")
			.withArcs(1, "a"));
	}

	@Test
	public void testStoreATotalized() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match('a').totalize().store("matchA").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchA\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "a"));
	}

	@Test
	public void testStoreUnprintableCharTransition() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		match((char) 0x02FF).store("matchU02FF").to(out);
		String output = out.toString();
		assertThat(output, startsWith("digraph \"matchU02FF\"")
			.withNodes(1, "circle")
			.withNodes(1, "doublecircle")
			.withArcs(1, "\\u02ff"));
	}

	@Test
	public void testTotalizeAndCleanAndCleanDeadStates1() throws Exception {
		GenericAutomaton nondet1 = nfa1();
		GenericAutomaton det1 = nondet1.eliminateEpsilons().determinize();
		int accept = det1.findAcceptStates().size();
		GenericAutomaton cl1 = det1.totalizeAndClean();
		assertThat(cl1.findAcceptStates().size(), equalTo(accept));
		assertMatches(cl1, valid1(), invalid1());
		assertThat(cl1.findDeadStates().size(), lessThanOrEqualTo(1));
	}

	@Test
	public void testTotalizeAndCleanAndCleanDeadStates2() throws Exception {
		GenericAutomaton nondet2 = nfa2();
		GenericAutomaton det2 = nondet2.eliminateEpsilons().determinize();
		int accept = det2.findAcceptStates().size();
		GenericAutomaton cl2 = det2.totalizeAndClean();
		assertThat(cl2.findAcceptStates().size(), equalTo(accept));
		assertMatches(cl2, valid2(), invalid2());
		assertThat(cl2.findDeadStates().size(), lessThanOrEqualTo(1));
	}

	@Test
	public void testTotalizeAndCleanAndCleanDeadStates3() throws Exception {
		GenericAutomaton nondet3 = nfa3();
		GenericAutomaton det3 = nondet3.eliminateEpsilons().determinize();
		int accept = det3.findAcceptStates().size();
		GenericAutomaton cl3 = det3.totalizeAndClean();
		assertThat(cl3.findAcceptStates().size(), equalTo(accept));
		assertMatches(cl3, valid3(), invalid3());
		assertThat(cl3.findDeadStates().size(), lessThanOrEqualTo(1));
	}

	@Test
	public void testTotalizeAndCleanAndCleanDeadStates4() throws Exception {
		GenericAutomaton nondet4 = nfa4();
		GenericAutomaton det4 = nondet4.eliminateEpsilons().determinize();
		int accept = det4.findAcceptStates().size();
		GenericAutomaton cl4 = det4.totalizeAndClean();
		assertThat(cl4.findAcceptStates().size(), equalTo(accept));
		assertMatches(cl4, valid4(), invalid4());
		assertThat(cl4.findDeadStates().size(), lessThanOrEqualTo(1));
	}

	private List<State> targets(List<? extends Transition> nexts) {
		List<State> targets = new ArrayList<State>(nexts.size());
		for (Transition transition : nexts) {
			targets.add(transition.getTarget());
		}
		return targets;
	}

}
