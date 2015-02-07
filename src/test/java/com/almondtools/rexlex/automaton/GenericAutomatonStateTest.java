package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.tokens.Accept.A;
import static com.almondtools.rexlex.tokens.Accept.B;
import static com.almondtools.rexlex.tokens.Accept.REMAINDER;
import static com.almondtools.rexlex.pattern.DefaultTokenType.ACCEPT;
import static com.almondtools.rexlex.pattern.DefaultTokenType.ERROR;
import static com.almondtools.rexlex.pattern.DefaultTokenType.IGNORE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomaton.EpsilonTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.EventTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.automaton.GenericAutomaton.Transition;
import com.almondtools.rexlex.automaton.GenericAutomaton.TransitionComparator;
import com.almondtools.rexlex.pattern.DefaultTokenType;
import com.almondtools.rexlex.pattern.RemainderTokenFactory;

public class GenericAutomatonStateTest {

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
	public void testGetTypeClosureOnNullState() throws Exception {
		assertThat(new State().getTypeClosure(), empty());
	}

	@Test
	public void testGetDominantTypeOnOneState() throws Exception {
		assertThat(new State(REMAINDER).getTypeClosure(), contains((TokenType) REMAINDER));
	}

	@Test
	public void testGetDominantTypeOnEpsilonClosureWithExternalDominantState() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		start.addTransition(new GenericAutomaton.EpsilonTransition(next));
		assertThat(start.getTypeClosure(), containsInAnyOrder((TokenType) REMAINDER, IGNORE));
	}

	@Test
	public void testGetDominantTypeOnEpsilonClosureWithDominantState() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		start.addTransition(new GenericAutomaton.EpsilonTransition(next));
		assertThat(start.getTypeClosure(), containsInAnyOrder((TokenType) REMAINDER, IGNORE));
	}

	@Test
	public void testAddTransition() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		start.addTransition(a);
		assertThat(start.getTransitions(), contains(a));
	}

	@Test
	public void testAddEpsilonTransition() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition e = new EpsilonTransition(next);
		start.addTransition(e);
		assertThat(start.getTransitions(), contains(e));
	}

	@Test
	public void testRemoveEpsilonTransition() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		EpsilonTransition e = new EpsilonTransition(next);
		start.addTransition(e);
		assertThat(start.getClosure(), hasItem(next));
		start.removeTransition(e);
		assertThat(start.getClosure(), not(hasItem(next)));
	}

	@Test
	public void testRemoveEventTransition() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		ExactTransition a = new ExactTransition('a', next);
		start.addTransition(a);
		assertThat(start.nexts('a'), hasItem(a));
		start.removeTransition(a);
		assertThat(start.nexts('a'), not(hasItem(a)));
	}

	@Test
	public void testAddTransitions() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		start.addTransitions(Arrays.asList(a, b));
		assertThat(start.getTransitions(), contains(a, b));
	}

	@Test
	public void testReplaceTransition() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		start.addTransition(a);
		assertThat(start.nexts('a'), hasSize(1));
		assertThat(start.nexts('b'), hasSize(0));
		start.replaceTransition(a, b);
		assertThat(start.nexts('a'), hasSize(0));
		assertThat(start.nexts('b'), hasSize(1));
	}

	@Test
	public void testReplaceTransitions() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		Transition c = new ExactTransition('c', next);
		start.addTransition(a);
		assertThat(start.nexts('a'), hasSize(1));
		assertThat(start.nexts('b'), hasSize(0));
		assertThat(start.nexts('c'), hasSize(0));
		start.replaceTransition(a, Arrays.asList(b, c));
		assertThat(start.nexts('a'), hasSize(0));
		assertThat(start.nexts('b'), hasSize(1));
		assertThat(start.nexts('c'), hasSize(1));
	}

	@Test
	public void testInlineEpsilonOnlyAddNewTransitions() throws Exception {
		State start = new State();
		State next = new State();
		State last = new State(REMAINDER);
		EpsilonTransition e = new EpsilonTransition(next);
		Transition a1 = new ExactTransition('a', last);
		Transition a2 = new ExactTransition('a', last);
		start.addTransition(e);
		start.addTransition(a1);
		next.addTransition(a2);
		start.inlineEpsilons(new DefaultTokenType.Factory());
		assertThat(start.getTransitions(), hasSize(1));
		assertThat(start.getTransitions(), contains(a1));
		assertThat(start.getTransitions(), contains(a2));
	}

	@Test
	public void testInlineEpsilonMergesTypeToAdded() throws Exception {
		State start = new State();
		State next = new State(REMAINDER);
		EpsilonTransition e = new EpsilonTransition(next);
		start.addTransition(e);
		start.inlineEpsilons(new DefaultTokenType.Factory());
		assertThat(start.getTransitions(), empty());
		assertThat(start.getType(), equalTo((TokenType) REMAINDER));
	}

	@Test
	public void testInlineEpsilonKeepsTypeIfDominant() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		EpsilonTransition e = new EpsilonTransition(next);
		start.addTransition(e);
		start.inlineEpsilons(new RemainderTokenFactory(REMAINDER));
		assertThat(start.getTransitions(), empty());
		assertThat(start.getType(), equalTo((TokenType) REMAINDER));
	}

	@Test
	public void testInlineEpsilonMergesTransitions() throws Exception {
		State start = new State();
		State next = new State();
		Transition e = new EpsilonTransition(start);
		Transition a = new ExactTransition('a', start);
		start.addTransition(e);
		next.addTransition(a);
		EpsilonTransition toInline = new EpsilonTransition(next);
		start.addTransition(toInline);
		start.inlineEpsilons(new DefaultTokenType.Factory());
		assertThat(start.getTransitions(), contains(a));
	}

	@Test
	public void testEliminateDuplicateTransitionsAlreadyDuplicateFree() throws Exception {
		State start = new State();
		State next = new State();
		Transition a1 = new ExactTransition('a', next);
		start.addTransition(a1);
		start.eliminateDuplicateTransitions();
		assertThat(start.getTransitions(), hasSize(1));
		assertThat(start.getTransitions(), contains(a1));
	}

	@Test
	public void testEliminateDuplicateTransitions() throws Exception {
		State start = new State();
		State next = new State();
		Transition a1 = new ExactTransition('a', next);
		Transition a2 = new ExactTransition('a', start);
		start.addTransition(a1);
		start.addTransition(a2);
		a2.setTarget(next); // addTransition already removes duplicates, a duplicate can only exist if retargetted
		start.eliminateDuplicateTransitions();
		assertThat(start.getTransitions(), hasSize(1));
		assertThat(start.getTransitions(), contains(a1));
		assertThat(start.getTransitions(), contains(a2));
	}

	@Test
	public void testEliminateErrorTransitions() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		State error = new State(ERROR);
		Transition epsilon = new EpsilonTransition(next);
		Transition toerror = new EpsilonTransition(error);
		start.addTransition(epsilon);
		start.addTransition(toerror);
		start.eliminateErrorTransitions();
		assertThat(start.getTransitions(), contains(epsilon));
		assertThat(start.findReachableStates(), containsInAnyOrder(start, next));
	}

	@Test
	public void testEliminateErrorTransitionsWithNoneToEliminate() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		Transition epsilon = new EpsilonTransition(next);
		start.addTransition(epsilon);
		start.eliminateErrorTransitions();
		assertThat(start.getTransitions(), contains(epsilon));
		assertThat(start.findReachableStates(), containsInAnyOrder(start, next));
	}

	@Test
	public void testGetRelevantCharacters() throws Exception {
		State start = new State();
		State next = new State();
		start.addTransition(new EpsilonTransition(next));
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
	public void testGetClosureIsCached() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		EpsilonTransition epsilon = new EpsilonTransition(next);
		start.addTransition(epsilon);
		assertThat(start.getClosure(), containsInAnyOrder(start, next));
		epsilon.setTarget(start);
		assertThat(start.getClosure(), containsInAnyOrder(start, next));
	}

	@Test
	public void testGetClosure() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		start.addTransition(new EpsilonTransition(next));
		assertThat(start.getClosure(), containsInAnyOrder(start, next));
	}

	@Test
	public void testGetClosureWithCycle() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		start.addTransition(new EpsilonTransition(next));
		next.addTransition(new EpsilonTransition(start));
		assertThat(start.getClosure(), containsInAnyOrder(start, next));
	}

	@Test
	public void testGetNextClosure() throws Exception {
		State start = new State(IGNORE);
		State next = new State(IGNORE);
		State last = new State(REMAINDER);
		Transition a = new ExactTransition('a', last);
		Transition b = new ExactTransition('b', last);
		start.addTransition(new EpsilonTransition(next));
		next.addTransition(new EpsilonTransition(start));
		start.addTransition(a);
		next.addTransition(b);
		assertThat(start.getNextClosure(), containsInAnyOrder(a, b));
	}

	@Test
	public void testGetNextClosureIsCached() throws Exception {
		State start = new State(IGNORE);
		State next = new State(IGNORE);
		State last = new State(REMAINDER);
		Transition a = new ExactTransition('a', last);
		Transition b = new ExactTransition('b', last);
		Transition epsilon1 = new EpsilonTransition(next);
		Transition epsilon2 = new EpsilonTransition(start);
		start.addTransition(epsilon1);
		next.addTransition(epsilon2);
		start.addTransition(a);
		next.addTransition(b);
		assertThat(start.getNextClosure(), containsInAnyOrder(a, b));
		epsilon1.setTarget(start);
		assertThat(start.getNextClosure(), containsInAnyOrder(a, b));
	}

	@Test
	public void testGetSortedNextClosure() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		Transition c = new ExactTransition('c', next);
		start.addTransition(c);
		start.addTransition(a);
		start.addTransition(b);
		assertThat(start.getSortedNextClosure(), contains(a, b, c));
	}

	@Test
	public void testGetConnectedStatesContainsEpsilonReachable() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		start.addTransition(new EpsilonTransition(next));
		assertThat(start.getConnectedStates(), contains(next));
	}

	@Test
	public void testGetConnectedStatesContainsEventReachable() throws Exception {
		State start = new State(IGNORE);
		State next = new State(REMAINDER);
		start.addTransition(new ExactTransition('a', next));
		assertThat(start.getConnectedStates(), contains(next));
	}

	@Test
	public void testNexts() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		start.addTransition(a);
		assertThat(start.nexts('a'), contains(a));
	}

	@Test
	public void testNextsForNotMatching() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition a = new ExactTransition('a', next);
		Transition b = new ExactTransition('b', next);
		start.addTransition(a);
		start.addTransition(b);
		assertThat(start.nexts('a'), contains(a));
	}

	@Test
	public void testNextsForEpsilon() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		EpsilonTransition e = new EpsilonTransition(next);
		start.addTransition(e);
		assertThat(start.nexts('a'), empty());
	}

	@Test
	public void testNextsForEpsilonConnectedTransition() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		Transition e = new EpsilonTransition(next);
		Transition a = new ExactTransition('a', start);
		start.addTransition(e);
		next.addTransition(a);
		assertThat(start.nexts('a'), contains(a));
	}

	@Test
	public void testToString() throws Exception {
		State start = new State(REMAINDER);
		State next = new State(IGNORE);
		EpsilonTransition e = new EpsilonTransition(next);
		ExactTransition a = new ExactTransition('a', next);
		start.addTransition(e);
		start.addTransition(a);
		assertThat(start.toString(), containsString("state(" + start.getId() + ")"));
		assertThat(start.toString(), containsString("-> " + next.getId()));
		assertThat(start.toString(), containsString("-<a>-> " + next.getId()));
	}

	@Test
	public void testCloneTree() throws Exception {
		State start = new State(A);
		State next = new State(B);
		Transition e = new EpsilonTransition(next);
		Transition a = new ExactTransition('a', next);
		start.addTransition(e);
		start.addTransition(a);

		State cloned = start.cloneTree();
		State clonednext = cloned.nexts('a').get(0).getTarget();
		assertThat(cloned, not(sameInstance(start)));
		assertThat(cloned.getType(), equalTo((TokenType) A));
		assertThat(clonednext, not(sameInstance(next)));
		assertThat(clonednext.getType(), equalTo((TokenType) B));
		assertThat(cloned.getClosure(), containsInAnyOrder(cloned, clonednext));
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
		State accept = new State(ACCEPT);
		State error = new State(ERROR);
		start.addTransition(new ExactTransition('a', accept));
		start.addTransition(new ExactTransition('b', error));

		assertThat(start.findLiveStates(), containsInAnyOrder(start, accept));
	}

	@Test
	public void testAddErrorTransitionsOnMaxValue() throws Exception {
		State state = new State();
		State target = new State();
		State error = new State();
		EventTransition c = new ExactTransition((char) (Character.MAX_VALUE - 1), target);
		state.addTransition(c);
		state.addErrorTransitions(error);
		List<EventTransition> transitions = state.getSortedNextClosure();
		assertThat(transitions, hasItems(c));
		assertThat(transitions, hasSize(3));
		assertThat(transitions.get(1), equalTo(c));
		assertThat(transitions.get(0).getFrom(), equalTo(Character.MIN_VALUE));
		assertThat(transitions.get(0).getTo(), equalTo((char) (Character.MAX_VALUE - 2)));
		assertThat(transitions.get(2).getFrom(), equalTo(Character.MAX_VALUE));
		assertThat(transitions.get(2).getTo(), equalTo(Character.MAX_VALUE));
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
		SortedSet<Transition> transitions = new TreeSet<Transition>(new TransitionComparator());
		transitions.addAll(state.getSortedNextClosure());
		assertThat(transitions, hasItems(a, ce, ef, h));
		assertThat(transitions, hasSize(8));
		assertThat(transitions.headSet(a).size(), equalTo(1));
		assertThat(transitions.headSet(ce).size(), equalTo(3));
		assertThat(transitions.headSet(ef).size(), equalTo(4));
		assertThat(transitions.headSet(h).size(), equalTo(6));
		assertThat(((EventTransition) transitions.headSet(a).first()).getFrom(), equalTo(Character.MIN_VALUE));
		assertThat(((EventTransition) transitions.headSet(a).first()).getTo(), equalTo((char) ('a' - 1)));
		assertThat(((EventTransition) transitions.last()).getFrom(), equalTo((char) ('h' + 1)));
		assertThat(((EventTransition) transitions.last()).getTo(), equalTo(Character.MAX_VALUE));
	}

	@Test
	public void testComputeSortedTransitionsOnlyEventTransitions() throws Exception {
		State state = new State();
		State target = new State();
		EventTransition a = new ExactTransition('a', target);
		EventTransition ce = new RangeTransition('c', 'e', target);
		EventTransition ef = new RangeTransition('e', 'f', target);
		EventTransition h = new ExactTransition('h', target);
		state.addTransition(a);
		state.addTransition(ef);
		state.addTransition(ce);
		state.addTransition(h);
		assertThat(state.getTransitions().size(), equalTo(4));
		SortedSet<Transition> transitions = new TreeSet<Transition>(new TransitionComparator());
		transitions.addAll(state.getSortedNextClosure());
		assertThat(transitions.size(), equalTo(4));
		assertThat(transitions.headSet(ce).size(), equalTo(1));
		assertThat(transitions.headSet(ef).size(), equalTo(2));
		assertThat(transitions.headSet(h).size(), equalTo(3));
	}

}
