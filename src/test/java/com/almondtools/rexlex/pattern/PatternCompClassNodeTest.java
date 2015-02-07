package com.almondtools.rexlex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.almondtools.rexlex.pattern.Pattern.CharNode;
import com.almondtools.rexlex.pattern.Pattern.CompClassNode;
import com.almondtools.rexlex.pattern.Pattern.ProCharNode;
import com.almondtools.rexlex.pattern.Pattern.RangeCharNode;
import com.almondtools.rexlex.pattern.Pattern.SingleCharNode;
import com.almondtools.rexlex.pattern.Pattern.SpecialCharClassNode;

public class PatternCompClassNodeTest {

	@Test
	public void testToStringOneElement() throws Exception {
		CompClassNode compClassNode = new CompClassNode(Arrays.asList((CharNode) new RangeCharNode('0', '9')));
		assertThat(compClassNode.toString(), equalTo("[^0-9]"));
	}

	@Test
	public void testToStringMoreElements() throws Exception {
		CompClassNode compClassNode = new CompClassNode(Arrays.asList(new RangeCharNode('0', '9'), new SingleCharNode('a')));
		assertThat(compClassNode.toString(), equalTo("[^0-9a]"));
	}

	@Test
	public void testToCharNodes() throws Exception {
		CompClassNode compClassNode = new CompClassNode(ProCharNode.toCharNodes(Arrays.asList((ProCharNode) new RangeCharNode('0', '9'), new SpecialCharClassNode('x', chars('a', 'b', 'c')))));
		assertThat(compClassNode.toCharNodes(), hasSize(3));
	}

	private List<CharNode> chars(char... values) {
		List<CharNode> charNodes = new ArrayList<CharNode>();
		for (char value : values) {
			charNodes.add(new SingleCharNode(value));
		}
		return charNodes;
	}
}
