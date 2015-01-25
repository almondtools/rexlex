package com.almondarts.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondarts.relex.pattern.Pattern.AlternativesNode;
import com.almondarts.relex.pattern.Pattern.AnyCharNode;
import com.almondarts.relex.pattern.Pattern.CharClassNode;
import com.almondarts.relex.pattern.Pattern.CompClassNode;
import com.almondarts.relex.pattern.Pattern.ComplementNode;
import com.almondarts.relex.pattern.Pattern.ConcatNode;
import com.almondarts.relex.pattern.Pattern.ConjunctiveNode;
import com.almondarts.relex.pattern.Pattern.EmptyNode;
import com.almondarts.relex.pattern.Pattern.GroupNode;
import com.almondarts.relex.pattern.Pattern.LoopNode;
import com.almondarts.relex.pattern.Pattern.OptionalNode;
import com.almondarts.relex.pattern.Pattern.PatternNode;
import com.almondarts.relex.pattern.Pattern.PatternParser;
import com.almondarts.relex.pattern.Pattern.RangeCharNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;
import com.almondarts.relex.pattern.Pattern.StringNode;
public class PatternParserTest {

	@Test
	public void testAnyChar() {
		PatternParser parser = new PatternParser(".");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(AnyCharNode.class));
		assertThat(node.toString(), equalTo("."));
	}
	
	@Test
	public void testAlternatives() {
		PatternParser parser = new PatternParser("a|b");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(AlternativesNode.class));
		assertThat(node.toString(), equalTo("a|b"));
		assertThat(((AlternativesNode) node).getSubNodes().size(), equalTo(2));
	}
	
	@Test
	public void testIntersection() {
		PatternParser parser = new PatternParser("a&b");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(ConjunctiveNode.class));
		assertThat(node.toString(), equalTo("a&b"));
		assertThat(((ConjunctiveNode) node).getSubNodes().size(), equalTo(2));
	}
	
	@Test
	public void testAlternativesInParentheses() {
		PatternParser parser = new PatternParser("(a|b)");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(GroupNode.class));
		assertThat(node.toString(), equalTo("(a|b)"));
		node = ((GroupNode) node).getSubNode();
		assertThat(node, instanceOf(AlternativesNode.class));
		assertThat(((AlternativesNode) node).getSubNodes().size(), equalTo(2));
	}
	
	@Test
	public void testNonCapturedAlternatives() {
		PatternParser parser = new PatternParser("(a|b)");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(GroupNode.class));
		assertThat(node.toString(), equalTo("(a|b)"));
		node = ((GroupNode) node).getSubNode();
		assertThat(node, instanceOf(AlternativesNode.class));
		assertThat(((AlternativesNode) node).getSubNodes().size(), equalTo(2));
	}
	
	@Test
	public void testConcat() {
		PatternParser parser = new PatternParser("ab*");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(ConcatNode.class));
		assertThat(node.toString(), equalTo("ab*"));
		assertThat(((ConcatNode) node).getSubNodes().size(), equalTo(2));
	}
	
	@Test
	public void testMultiConcat() {
		PatternParser parser = new PatternParser("ab*c");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(ConcatNode.class));
		assertThat(node.toString(), equalTo("ab*c"));
		assertThat(((ConcatNode) node).getSubNodes().size(), equalTo(3));
	}	

	@Test
	public void testString() {
		PatternParser parser = new PatternParser("ab");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(StringNode.class));
		assertThat(node.toString(), equalTo("ab"));
	}
	
	@Test
	public void testEmpty() {
		PatternParser parser = new PatternParser("");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(EmptyNode.class));
		assertThat(node.toString(), equalTo(""));
	}
	
	@Test
	public void testLoopPlus() {
		PatternParser parser = new PatternParser("a+");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(LoopNode.class));
		assertThat(node.toString(), equalTo("a+"));
		assertThat(((LoopNode) node).getFrom(), equalTo(1));
		assertThat(((LoopNode) node).getTo(), equalTo(LoopNode.INFINITY));
	}
	
	@Test
	public void testLoopStar() {
		PatternParser parser = new PatternParser("a*");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(LoopNode.class));
		assertThat(node.toString(), equalTo("a*"));
		assertThat(((LoopNode) node).getFrom(), equalTo(0));
		assertThat(((LoopNode) node).getTo(), equalTo(LoopNode.INFINITY));
	}
	
	@Test
	public void testLoopBounded() {
		PatternParser parser = new PatternParser("a{1,2}");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(LoopNode.class));
		assertThat(node.toString(), equalTo("a{1,2}"));
		assertThat(((LoopNode) node).getFrom(), equalTo(1));
		assertThat(((LoopNode) node).getTo(), equalTo(2));
	}
	
	@Test
	public void testLoopFixed() {
		PatternParser parser = new PatternParser("a{2}");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(LoopNode.class));
		assertThat(node.toString(), equalTo("a{2}"));
		assertThat(((LoopNode) node).getFrom(), equalTo(2));
		assertThat(((LoopNode) node).getTo(), equalTo(2));
	}
	
	@Test
	public void testOptional() {
		PatternParser parser = new PatternParser("a?");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(OptionalNode.class));
		assertThat(node.toString(), equalTo("a?"));
	}
	
	@Test
	public void testComplement() {
		PatternParser parser = new PatternParser("~a");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(ComplementNode.class));
		assertThat(node.toString(), equalTo("~a"));
	}
	
	@Test
	public void testCharClassRangeChar() {
		PatternParser parser = new PatternParser("[a-b]");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(CharClassNode.class));
		assertThat(node.toString(), equalTo("[a-b]"));
		assertThat(((CharClassNode) node).toCharNodes().get(0), instanceOf(RangeCharNode.class));
		assertThat(((RangeCharNode) ((CharClassNode) node).toCharNodes().get(0)).getFrom(), equalTo('a'));
		assertThat(((RangeCharNode) ((CharClassNode) node).toCharNodes().get(0)).getTo(), equalTo('b'));
	}
	
	@Test
	public void testCharClassComplementaryRangeChar() {
		PatternParser parser = new PatternParser("[^a-b]");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(CompClassNode.class));
		assertThat(node.toString(), equalTo("[^a-b]"));
		assertThat(((CompClassNode) node).toCharNodes().get(0), instanceOf(RangeCharNode.class));
		assertThat(((RangeCharNode) ((CompClassNode) node).toCharNodes().get(0)).getTo(), equalTo((char) ('a' - 1)));
		assertThat(((RangeCharNode) ((CompClassNode) node).toCharNodes().get(1)).getFrom(), equalTo((char) ('b' + 1)));
	}
	
	@Test
	public void testCharClassMixed() {
		PatternParser parser = new PatternParser("[a-bc-de]");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(CharClassNode.class));
		assertThat(node.toString(), equalTo("[a-bc-de]"));
		assertThat(((CharClassNode) node).toCharNodes().get(0), instanceOf(RangeCharNode.class));
		assertThat(((CharClassNode) node).toCharNodes().get(1), instanceOf(RangeCharNode.class));
		assertThat(((CharClassNode) node).toCharNodes().get(2), instanceOf(SingleCharNode.class));
	}
	
	@Test
	public void testSingleChar() {
		PatternParser parser = new PatternParser("a");
		PatternNode node = parser.parse();
		assertThat(node, instanceOf(SingleCharNode.class));
		assertThat(node.toString(), equalTo("a"));
	}
	
	@Test
	public void testEscapedInput() {
		checkSingleCharPattern("\\|",'|');
		checkSingleCharPattern("\\{",'{');
		checkSingleCharPattern("\\}",'}');
		checkSingleCharPattern("\\(",'(');
		checkSingleCharPattern("\\)",')');
		checkSingleCharPattern("\\[",'[');
		checkSingleCharPattern("\\]",']');
		checkSingleCharPattern("\\?",'?');
		checkSingleCharPattern("\\*",'*');
		checkSingleCharPattern("\\+",'+');
		checkSingleCharPattern("\\,",',');
		checkSingleCharPattern("\\&",'&');
		checkSingleCharPattern("\\t",'\t');
		checkSingleCharPattern("\\n",'\n');
	}

	@Test(expected=PatternCompileException.class)
	public void testFalseInput1() {
		PatternParser parser = new PatternParser("a(b|c{1,)");
		parser.parse();
	}
	
	@Test(expected=PatternCompileException.class)
	public void testFalseInput2() {
		PatternParser parser = new PatternParser("a(b|c");
		parser.parse();
	}
	
	private void checkSingleCharPattern(String pattern,char c) {
		PatternParser parser = new PatternParser(pattern);
		PatternNode node = parser.parse();
		assertThat("failed on pattern '" + pattern + "'", node, instanceOf(SingleCharNode.class));
		assertThat("failed on pattern '" + pattern + "'", ((SingleCharNode) node).getValue(), equalTo(c));
	}
	
}
