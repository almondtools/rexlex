package com.almondtools.relex.automaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.automaton.DeterministicAutomaton.ExactTransition;
import com.almondtools.relex.automaton.DeterministicAutomaton.State;
import com.almondtools.relex.tokens.Accept;

public class DeterministicAutomatonExactTransitionTest {

	@Test
	public void testToString() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.toString(), equalTo(" -<a>-> " + state.getId()));
	}

	@Test
	public void testGetTarget() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.getTarget(), equalTo(state));
	}

	@Test
	public void testClone() throws Exception {
		State state = new State();
		State next = new State(Accept.REMAINDER);
		ExactTransition a = new ExactTransition('a', state);
		ExactTransition ca = (ExactTransition) a.clone();
		assertThat(ca.getTarget(), equalTo(state));
		assertThat(ca.getFrom(), equalTo('a'));
		assertThat(ca.getTo(), equalTo('a'));
		ExactTransition can = (ExactTransition) a.clone(next);
		assertThat(can.getTarget(), equalTo(next));
		assertThat(can.getFrom(), equalTo('a'));
		assertThat(can.getTo(), equalTo('a'));
	}

	@Test
	public void testGetValue() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.getValue(), equalTo('a'));
	}

	@Test
	public void testGetFrom() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.getFrom(), equalTo('a'));
	}

	@Test
	public void testGetTo() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.getTo(), equalTo('a'));
	}

	@Test
	public void testMatchesChar() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.matches('a'), is(true));
	}

	@Test
	public void testNotMatchesOtherChar() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.matches('b'), is(false));
	}
}
