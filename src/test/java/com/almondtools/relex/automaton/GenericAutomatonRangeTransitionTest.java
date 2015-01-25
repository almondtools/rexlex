package com.almondtools.relex.automaton;

import static com.almondtools.relex.pattern.DefaultTokenType.ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.relex.automaton.GenericAutomaton.State;
import com.almondtools.relex.tokens.Accept;

public class GenericAutomatonRangeTransitionTest {

	@Test
	public void testToString() throws Exception {
		State state = new State();
		RangeTransition ab = new RangeTransition('a', 'b', state);
		assertThat(ab.toString(), equalTo(" -<a..b>-> " + state.getId()));
	}

	@Test
	public void testEquals() throws Exception {
		State state = new State();
		State error = new State(ERROR);
		RangeTransition e1 = new RangeTransition('a', 'b', state);
		RangeTransition e2 = new RangeTransition('a', 'b', state);
		RangeTransition e3 = new RangeTransition('a', 'b', error);
		RangeTransition e4 = new RangeTransition('a', 'c', state);
		RangeTransition e5 = new RangeTransition('b', 'b', state);
		RangeTransition e6 = new RangeTransition('b', 'c', state);
		assertThat(e1, not(equalTo(null)));
		assertThat(e1, not(equalTo(new Object())));
		assertThat(e1, equalTo(e1));
		assertThat(e1, equalTo(e2));
		assertThat(e1.hashCode(), equalTo(e2.hashCode()));
		assertThat(e1, not(equalTo(e3)));
		assertThat(e1, not(equalTo(e4)));
		assertThat(e1, not(equalTo(e5)));
		assertThat(e1, not(equalTo(e6)));
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
