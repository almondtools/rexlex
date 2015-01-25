package com.almondtools.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.pattern.Pattern.EmptyNode;

public class PatternEmptyNodeTest {

	@Test
	public void testToString() throws Exception {
		EmptyNode emptyNode = new EmptyNode();
		assertThat(emptyNode.toString(), equalTo(""));
	}

}
