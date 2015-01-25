package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.LoopNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;

public class PatternLoopNodeTest {

	@Test
	public void testToFrom() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('a'), 3, 5);
		assertThat(loopNode.getFrom(), equalTo(3));
		assertThat(loopNode.getTo(), equalTo(5));
	}

	@Test
	public void testGetSubNode() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('b'), 3, 5);
		assertThat(loopNode.getSubNode().toString(), equalTo("b"));
	}

	@Test
	public void testToStringOnEmptyLoopNode() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('a'), 0, 0);
		assertThat(loopNode.toString(), equalTo("a{0}"));
	}
	
	@Test
	public void testToStringOnTrivialLoopNode() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('a'), 1, 1);
		assertThat(loopNode.toString(), equalTo("a{1}"));
	}

	@Test
	public void testToStringOnFixedLoopNode() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('a'), 1, 2);
		assertThat(loopNode.toString(), equalTo("a{1,2}"));
	}

	@Test
	public void testToStringOnPlusLoopNode() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('a'), 1, LoopNode.INFINITY);
		assertThat(loopNode.toString(), equalTo("a+"));
	}

	@Test
	public void testToStringOnStarLoopNode() throws Exception {
		LoopNode loopNode = new LoopNode(new SingleCharNode('a'), 0, LoopNode.INFINITY);
		assertThat(loopNode.toString(), equalTo("a*"));
	}

}
