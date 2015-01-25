package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.ComplementNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;

public class PatternComplementNodeTest {

	@Test
	public void testToString() throws Exception {
		ComplementNode complementNode = new ComplementNode(new SingleCharNode('a'));
		assertThat(complementNode.toString(), equalTo("~a"));
	}

	@Test
	public void testSubNode() throws Exception {
		ComplementNode complementNode = new ComplementNode(new SingleCharNode('b'));
		assertThat(complementNode.getSubNode().toString(), equalTo("b"));
	}
	
}
