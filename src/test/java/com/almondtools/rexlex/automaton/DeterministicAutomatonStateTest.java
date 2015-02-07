package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.tokens.Accept.REMAINDER;
import static com.almondtools.rexlex.pattern.DefaultTokenType.ACCEPT;
import static com.almondtools.rexlex.pattern.DefaultTokenType.ERROR;
import static com.almondtools.rexlex.pattern.DefaultTokenType.IGNORE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.SortedSet;

import org.junit.Test;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.ExactTransition;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.State;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.Transition;

public class DeterministicAutomatonStateTest {

	@Test
	public void testAccept() throws Exception {
		assertThat(new State(REMAINDER).accept(), is(true));
		assertThat(new State(IGNORE).accept(), is(true));
		assertThat(new State().accept(), is(false));
		assertThat(new State(ERROR).accept(), is(false));
	}

	@Test
	public void testError() throws Exception {
		assertThat(new State(ERROR).error(), is(true));
		assertThat(new State().error(), is(false));
		assertThat(new State(IGNORE).error(), is(false));
		assertThat(new State(REMAINDER).error(), is(false));
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(new State(REMAINDER).getType(), equalTo((TokenType) REMAINDER));
		assertThat(new State().getType(), nullValue());
	}

	@Test
	public void testAddTransition() throws Exception {
		State state = new State();
		State next = new State();
		Transition a = new ExactTransition('a', next);
		state.addTransition(a);
		assertThat(state.getTransitions(), contains(a));
	}

	@Test
	public void testGetRelevantCharacters() throws Exception {
		State start = new State();
		State next = new State();
		start.addTransition(new ExactTransition('a', next));
		start.addTransition(new RangeTransition('c', 'e', next));
		assertThat(start.getRelevantCharacters(), containsInAnyOrder(Character.MIN_VALUE, 'a', 'b', 'c', 'f'));
	}

	@Test
	public void testGetRelevantCharactersForCharacterMaxValue() throws Exception {
		State start = new State();
		State next = new State();
		start.addTransition(new ExactTransition('a', next));
		start.addTransition(new ExactTransition(Character.MAX_VALUE, next));
		assertThat(start.getRelevantCharacters(), containsInAnyOrder(Character.MIN_VALUE, 'a', 'b', Character.MAX_VALUE));
	}

	@Test
	public void testGetDirectlyReachableStatesEmpty() throws Exception {
		State start = new State();
		assertThat(start.getDirectlyReachableStates(), empty());
	}

	@Test
	public void testGetDirectlyReachableStates() throws Exception {
		State start = new State();
		State next = new State();
		State nextnext = new State();
		ExactTransition a1 = new ExactTransition('a', next);
		ExactTransition a2 = new ExactTransition('a', nextnext);
		start.addTransition(a1);
		next.addTransition(a2);
		assertThat(start.getDirectlyReachableStates(), contains(next));
	}
	
	@Test
	public void testNext() throws Exception {
		State start = new State();
		State nexta = new State();
		State nextb = new State();
		ExactTransition a = new ExactTransition('a', nexta);
		ExactTransition b = new ExactTransition('b', nextb);
		start.addTransition(a);
		start.addTransition(b);
		assertThat(start.next('a'), equalTo(nexta));
		assertThat(start.next('b'), equalTo(nextb));
	}

	@Test
	public void testMergeAdjacentTransitionsTwoExact() throws Exception {
		State start = new State();
		State next = new State();
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		start.addTransition(a);
		start.addTransition(b);
		start.mergeAdjacentTransitions();
		assertThat(start.getTransitions(), hasSize(1));
		assertThat(start.next('a'), equalTo(next));
		assertThat(start.next('b'), equalTo(next));
	}
	
	@Test
	public void testMergeAdjacentTransitionsTwoRange() throws Exception {
		State start = new State();
		State next = new State();
		Transition ab = new RangeTransition('a', 'b', next);
		Transition cd = new RangeTransition('c', 'd', next);
		start.addTransition(ab);
		start.addTransition(cd);
		start.mergeAdjacentTransitions();
		assertThat(start.getTransitions(), hasSize(1));
		assertThat(start.next('a'), equalTo(next));
		assertThat(start.next('b'), equalTo(next));
		assertThat(start.next('c'), equalTo(next));
		assertThat(start.next('d'), equalTo(next));
	}
	
	@Test
	public void testMergeAdjacentTransitionsMixed() throws Exception {
		State start = new State();
		State next = new State();
		State other = new State();
		Transition a = new ExactTransition('a', next);
		Transition bc = new RangeTransition('b', 'c', next);
		Transition de = new RangeTransition('d', 'e', other);
		Transition f = new ExactTransition('f', other);
		start.addTransition(a);
		start.addTransition(bc);
		start.addTransition(de);
		start.addTransition(f);
		start.mergeAdjacentTransitions();
		assertThat(start.getTransitions(), hasSize(2));
		assertThat(start.next('a'), equalTo(next));
		assertThat(start.next('b'), equalTo(next));
		assertThat(start.next('c'), equalTo(next));
		assertThat(start.next('d'), equalTo(other));
		assertThat(start.next('e'), equalTo(other));
		assertThat(start.next('f'), equalTo(other));
	}

	@Test
	public void testMergeAdjacentTransitionsNotOverGaps() throws Exception {
		State start = new State();
		State next = new State();
		Transition a = new ExactTransition('a', next);
		Transition c = new ExactTransition('c', next);
		start.addTransition(a);
		start.addTransition(c);
		start.mergeAdjacentTransitions();
		assertThat(start.getTransitions(), hasSize(2));
		assertThat(start.next('a'), equalTo(next));
		assertThat(start.next('b'), nullValue());
		assertThat(start.next('c'), equalTo(next));
	}
	
