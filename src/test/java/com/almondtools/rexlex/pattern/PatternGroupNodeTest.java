package com.almondtools.rexlex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.rexlex.pattern.Pattern.GroupNode;
import com.almondtools.rexlex.pattern.Pattern.SingleCharNode;

public class PatternGroupNodeTest {

	@Test
	public void testToString() throws Exception {
		GroupNode groupNode = new GroupNode(new SingleCharNode('a'));
		assertThat(groupNode.toString(), equalTo("(a)"));
	}

	@Test
	public void testGetNode() throws Exception {
		GroupNode groupNode = new GroupNode(new SingleCharNode('a'));
		assertThat(groupNode.getSubNode().toString(), equalTo("a"));
	}
	
}
