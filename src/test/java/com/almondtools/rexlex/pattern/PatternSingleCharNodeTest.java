package com.almondtools.rexlex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.rexlex.pattern.Pattern.CharNode;
import com.almondtools.rexlex.pattern.Pattern.SingleCharNode;

public class PatternSingleCharNodeTest {

	@Test
	public void testFromTo() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('x');
		assertThat(singleCharNode.getFrom(), equalTo('x'));
		assertThat(singleCharNode.getTo(), equalTo('x'));
	}
	
	@Test
	public void testToString() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('x');
		assertThat(singleCharNode.toString(), equalTo("x"));
	}

	@Test
	public void testToStringOperator() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('|');
		assertThat(singleCharNode.toString(), equalTo("\\|"));
	}
	
	@Test
	public void testToStringEscape() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('\n');
		assertThat(singleCharNode.toString(), equalTo("\\n"));
	}
	
	@Test
	public void testToStringBackslash() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('\\');
		assertThat(singleCharNode.toString(), equalTo("\\\\"));
	}
	
	@Test
	public void testToStringQuote() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('\'');
		assertThat(singleCharNode.toString(), equalTo("'"));
	}
	
	@Test
	public void testToStringDoubleQuote() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('\"');
		assertThat(singleCharNode.toString(), equalTo("\""));
	}
	
	@Test
	public void testGetValue() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('x');
		assertThat(singleCharNode.getValue(), equalTo('x'));
	}
	
	@Test
	public void testGetLiteralValue() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('x');
		assertThat(singleCharNode.getLiteralValue(), equalTo("x"));
	}
	
	@Test
	public void testToCharNodes() throws Exception {
		SingleCharNode singleCharNode = new SingleCharNode('x');
		assertThat(singleCharNode.toCharNodes(), contains((CharNode) singleCharNode));
	}
}
