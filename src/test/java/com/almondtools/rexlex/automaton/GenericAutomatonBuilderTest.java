package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.automaton.Automatons.matchSamples;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.atLeastOne;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.match;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchAllPrefixes;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchAlternatives;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchAnyChar;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchAnyOf;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchComplement;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchConcatenation;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchConjunctive;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchEmpty;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchFixedLoop;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchNothing;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchOptional;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchRangeLoop;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchStarLoop;
import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchUnlimitedLoop;
import static com.almondtools.rexlex.pattern.DefaultTokenType.ACCEPT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.almondtools.rexlex.automaton.Automaton;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomatonBuilder;
import com.almondtools.rexlex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.pattern.DefaultTokenType;

public class GenericAutomatonBuilderTest {

	@Test
	public void testMatchChar() throws Exception {
		GenericAutomaton a = match('a');

		assertThat(matchSamples(a, "a"), contains("a"));
		assertThat(matchSamples(a, "aa"), empty());
		assertThat(matchSamples(a, "b"), empty());
		assertThat(matchSamples(a, ""), empty());
	}

	@Test
	public void testMatchString() throws Exception {
		GenericAutomaton abc = match("abc");
		assertThat(matchSamples(abc, "abc"), contains("abc"));
		assertThat(matchSamples(abc, "ab"), empty());
		assertThat(matchSamples(abc, "bc"), empty());
		assertThat(matchSamples(abc, "ac"), empty());
		assertThat(matchSamples(abc, ""), empty());
		assertThat(matchSamples(abc, "abcd"), empty());
	}

	@Test
	public void testMatchCharRange() throws Exception {
		GenericAutomaton abc = match('a', 'c');
		assertThat(matchSamples(abc, "a"), contains("a"));
		assertThat(matchSamples(abc, "b"), contains("b"));
		assertThat(matchSamples(abc, "c"), contains("c"));
		assertThat(matchSamples(abc, "ab"), empty());
		assertThat(matchSamples(abc, "bc"), empty());
		assertThat(matchSamples(abc, "ac"), empty());
		assertThat(matchSamples(abc, ""), empty());
		assertThat(matchSamples(abc, "abcd"), empty());
	}

	@Test
	public void testMatchReverseCharRange() throws Exception {
		GenericAutomaton abc = match('c', 'a');
		assertThat(matchSamples(abc, "a"), contains("a"));
		assertThat(matchSamples(abc, "b"), contains("b"));
		assertThat(matchSamples(abc, "c"), contains("c"));
		assertThat(matchSamples(abc, "ab"), empty());
		assertThat(matchSamples(abc, "bc"), empty());
		assertThat(matchSamples(abc, "ac"), empty());
		assertThat(matchSamples(abc, ""), empty());
		assertThat(matchSamples(abc, "abcd"), empty());
	}

	@Test
	public void testMatchAnyChar() throws Exception {
		GenericAutomaton abc = matchAnyChar();
		assertThat(matchSamples(abc, "a"), contains("a"));
		assertThat(matchSamples(abc, "z"), contains("z"));
		assertThat(matchSamples(abc, "1"), contains("1"));
		assertThat(matchSamples(abc, "&"), contains("&"));
		assertThat(matchSamples(abc, "\n"), contains("\n"));
		assertThat(matchSamples(abc, "ab"), empty());
		assertThat(matchSamples(abc, "aa"), empty());
		assertThat(matchSamples(abc, "a\n"), empty());
		assertThat(matchSamples(abc, "a&b"), empty());
		assertThat(matchSamples(abc, ""), empty());
	}

	@Test
	public void testMatchAnyCharOf() throws Exception {
		GenericAutomaton abc = matchAnyOf('a', 'b');
		assertThat(matchSamples(abc, "a"), contains("a"));
		assertThat(matchSamples(abc, "b"), contains("b"));
		assertThat(matchSamples(abc, "c"), empty());
		assertThat(matchSamples(abc, "cc"), empty());
		assertThat(matchSamples(abc, ""), empty());
	}

