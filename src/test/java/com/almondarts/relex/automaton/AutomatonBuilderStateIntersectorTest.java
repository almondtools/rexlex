package com.almondarts.relex.automaton;

import static com.almondarts.relex.pattern.DefaultTokenType.ACCEPT;
import static com.almondarts.relex.pattern.DefaultTokenType.IGNORE;
import static com.almondarts.relex.tokens.Accept.A;
import static com.almondarts.relex.tokens.Accept.B;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.almondarts.relex.TokenType;
import com.almondarts.relex.automaton.GenericAutomaton.State;
import com.almondarts.relex.pattern.DefaultTokenType;

public class AutomatonBuilderStateIntersectorTest {

	private GenericAutomatonBuilder.StateIntersector stateIntersector;

	@Before
	public void before() {
		stateIntersector = new GenericAutomatonBuilder.StateIntersector(new DefaultTokenType.Factory());
	}

	@Test
	public void testIntersectSame() throws Exception {
		State state = new State();
		State intersected = stateIntersector.intersect(state, state);
		assertThat(intersected, sameInstance(state));
	}


	@Test
	public void testIntersectStatesWithRelatedTokenTypes() throws Exception {
		State s1 = new State(ACCEPT);
		State s2 = new State(IGNORE);
		State intersected12 = stateIntersector.intersect(s1, s2);
		assertThat(intersected12.getType(), equalTo((TokenType) ACCEPT));
		State intersected21 = stateIntersector.intersect(s2, s1);
		assertThat(intersected21.getType(), equalTo((TokenType) ACCEPT));
	}

	@Test
	public void testIntersectStatesWithForeignTokenTypes() throws Exception {
		State s1 = new State(A);
		State s2 = new State(B);
		State intersected12 = stateIntersector.intersect(s1, s2);
		assertThat(intersected12.getType(), nullValue());
		State intersected21 = stateIntersector.intersect(s2, s1);
		assertThat(intersected21.getType(), nullValue());
	}
	
	@Test
	public void testIntersectStatesWithSameTokenTypes() throws Exception {
		State s1 = new State(IGNORE);
		State s2 = new State(IGNORE);
		State intersected12 = stateIntersector.intersect(s1, s2);
		assertThat(intersected12.getType(), equalTo((TokenType) IGNORE));
		State intersected21 = stateIntersector.intersect(s2, s1);
		assertThat(intersected21.getType(), equalTo((TokenType) IGNORE));
	}
	
	@Test
	public void testIntersectStatesWithNullTokenTypes() throws Exception {
		State s1 = new State(IGNORE);
		State s2 = new State((TokenType) null);
		State intersected12 = stateIntersector.intersect(s1, s2);
		assertThat(intersected12.getType(), nullValue());
		State intersected21 = stateIntersector.intersect(s2, s1);
		assertThat(intersected21.getType(), nullValue());
	}
	
}
