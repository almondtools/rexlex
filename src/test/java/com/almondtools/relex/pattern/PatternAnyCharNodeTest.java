package com.almondtools.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.pattern.Pattern.AnyCharNode;

public class PatternAnyCharNodeTest {

	@Test
	public void testToString() throws Exception {
		AnyCharNode anyCharNode = new AnyCharNode(false);
		assertThat(anyCharNode.toString(), equalTo("."));
	}

	@Test
	public void testToCharNodes() throws Exception {
		AnyCharNode anyCharNode = new AnyCharNode(false);
		assertThat(anyCharNode.toCharNodes(), hasSize(5));
	}
	
	@Test
	public void testToCharNodesDotall() throws Exception {
		AnyCharNode anyCharNode = new AnyCharNode(true);
		assertThat(anyCharNode.toCharNodes(), hasSize(1));
	}
}
