package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.pattern.ComparatorMatcher.compareWith;
import static java.lang.Integer.signum;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.rexlex.automaton.GenericAutomaton.EpsilonTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.automaton.GenericAutomaton.TransitionComparator;
import com.almondtools.rexlex.pattern.ComparatorMatcher;


public class GenericAutomatonTransitionComparatorTest {

	@Test
	public void testCompareEventTransitionsFromFirst() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		RangeTransition bc = new RangeTransition('b', 'c', state);
		assertThat(a, ComparatorMatcher.compareWith(new TransitionComparator()).lessThan(bc));
	}

	@Test
	public void testCompareEventTransitionsToSecondFirst() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		RangeTransition ac = new RangeTransition('a', 'c', state);
		assertThat(a, ComparatorMatcher.compareWith(new TransitionComparator()).lessThan(ac));
	}
	
	@Test
	public void testCompareEventTransitionsIdentityHashcodeLast() throws Exception {
		State state = new State();
		ExactTransition a1 = new ExactTransition('a', state);
		ExactTransition a2 = new ExactTransition('a', state);
		assertThat(a1, compareWith(new TransitionComparator()).compare(signum(System.identityHashCode(a1) - System.identityHashCode(a2)), a2));
		assertThat(a2, compareWith(new TransitionComparator()).compare(signum(System.identityHashCode(a2) - System.identityHashCode(a1)), a1));
	}
	
	@Test
	public void testCompareEventTransitionWithEpsilonTransition() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		EpsilonTransition e = new EpsilonTransition(state);
		assertThat(a, ComparatorMatcher.compareWith(new TransitionComparator()).greaterThan(e));
		assertThat(e, ComparatorMatcher.compareWith(new TransitionComparator()).lessThan(a));
	}
	
	@Test
	public void testCompareEpsilonTransitions() throws Exception {
		State state = new State();
		EpsilonTransition e1 = new EpsilonTransition(state);
		EpsilonTransition e2 = new EpsilonTransition(state);
		assertThat(e1, compareWith(new TransitionComparator()).compare(signum(System.identityHashCode(e1) - System.identityHashCode(e2)), e2));
		assertThat(e2, compareWith(new TransitionComparator()).compare(signum(System.identityHashCode(e2) - System.identityHashCode(e1)), e1));
	}
	
}
