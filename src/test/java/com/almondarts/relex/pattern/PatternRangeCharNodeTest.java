package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.CharNode;
import com.almondarts.relex.pattern.Pattern.RangeCharNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;

public class PatternRangeCharNodeTest {

	@Test
	public void testFromTo() throws Exception {
		RangeCharNode rangeCharNode = new RangeCharNode('0', '9');
		assertThat(rangeCharNode.getFrom(), equalTo('0'));
		assertThat(rangeCharNode.getTo(), equalTo('9'));
	}
	
	@Test
	public void testToStringSimpleConstructor() throws Exception {
		RangeCharNode rangeCharNode = new RangeCharNode('0', '9');
		assertThat(rangeCharNode.toString(), equalTo("0-9"));
	}

	@Test
	public void testToStringAlternativeConstructor() throws Exception {
		RangeCharNode rangeCharNode = new RangeCharNode(new SingleCharNode('0'), new SingleCharNode('9'));
		assertThat(rangeCharNode.toString(), equalTo("0-9"));
	}

	@Test
	public void testToCharNodes() throws Exception {
		RangeCharNode rangeCharNode = new RangeCharNode('0', '9');
		assertThat(rangeCharNode.toCharNodes(), contains((CharNode) rangeCharNode));
	}
}
