package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.AlternativesNode;
import com.almondarts.relex.pattern.Pattern.PatternNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;


public class PatternAlternativesNodeTest {

	@Test
	public void testToStringNoAlternatives() throws Exception {
		AlternativesNode alternativesNode = new AlternativesNode(Collections.<PatternNode>emptyList());
		assertThat(alternativesNode.toString(), equalTo(""));
	}

	@Test
	public void testToStringTwoAlternatives() throws Exception {
		AlternativesNode alternativesNode = new AlternativesNode(chars('a', 'b'));
		assertThat(alternativesNode.toString(), equalTo("a|b"));
	}
	
	@Test
	public void testToStringOneAlternative() throws Exception {
		AlternativesNode alternativesNode = new AlternativesNode(chars('a'));
		assertThat(alternativesNode.toString(), equalTo("a"));
	}
	
	@Test
	public void testToStringMoreAlternatives() throws Exception {
		AlternativesNode alternativesNode = new AlternativesNode(chars('a', 'b','c'));
		assertThat(alternativesNode.toString(), equalTo("a|b|c"));
	}

	@Test
	public void testJoinSingleNodes() throws Exception {
		AlternativesNode alternativesNode = AlternativesNode.join(new SingleCharNode('a'), new SingleCharNode('b'));
		assertThat(alternativesNode.getSubNodes(), hasSize(2));
		assertThat(alternativesNode.toString(), equalTo("a|b"));
	}

	@Test
	public void testJoinAlternativesNodeLeft() throws Exception {
		AlternativesNode alternativesNode = AlternativesNode.join(new AlternativesNode(chars('a', 'b')), new SingleCharNode('c'));
		assertThat(alternativesNode.getSubNodes(), hasSize(3));
		assertThat(alternativesNode.toString(), equalTo("a|b|c"));
	}
	
	@Test
	public void testJoinAlternativesNodeRight() throws Exception {
		AlternativesNode alternativesNode = AlternativesNode.join(new SingleCharNode('a'), new AlternativesNode(chars('b', 'c')));
		assertThat(alternativesNode.getSubNodes(), hasSize(3));
		assertThat(alternativesNode.toString(), equalTo("a|b|c"));
	}
	
	private List<PatternNode> chars(char... values) {
		List<PatternNode> charNodes = new ArrayList<PatternNode>();
		for (char value : values) {
			charNodes.add(new SingleCharNode(value));
		}
		return charNodes;
	}
	
}