	@Test
	public void testMatchNothing() throws Exception {
		GenericAutomaton abc = matchNothing();
		assertThat(matchSamples(abc, ""), empty());
		assertThat(matchSamples(abc, "a"), empty());
		assertThat(matchSamples(abc, "ab"), empty());
	}

	@Test
	public void testMatchEmpty() throws Exception {
		GenericAutomaton abc = matchEmpty();
		assertThat(matchSamples(abc, ""), contains(""));
		assertThat(matchSamples(abc, "a"), empty());
		assertThat(matchSamples(abc, "ab"), empty());
	}

	@Test
	public void testOptional() throws Exception {
		GenericAutomaton ab_question = matchOptional(match("ab"));
		assertThat(matchSamples(ab_question, ""), contains(""));
		assertThat(matchSamples(ab_question, "ab"), contains("ab"));
		assertThat(matchSamples(ab_question, "a"), empty());
		assertThat(matchSamples(ab_question, "b"), empty());
		assertThat(matchSamples(ab_question, "abc"), empty());
	}

	@Test
	public void testUnlimitedLoop0() throws Exception {
		GenericAutomaton a_star = matchUnlimitedLoop(match('a'), 0);
		assertThat(matchSamples(a_star, ""), contains(""));
		assertThat(matchSamples(a_star, "a"), contains("a"));
		assertThat(matchSamples(a_star, "aa"), contains("aa"));
		assertThat(matchSamples(a_star, "aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_star, "ab"), empty());
	}

	@Test
	public void testUnlimitedLoop1() throws Exception {
		GenericAutomaton a_plus = matchUnlimitedLoop(match('a'), 1);
		assertThat(matchSamples(a_plus, "a"), contains("a"));
		assertThat(matchSamples(a_plus, "aa"), contains("aa"));
		assertThat(matchSamples(a_plus, "aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_plus, ""), empty());
		assertThat(matchSamples(a_plus, "ab"), empty());
	}

	@Test
	public void testUnlimitedLoopN() throws Exception {
		GenericAutomaton a_minN = matchUnlimitedLoop(match('a'), 4);
		assertThat(matchSamples(a_minN, "aaaa"), contains("aaaa"));
		assertThat(matchSamples(a_minN, "aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_minN, ""), empty());
		assertThat(matchSamples(a_minN, "aaa"), empty());
		assertThat(matchSamples(a_minN, "ab"), empty());
	}

	@Test
	public void testRangeLoop() throws Exception {
		GenericAutomaton a_1_2 = matchRangeLoop(match('a'), 1, 2);
		assertThat(matchSamples(a_1_2, "a"), contains("a"));
		assertThat(matchSamples(a_1_2, "aa"), contains("aa"));
		assertThat(matchSamples(a_1_2, ""), empty());
		assertThat(matchSamples(a_1_2, "aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_1_2, "ab"), empty());
	}

	@Test
	public void testBroadRangeLoop() throws Exception {
		GenericAutomaton a_2_4 = matchRangeLoop(match('a'), 2, 4);
		assertThat(matchSamples(a_2_4, "aa"), contains("aa"));
		assertThat(matchSamples(a_2_4, "aaa"), contains("aaa"));
		assertThat(matchSamples(a_2_4, "aaaa"), contains("aaaa"));
		assertThat(matchSamples(a_2_4, ""), empty());
		assertThat(matchSamples(a_2_4, "aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_2_4, "ab"), empty());
	}

	@Test
	public void testFixedRangeLoop() throws Exception {
		GenericAutomaton a_1_1 = matchRangeLoop(match('a'), 1, 1);
		assertThat(matchSamples(a_1_1, "a"), contains("a"));
		assertThat(matchSamples(a_1_1, "aa"), empty());
		assertThat(matchSamples(a_1_1, ""), empty());
		assertThat(matchSamples(a_1_1, "aaaaaaaaaaaaaaaaaaaaa"), empty());
	}

	@Test
	public void testMatchFixedLoop() throws Exception {
		GenericAutomaton a_2 = matchFixedLoop(match('a'), 2);
		assertThat(matchSamples(a_2, "aa"), contains("aa"));
		assertThat(matchSamples(a_2, ""), empty());
		assertThat(matchSamples(a_2, "a"), empty());
		assertThat(matchSamples(a_2, "aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_2, "ab"), empty());
	}

	@Test
	public void testMatchConcatenation() throws Exception {
		GenericAutomaton aAndb = matchConcatenation(match('a'), match('b'));
		assertThat(matchSamples(aAndb, "ab"), contains("ab"));
		assertThat(matchSamples(aAndb, ""), empty());
		assertThat(matchSamples(aAndb, "a"), empty());
		assertThat(matchSamples(aAndb, "b"), empty());
		assertThat(matchSamples(aAndb, "abab"), empty());
	}

	@Test
	public void testMatchAlternatives() throws Exception {
		GenericAutomaton aOrb = matchAlternatives(match('a'), match('b'));
		assertThat(matchSamples(aOrb, "a"), contains("a"));
		assertThat(matchSamples(aOrb, "b"), contains("b"));
		assertThat(matchSamples(aOrb, ""), empty());
		assertThat(matchSamples(aOrb, "ab"), empty());
		assertThat(matchSamples(aOrb, "abab"), empty());
	}

	@Test
	public void testMatchAlternativesOne() throws Exception {
		GenericAutomaton aOrb = matchAlternatives(match('a'));
		assertThat(matchSamples(aOrb, "a"), contains("a"));
		assertThat(matchSamples(aOrb, ""), empty());
		assertThat(matchSamples(aOrb, "b"), empty());
		assertThat(matchSamples(aOrb, "ab"), empty());
	}

	@Test
	public void testMatchConjunctiveEmptyIntersection() throws Exception {
		GenericAutomaton ei = matchConjunctive(match('a'), match('b'));
		assertThat(matchSamples(ei, ""), empty());
		assertThat(matchSamples(ei, "a"), empty());
		assertThat(matchSamples(ei, "b"), empty());
	}

	@Test
	public void testMatchConjunctiveOneCharIntersection() throws Exception {
		GenericAutomaton oci = matchConjunctive(match('a'), match('a'));
		assertThat(matchSamples(oci, "a"), contains("a"));
		assertThat(matchSamples(oci, ""), empty());
		assertThat(matchSamples(oci, "b"), empty());
	}

	@Test
	public void testMatchConjunctiveComplexIntersection() throws Exception {
		GenericAutomaton ci = matchConjunctive(matchStarLoop(match('a')), matchConcatenation(match('a'), matchOptional(match('b'))));
		assertThat(matchSamples(ci, "a"), contains("a"));
		assertThat(matchSamples(ci, ""), empty());
		assertThat(matchSamples(ci, "ab"), empty());
		assertThat(matchSamples(ci, "aa"), empty());
	}

	@Test
	public void testMatchSimpleComplement() throws Exception {
		GenericAutomaton ci = matchComplement(match('a'));
		assertThat(matchSamples(ci, ""), contains(""));
		assertThat(matchSamples(ci, "b"), contains("b"));
		assertThat(matchSamples(ci, "a"), empty());
	}

	@Test
	public void testMatchComplexComplement() throws Exception {
		GenericAutomaton ci = matchComplement(matchUnlimitedLoop(match('a'), 1));
		assertThat(matchSamples(ci, ""), contains(""));
		assertThat(matchSamples(ci, "b"), contains("b"));
		assertThat(matchSamples(ci, "ba"), contains("ba"));
		assertThat(matchSamples(ci, "bb"), contains("bb"));
		assertThat(matchSamples(ci, "a"), empty());
		assertThat(matchSamples(ci, "aa"), empty());
	}

	@Test
	public void testIntersectTagsEquivalentStates() {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(ACCEPT);
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('a', s3));
		State s12 = intersectStates(s1, s2);
		assertFalse(s12.accept());
		assertThat(s12.nexts('a').size(), equalTo(1));
		assertTrue(s12.nexts('a').iterator().next().getTarget().accept());
	}

	@Test
	public void testIntersectTagsDisjunctStates() {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(ACCEPT);
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('b', s3));
		State s12 = intersectStates(s1, s2);
		assertFalse(s12.accept());
		assertThat(s12.nexts('a').size(), equalTo(0));
		assertThat(s12.nexts('b').size(), equalTo(0));
	}

	@Test
	public void testIntersectTagsSubsumingStates() {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(ACCEPT);
		s1.addTransition(new ExactTransition('a', s1));
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('a', s3));
		State s12 = intersectStates(s1, s2);
		Automaton merged = new GenericAutomaton(s12);
		assertThat(matchSamples(merged, "a", "aa", "aaa"), contains("a"));
	}

	@Test
	public void testMergeEquivalentStates() {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(ACCEPT);
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('a', s3));
		State s12 = mergePrefixes(s1, s2);
		assertFalse(s12.accept());
		assertThat(s12.getTransitions().size(), equalTo(1));
		assertThat(s12.getClosure().size(), equalTo(1));
		assertThat(s12.getNextClosure().size(), equalTo(1));
		assertThat(s12.nexts('a').size(), equalTo(1));
		assertTrue(s12.nexts('a').iterator().next().getTarget().accept());
	}

	@Test
	public void testMergeDisjunctStates() {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(ACCEPT);
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('b', s3));
		State s12 = mergePrefixes(s1, s2);
		assertFalse(s12.accept());
		assertThat(s12.nexts('a').size(), equalTo(0));
		assertThat(s12.nexts('b').size(), equalTo(0));
		assertThat(s12.findAcceptStates().size(), equalTo(0));
	}

	@Test
	public void testMergeSubsumingStates() {
		State s1 = new State();
		State s2 = new State();
		State s3 = new State(ACCEPT);
		s1.addTransition(new ExactTransition('a', s1));
		s1.addTransition(new ExactTransition('a', s3));
		s2.addTransition(new ExactTransition('a', s3));
		State s12 = mergePrefixes(s1, s2);
		assertFalse(s12.accept());
		Automaton merged = new GenericAutomaton(s12);
		assertThat(matchSamples(merged, "a"), contains("a"));
		assertThat(matchSamples(merged, "aa"), contains("aa"));
		assertThat(matchSamples(merged, "aaa"), contains("aaa"));
	}

	@Test
	public void testDisjunctAnd() throws Exception {
		GenericAutomaton a = matchConjunctive(match('a'), match('b'));
		assertThat(matchSamples(a, "", "a", "b"), empty());
	}

	@Test
	public void testSubsumingAnd() throws Exception {
		GenericAutomaton a = matchConjunctive(matchStarLoop(match('a')), matchUnlimitedLoop(match('a'), 1));
		assertThat(matchSamples(a, "", "a", "aa"), contains("a", "aa"));
	}

	@Test
	public void testTrivialAnd() throws Exception {
		GenericAutomaton a = matchConjunctive(matchStarLoop(match('a')));
		assertThat(matchSamples(a, "", "a", "aa"), contains("", "a", "aa"));
	}

	@Test
	public void testPrefixAnd() throws Exception {
		GenericAutomaton a = matchAllPrefixes(matchStarLoop(match('a')), match("ab"));
		assertThat(matchSamples(a, "ab"), contains("ab"));
		assertThat(matchSamples(a, "a", "b"), empty());
	}

	@Test
	public void testPrefix() throws Exception {
		GenericAutomaton a = matchAllPrefixes(matchStarLoop(match('a')));
		assertThat(matchSamples(a, "", "a", "aa"), contains("", "a", "aa"));
	}

	@Test
	public void testComplement() throws Exception {
		GenericAutomaton a = matchComplement(match('a'));
		assertThat(matchSamples(a, "b", "a", ""), contains("b", ""));
	}

	@Test
	public void testComplementNotEmpty() throws Exception {
		GenericAutomaton a = matchComplement(matchAlternatives(match('a'), matchEmpty()));
		assertThat(matchSamples(a, "b", "a", ""), contains("b"));
	}

	@Test
	public void testComplementAtLeastOneWithChar() throws Exception {
		GenericAutomaton a = atLeastOne(matchComplement(match('a')));
		assertThat(matchSamples(a, "b", "a", ""), contains("b"));
	}

	@Test
	public void testComplementWithString() throws Exception {
		GenericAutomaton a = matchComplement(match("ab"));
		assertThat(matchSamples(a, "b", "a", "aa", "bb", "ba", "abc", "", "ab"), contains("b", "a", "aa", "bb", "ba", "abc", ""));
	}

	@Test
	public void testComplementAtLeastOneWithString() throws Exception {
		GenericAutomaton a = atLeastOne(matchComplement(match("ab")));
		assertThat(matchSamples(a, "b", "a", "aa", "bb", "ba", "abc", "", "ab"), contains("b", "a", "aa", "bb", "ba", "abc"));
	}

	@Test
	public void testMatchPrefixWithAutomatons() {
		GenericAutomaton a = match('a');
		GenericAutomaton la = matchConjunctive(matchComplement(match('a')), matchAnyChar());
		GenericAutomaton result = GenericAutomatonBuilder.matchWithPrefix(a, la);
		assertThat(matchSamples(result, "a"), empty());
	}

	@Test
	public void testPrefixStates() {
		State a = new State();
		State fa = new State(ACCEPT);
		a.addTransition(new ExactTransition('a', fa));
		State la = new State();
		State ala = new State(ACCEPT);
		State fla = new State();
		la.addTransition(new ExactTransition('a', fla));
		la.addTransition(new RangeTransition((char) Character.MIN_VALUE, (char) ('a' - 1), ala));
		la.addTransition(new RangeTransition((char) ('a' + 1), (char) Character.MAX_VALUE, ala));
		fla.addTransition(new ExactTransition('a', fla));
		State s = prefixStates(a, la);
		assertThat(s.getTransitions().size(), equalTo(1));
	}

	@Test
	public void testPrefixStateOnLoop() throws Exception {
		State a = new State();
		State fa = new State(ACCEPT);
		a.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, a));
		a.addTransition(new ExactTransition('a', fa));
		State prefix = new State();
		State p2 = new State();
		State p3 = new State(ACCEPT);
		prefix.addTransition(new ExactTransition('-', p2));
		prefix.addTransition(new RangeTransition(Character.MIN_VALUE, (char) ('-' - 1), p3));
		prefix.addTransition(new RangeTransition((char) ('-' + 1), Character.MAX_VALUE, p3));
		p2.addTransition(new RangeTransition(Character.MIN_VALUE, (char) ('}' - 1), p3));
		p2.addTransition(new RangeTransition((char) ('}' + 1), Character.MAX_VALUE, p3));
		State news = prefixStates(a, prefix);
		Automaton newa = new GenericAutomaton(news);
		assertThat(matchSamples(newa, "a", "aaaaa", "----a", "}}}}a"), containsInAnyOrder("a", "aaaaa", "----a", "}}}}a"));
		assertThat(matchSamples(newa, "b", "-}a"), empty());
	}

	private State intersectStates(State s1, State s2) {
		return GenericAutomatonBuilder.intersectStates(s1, s2, new DefaultTokenType.Factory());
	}

	private State prefixStates(State s1, State s2) {
		return GenericAutomatonBuilder.prefixStates(s1, s2, new DefaultTokenType.Factory());
	}

	private State mergePrefixes(State s1, State s2) {
		return GenericAutomatonBuilder.mergePrefixes(s1, s2, new DefaultTokenType.Factory());
	}

}
