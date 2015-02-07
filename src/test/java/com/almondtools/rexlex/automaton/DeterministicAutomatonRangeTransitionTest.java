package com.almondtools.rexlex.automaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.rexlex.tokens.Accept;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.State;

public class DeterministicAutomatonRangeTransitionTest {

	@Test
	public void testToString() throws Exception {
		State state = new State();
		RangeTransition ab = new RangeTransition('a', 'b', state);
		assertThat(ab.toString(), equalTo(" -<a..b>-> " + state.getId()));
	}

	@Test
	public void testGetTarget() throws Exception {
		State state = new State();
		RangeTransition ab = new RangeTransition('a', 'b', state);
		assertThat(ab.getTarget(), equalTo(state));
	}

	@Test
	public void testClone() throws Exception {
		State state = new State();
		State next = new State(Accept.REMAINDER);
		RangeTransition ab = new RangeTransition('a', 'b', state);
		RangeTransition cab = (RangeTransition) ab.clone();
		assertThat(cab.getTarget(), equalTo(state));
		assertThat(cab.getFrom(), equalTo('a'));
		assertThat(cab.getTo(), equalTo('b'));
		RangeTransition can = (RangeTransition) ab.clone(next);
		assertThat(can.getTarget(), equalTo(next));
		assertThat(can.getFrom(), equalTo('a'));
		assertThat(can.getTo(), equalTo('b'));
	}

	@Test
	public void testGetFrom() throws Exception {
		State state = new State();
		RangeTransition ab = new RangeTransition('a', 'b', state);
		assertThat(ab.getFrom(), equalTo('a'));
	}

	@Test
	public void testGetTo() throws Exception {
		State state = new State();
		RangeTransition ab = new RangeTransition('a', 'b', state);
		assertThat(ab.getTo(), equalTo('b'));
	}

	@Test
	public void testMatchesChar() throws Exception {
		State state = new State();
		RangeTransition ab = new RangeTransition('a', 'b', state);
		assertThat(ab.matches('a'), is(true));
		assertThat(ab.matches('b'), is(true));
		assertThat(ab.matches('c'), is(false));
		assertThat(ab.matches(Character.MIN_VALUE), is(false));
	}

	@Test
	public void testNotMatchesOtherChar() throws Exception {
		State state = new State();
		RangeTransition a = new RangeTransition('a', 'b', state);
		assertThat(a.matches('c'), is(false));
	}

}