	@Test
	public void testMergeAdjacentTransitionsOverlapping() throws Exception {
		State start = new State();
		State next = new State();
		Transition ab = new RangeTransition('a', 'b', next);
		Transition bc = new RangeTransition('b', 'c', next);
		start.addTransition(ab);
		start.addTransition(bc);
		start.mergeAdjacentTransitions();
		assertThat(start.getTransitions(), hasSize(1));
		assertThat(start.next('a'), equalTo(next));
		assertThat(start.next('b'), equalTo(next));
		assertThat(start.next('c'), equalTo(next));
	}
	
	@Test
	public void testMergeAdjacentTransitionsSubsuming() throws Exception {
		State start = new State();
		State next = new State();
		State other = new State();
		Transition ad = new RangeTransition('a', 'd', next);
		Transition bc = new RangeTransition('b', 'c', next);
		Transition e = new ExactTransition('e', other);
		Transition ef = new RangeTransition('e', 'f', other);
		start.addTransition(ad);
		start.addTransition(bc);
		start.addTransition(e);
		start.addTransition(ef);
		start.mergeAdjacentTransitions();
		assertThat(start.getTransitions(), hasSize(2));
		assertThat(start.next('a'), equalTo(next));
		assertThat(start.next('b'), equalTo(next));
		assertThat(start.next('c'), equalTo(next));
		assertThat(start.next('d'), equalTo(next));
		assertThat(start.next('e'), equalTo(other));
		assertThat(start.next('f'), equalTo(other));
	}
	
	@Test
	public void testToString() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition bc = new RangeTransition('b', 'c', next);
		start.addTransition(a);
		start.addTransition(bc);
		assertThat(start.toString(), containsString("state(" + start.getId() + ")"));
		assertThat(start.toString(), containsString("-<a>-> " + next.getId()));
		assertThat(start.toString(), containsString("-<b..c>-> " + next.getId()));
	}

	@Test
	public void testAddErrorTransitionsOnMaxValue() throws Exception {
		State state = new State();
		State target = new State();
		State error = new State();
		Transition c = new ExactTransition((char) (Character.MAX_VALUE - 1), target);
		state.addTransition(c);
		state.addErrorTransitions(error);
		SortedSet<Transition> transitions = state.computeSortedTransitions();
		assertThat(transitions, hasItems(c));
		assertThat(transitions, hasSize(3));
		assertThat(transitions.toArray(new Transition[0])[1], equalTo(c));
		assertThat(transitions.first().getFrom(), equalTo(Character.MIN_VALUE));
		assertThat(transitions.first().getTo(), equalTo((char) (Character.MAX_VALUE - 2)));
		assertThat(transitions.last().getFrom(), equalTo(Character.MAX_VALUE));
		assertThat(transitions.last().getTo(), equalTo(Character.MAX_VALUE));
	}

	@Test
	public void testAddErrorTransitions() throws Exception {
		State state = new State();
		State target = new State();
		State error = new State();
		Transition a = new ExactTransition('a', target);
		Transition ce = new RangeTransition('c', 'e', target);
		Transition ef = new RangeTransition('e', 'f', target);
		Transition h = new ExactTransition('h', target);
		state.addTransition(a);
		state.addTransition(ef);
		state.addTransition(ce);
		state.addTransition(h);
		state.addErrorTransitions(error);
		SortedSet<Transition> transitions = state.computeSortedTransitions();
		assertThat(transitions, hasItems(a, ce, ef, h));
		assertThat(transitions, hasSize(8));
		assertThat(transitions.headSet(a).size(), equalTo(1));
		assertThat(transitions.headSet(ce).size(), equalTo(3));
		assertThat(transitions.headSet(ef).size(), equalTo(4));
		assertThat(transitions.headSet(h).size(), equalTo(6));
		assertThat(transitions.headSet(a).first().getFrom(), equalTo(Character.MIN_VALUE));
		assertThat(transitions.headSet(a).first().getTo(), equalTo((char) ('a' - 1)));
		assertThat(transitions.last().getFrom(), equalTo((char) ('h' + 1)));
		assertThat(transitions.last().getTo(), equalTo(Character.MAX_VALUE));
	}

	@Test
	public void testComputeSortedTransitions() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		Transition c = new ExactTransition('c', next);
		start.addTransition(c);
		start.addTransition(a);
		start.addTransition(b);
		assertThat(start.computeSortedTransitions(), contains(a, b, c));
	}

	@Test
	public void testFindAcceptStates() throws Exception {
		State start = new State();
		State accept = new State(ACCEPT);
		State error = new State(ERROR);
		start.addTransition(new ExactTransition('a', accept));
		start.addTransition(new ExactTransition('b', error));

		assertThat(start.findAcceptStates(), contains(accept));
	}

	@Test
	public void testFindReachableStates() throws Exception {
		State start = new State();
		State accept = new State(ACCEPT);
		State error = new State(ERROR);
		start.addTransition(new ExactTransition('a', accept));
		start.addTransition(new ExactTransition('b', error));

		assertThat(start.findReachableStates(), containsInAnyOrder(start, accept, error));
	}

	@Test
	public void testFindLiveStates() throws Exception {
		State start = new State();
		State next = new State();
		State accept = new State(ACCEPT);
		State error = new State(ERROR);
		start.addTransition(new ExactTransition('a', accept));
		start.addTransition(new ExactTransition('b', error));
		start.addTransition(new ExactTransition('c', next));
		next.addTransition(new ExactTransition('c', accept));

		assertThat(start.findLiveStates(), containsInAnyOrder(start, next, accept));
	}

}
