package com.almondtools.relex.automaton;

import static com.almondtools.relex.pattern.DefaultTokenType.ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.automaton.GenericAutomaton.EpsilonTransition;
import com.almondtools.relex.automaton.GenericAutomaton.State;
import com.almondtools.relex.tokens.Accept;


public class GenericAutomatonEpsilonTransitionTest {

	@Test
	public void testToString() throws Exception {
		State state = new State();
		EpsilonTransition e = new EpsilonTransition(state);
		assertThat(e.toString(), equalTo(" -> " + state.getId()));
	}

	@Test
	public void testEquals() throws Exception {
		State state = new State();
		State error = new State(ERROR);
		EpsilonTransition e1 = new EpsilonTransition(state);
		EpsilonTransition e2 = new EpsilonTransition(state);
		EpsilonTransition e3 = new EpsilonTransition(error);
		assertThat(e1, not(equalTo(null)));
		assertThat(e1, not(equalTo(new Object())));
		assertThat(e1, equalTo(e1));
		assertThat(e1, equalTo(e2));
		assertThat(e1.hashCode(), equalTo(e2.hashCode()));
		assertThat(e1, not(equalTo(e3)));
	}

	@Test
	public void testGetTarget() throws Exception {
		State state = new State();
		EpsilonTransition e = new EpsilonTransition(state);
		assertThat(e.getTarget(), equalTo(state));
	}

	@Test
	public void testClone() throws Exception {
		State state = new State();
		EpsilonTransition e = new EpsilonTransition(state);
		assertThat(e.clone().getTarget(), equalTo(state));
	}

	@Test
	public void testRevert() throws Exception {
		State state = new State();
		State next = new State(Accept.REMAINDER);
		EpsilonTransition e = new EpsilonTransition(state);
		assertThat(e.clone(next).getTarget(), equalTo(next));
	}

}