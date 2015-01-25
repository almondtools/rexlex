package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.CharClassNode;
import com.almondarts.relex.pattern.Pattern.CharNode;
import com.almondarts.relex.pattern.Pattern.ProCharNode;
import com.almondarts.relex.pattern.Pattern.RangeCharNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;
import com.almondarts.relex.pattern.Pattern.SpecialCharClassNode;

public class PatternCharClassNodeTest {

	@Test
	public void testToStringOneElement() throws Exception {
		CharClassNode charClassNode = new CharClassNode(Arrays.asList((CharNode) new RangeCharNode('0', '9')));
		assertThat(charClassNode.toString(), equalTo("[0-9]"));
	}

	@Test
	public void testToStringMoreElements() throws Exception {
		CharClassNode charClassNode = new CharClassNode(Arrays.asList(new RangeCharNode('0', '9'), new SingleCharNode('a')));
		assertThat(charClassNode.toString(), equalTo("[0-9a]"));
	}

	@Test
	public void testToCharNodes() throws Exception {
		CharClassNode charClassNode = new CharClassNode(ProCharNode.toCharNodes(Arrays.asList(new RangeCharNode('0', '9'), new SpecialCharClassNode('x', chars('a', 'b', 'c')))));
		assertThat(charClassNode.toCharNodes(), hasSize(4));
	}

	private List<CharNode> chars(char... values) {
		List<CharNode> charNodes = new ArrayList<CharNode>();
		for (char value : values) {
			charNodes.add(new SingleCharNode(value));
		}
		return charNodes;
	}
}
