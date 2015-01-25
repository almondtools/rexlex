package com.almondarts.relex.automaton;

import static com.almondarts.relex.pattern.DefaultTokenType.ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.automaton.GenericAutomaton.ExactTransition;
import com.almondarts.relex.automaton.GenericAutomaton.State;
import com.almondarts.relex.tokens.Accept;

public class GenericAutomatonExactTransitionTest {

	@Test
	public void testToString() throws Exception {
		State state = new State();
		ExactTransition a = new ExactTransition('a', state);
		assertThat(a.toString(), equalTo(" -<a>-> " + state.getId()));
	}

	@Test
	public void testEquals() throws Exception {
		State state = new State();
		State error = new State(ERROR);
		ExactTransition e1 = new ExactTransition('a', state);
		ExactTransition e2 = new ExactTransition('a', state);
		ExactTransition e3 = new ExactTransition('a', error);
		ExactTransition e4 = new ExactTransition('b', state);
		assertThat(e1, not(equalTo(null)));
		assertThat(e1, not(equalTo(new Object())));
		assertThat(e1, equalTo(e1));
		assertThat(e1, equalTo(e2));
		assertThat(e1.hashCode(), equalTo(e2.hashCode()));
		assertThat(e1, not(equalTo(e3)));
		assertThat(e1, not(equalTo(e4)));
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
