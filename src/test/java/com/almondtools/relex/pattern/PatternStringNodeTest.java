package com.almondtools.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.pattern.Pattern.StringNode;

public class PatternStringNodeTest {

	@Test
	public void testToString() throws Exception {
		StringNode stringNode = new StringNode("abc");
		assertThat(stringNode.toString(), equalTo("abc"));
	}

	@Test
	public void testGetValue() throws Exception {
		StringNode stringNode = new StringNode("abc");
		assertThat(stringNode.getValue(), equalTo("abc"));
	}

	@Test
	public void testGetLiteralValue() throws Exception {
		StringNode stringNode = new StringNode("abc");
		assertThat(stringNode.getLiteralValue(), equalTo("abc"));
	}

}
