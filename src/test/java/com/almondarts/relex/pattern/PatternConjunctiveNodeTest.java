package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.ConjunctiveNode;
import com.almondarts.relex.pattern.Pattern.PatternNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;


public class PatternConjunctiveNodeTest {

	@Test
	public void testToStringNoAlternatives() throws Exception {
		ConjunctiveNode conjunctiveNode = new ConjunctiveNode(Collections.<PatternNode>emptyList());
		assertThat(conjunctiveNode.toString(), equalTo(""));
	}

	@Test
	public void testToStringTwoAlternatives() throws Exception {
		ConjunctiveNode conjunctiveNode = new ConjunctiveNode(chars('a', 'b'));
		assertThat(conjunctiveNode.toString(), equalTo("a&b"));
	}
	
	@Test
	public void testToStringOneAlternative() throws Exception {
		ConjunctiveNode conjunctiveNode = new ConjunctiveNode(chars('a'));
		assertThat(conjunctiveNode.toString(), equalTo("a"));
	}
	
	@Test
	public void testToStringMoreAlternatives() throws Exception {
		ConjunctiveNode conjunctiveNode = new ConjunctiveNode(chars('a', 'b','c'));
		assertThat(conjunctiveNode.toString(), equalTo("a&b&c"));
	}

	@Test
	public void testJoinSingleNodes() throws Exception {
		ConjunctiveNode conjunctiveNode = ConjunctiveNode.join(new SingleCharNode('a'), new SingleCharNode('b'));
		assertThat(conjunctiveNode.getSubNodes(), hasSize(2));
		assertThat(conjunctiveNode.toString(), equalTo("a&b"));
	}

	@Test
	public void testJoinConjunctiveNodeLeft() throws Exception {
		ConjunctiveNode conjunctiveNode = ConjunctiveNode.join(new ConjunctiveNode(chars('a', 'b')), new SingleCharNode('c'));
		assertThat(conjunctiveNode.getSubNodes(), hasSize(3));
		assertThat(conjunctiveNode.toString(), equalTo("a&b&c"));
	}
	
	@Test
	public void testJoinConjunctiveNodeRight() throws Exception {
		ConjunctiveNode conjunctiveNode = ConjunctiveNode.join(new SingleCharNode('a'), new ConjunctiveNode(chars('b', 'c')));
		assertThat(conjunctiveNode.getSubNodes(), hasSize(3));
		assertThat(conjunctiveNode.toString(), equalTo("a&b&c"));
	}
	
	private List<PatternNode> chars(char... values) {
		List<PatternNode> charNodes = new ArrayList<PatternNode>();
		for (char value : values) {
			charNodes.add(new SingleCharNode(value));
		}
		return charNodes;
	}
	
}
