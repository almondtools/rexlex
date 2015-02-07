package com.almondtools.rexlex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.almondtools.rexlex.pattern.Pattern.AnyCharNode;
import com.almondtools.rexlex.pattern.Pattern.ConcatNode;
import com.almondtools.rexlex.pattern.Pattern.PatternNode;
import com.almondtools.rexlex.pattern.Pattern.SingleCharNode;
import com.almondtools.rexlex.pattern.Pattern.StringNode;


public class PatternConcatNodeTest {

	@Test
	public void testToStringNoAlternatives() throws Exception {
		ConcatNode concatNode = new ConcatNode(Collections.<PatternNode>emptyList());
		assertThat(concatNode.toString(), equalTo(""));
	}

	@Test
	public void testToStringTwoAlternatives() throws Exception {
		ConcatNode concatNode = new ConcatNode(chars('a', 'b'));
		assertThat(concatNode.toString(), equalTo("ab"));
	}
	
	@Test
	public void testToStringOneAlternative() throws Exception {
		ConcatNode concatNode = new ConcatNode(chars('a'));
		assertThat(concatNode.toString(), equalTo("a"));
	}
	
	@Test
	public void testToStringMoreAlternatives() throws Exception {
		ConcatNode concatNode = new ConcatNode(chars('a', 'b','c'));
		assertThat(concatNode.toString(), equalTo("abc"));
	}

	@Test
	public void testJoinSingleCharNodes() throws Exception {
		PatternNode concatNode = ConcatNode.join(new SingleCharNode('a'), new SingleCharNode('b'));
		assertThat(concatNode, instanceOf(StringNode.class));
		assertThat(concatNode.toString(), equalTo("ab"));
	}

	@Test
	public void testJoinSingleCharAndStringNodesLeft() throws Exception {
		PatternNode concatNode = ConcatNode.join(new StringNode("ab"), new SingleCharNode('c'));
		assertThat(concatNode, instanceOf(StringNode.class));
		assertThat(concatNode.toString(), equalTo("abc"));
	}
	
	@Test
	public void testJoinSingleCharAndStringNodesRight() throws Exception {
		PatternNode concatNode = ConcatNode.join(new SingleCharNode('a'), new StringNode("bc"));
		assertThat(concatNode, instanceOf(StringNode.class));
		assertThat(concatNode.toString(), equalTo("abc"));
	}
	
	@Test
	public void testJoinSingleCharAndConcatNodesLeft() throws Exception {
		PatternNode concatNode = ConcatNode.join(new ConcatNode(chars('a','b')), new SingleCharNode('c'));
		assertThat(concatNode, instanceOf(StringNode.class));
		assertThat(concatNode.toString(), equalTo("abc"));
	}
	
	@Test
	public void testJoinSingleCharAndConcatNodesRight() throws Exception {
		PatternNode concatNode = ConcatNode.join(new SingleCharNode('a'), new ConcatNode(chars('b','c')));
		assertThat(concatNode, instanceOf(StringNode.class));
		assertThat(concatNode.toString(), equalTo("abc"));
	}
	
	@Test
	public void testJoinSingleCharAndNonJoinableNode() throws Exception {
		PatternNode concatNode = ConcatNode.join(new SingleCharNode('a'), ConcatNode.join(new AnyCharNode(false), new SingleCharNode('b')));
		assertThat(concatNode, instanceOf(ConcatNode.class));
		assertThat(((ConcatNode) concatNode).getSubNodes(), hasSize(3));
		assertThat(concatNode.toString(), equalTo("a.b"));
	}
	
	private List<PatternNode> chars(char... values) {
		List<PatternNode> charNodes = new ArrayList<PatternNode>();
		for (char value : values) {
			charNodes.add(new SingleCharNode(value));
		}
		return charNodes;
	}
	
}
