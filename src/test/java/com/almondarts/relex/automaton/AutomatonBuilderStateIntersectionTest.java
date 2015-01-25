package com.almondarts.relex.automaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.automaton.GenericAutomaton.State;

public class AutomatonBuilderStateIntersectionTest {

	@Test
	public void testEqualsHashcode() throws Exception {
		State s1 = new State();
		State s2 = new State();
		GenericAutomatonBuilder.StateIntersection intersection = new GenericAutomatonBuilder.StateIntersection(s1, s2);
		assertThat(intersection, equalTo(intersection));
		assertThat(intersection, equalTo(new GenericAutomatonBuilder.StateIntersection(s1, s2)));
		assertThat(intersection, not(equalTo(null)));
		assertThat(intersection, not(equalTo(new Object())));
		assertThat(intersection, not(equalTo(new GenericAutomatonBuilder.StateIntersection(s1, s1))));
		assertThat(intersection, not(equalTo(new GenericAutomatonBuilder.StateIntersection(s2, s2))));
		assertThat(intersection, not(equalTo(new GenericAutomatonBuilder.StateIntersection(s2, s1))));
		assertThat(intersection.hashCode(), equalTo(new GenericAutomatonBuilder.StateIntersection(s1, s2).hashCode()));
	}

}
