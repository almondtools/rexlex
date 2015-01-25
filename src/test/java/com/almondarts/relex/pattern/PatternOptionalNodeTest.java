package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.OptionalNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;

public class PatternOptionalNodeTest {

	@Test
	public void testToString() throws Exception {
		OptionalNode optionalNode = new OptionalNode(new SingleCharNode('a'));
		assertThat(optionalNode.toString(), equalTo("a?"));
	}

	@Test
	public void testSubNode() throws Exception {
		OptionalNode optionalNode = new OptionalNode(new SingleCharNode('b'));
		assertThat(optionalNode.getSubNode().toString(), equalTo("b"));
	}
	
}
