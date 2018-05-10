package com.almondtools.rexlex.pattern;

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.automaton.GenericAutomaton.TreeWalker;
import com.almondtools.rexlex.automaton.GenericAutomatonBuilder;
import com.almondtools.rexlex.tokens.Accept;
import com.almondtools.rexlex.tokens.Info;

import net.amygdalum.regexparser.RegexNode;

public class PatternMatchTest {

	private static final RemainderTokenType REMAINDER = new RemainderTokenType(Accept.REMAINDER);

	private static final char MAX_VALUE_DEC = before(MAX_VALUE);
	private static final char MIN_VALUE_INC = after(MIN_VALUE);

	@Rule
	public PatternRule patterns = new PatternRule();

	@Test
	public void testDotNotMatchesAll() throws Exception {
		Pattern pattern = patterns.compile(".+");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("b\n").matches());
		assertFalse(pattern.matcher("b\r").matches());
		assertFalse(pattern.matcher("b\u0085").matches());
		assertFalse(pattern.matcher("b\u2028").matches());
		assertFalse(pattern.matcher("b\u2029").matches());
		Finder bna = pattern.finder("b\na");
		assertTrue(bna.find());
		assertThat(bna.group(), equalTo("b"));
		assertTrue(bna.find());
		assertThat(bna.group(), equalTo("a"));
		Finder bn = pattern.finder("b\n");
		assertTrue(bn.find());
		assertThat(bn.group(), equalTo("b"));
		assertFalse(bn.find());
	}

	@Test
	public void testDotMatchesAll() throws Exception {
		Pattern pattern = patterns.compile(".*", PatternFlag.DOTALL);
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("aa").matches());
		assertTrue(pattern.matcher("b\n").matches());
		assertTrue(pattern.matcher("b\r").matches());
		assertTrue(pattern.matcher("b\u0085").matches());
		assertTrue(pattern.matcher("b\u2028").matches());
		assertTrue(pattern.matcher("b\u2029").matches());
	}

	@Test
	public void testAlternatives() throws Exception {
		Pattern pattern = patterns.compile("a|b");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("b").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("ab").matches());
	}

	@Test
	public void testConjunctive() {
		Pattern pattern = patterns.compile("a&a*");
		assertTrue(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("b").matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testConcat() throws Exception {
		Pattern pattern = patterns.compile("ab*");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("ab").matches());
		assertTrue(pattern.matcher("abb").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("b").matches());
	}

	@Test
	public void testLoop() throws Exception {
		Pattern pattern = patterns.compile("a{1,2}");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("aaa").matches());
	}

	@Test
	public void testMoreItemsLoop() throws Exception {
		Pattern pattern = patterns.compile("a{1,3}");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("aa").matches());
		assertTrue(pattern.matcher("aaa").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("aaaa").matches());
	}

	@Test
	public void testOptional() throws Exception {
		Pattern pattern = patterns.compile("a?");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("").matches());
		assertFalse(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("b").matches());
	}

	@Test
	public void testProChar() throws Exception {
		Pattern pattern = patterns.compile(".");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("b").matches());
		assertTrue(pattern.matcher("c").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("ab").matches());
		assertFalse(pattern.matcher("bc").matches());
	}

	@Test
	public void testRangeChar() throws Exception {
		Pattern pattern = patterns.compile("[b-c]");
		assertTrue(pattern.matcher("b").matches());
		assertTrue(pattern.matcher("c").matches());
		assertFalse(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("d").matches());
		assertFalse(pattern.matcher("bc").matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testSingleChar() throws Exception {
		Pattern pattern = patterns.compile("a");
		assertTrue(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testString() throws Exception {
		Pattern pattern = patterns.compile("abc");
		assertTrue(pattern.matcher("abc").matches());
		assertFalse(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("b").matches());
		assertFalse(pattern.matcher("c").matches());
		assertFalse(pattern.matcher("abcd").matches());
	}

	@Test
	public void testEmpty() throws Exception {
		Pattern pattern = patterns.compile("");
		assertTrue(pattern.matcher("").matches());
		assertFalse(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("b").matches());
	}

	@Test
	public void testGroup() throws Exception {
		Pattern pattern = patterns.compile("(a)");
		assertTrue(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("aa").matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testMatchPattern1() throws Exception {
		Pattern pattern = patterns.compile("ab*c", REMAINDER);
		assertTrue(pattern.matcher("ac").matches());
		assertTrue(pattern.matcher("abc").matches());
		assertTrue(pattern.matcher("abbc").matches());
		assertTrue(pattern.matcher("abbbc").matches());
	}

	@Test
	public void testMatchPattern1WithInfo() throws Exception {
		Pattern pattern = patterns.compile("ab*c", REMAINDER, new InsertInfo());
		assertTrue(pattern.matcher("ac").matches());
		assertTrue(pattern.matcher("abc").matches());
		assertTrue(pattern.matcher("abbc").matches());
		assertTrue(pattern.matcher("abbbc").matches());
	}

	@Test
	public void testMatchPattern2() throws Exception {
		Pattern pattern = patterns.compile("(([^:]+)://)?([^:/]+)(:([0-9]+))?(/.*)", REMAINDER);
		assertTrue(pattern.matcher("http://www.linux.com/").matches());
		assertTrue(pattern.matcher("http://www.thelinuxshow.com/main.php3").matches());
	}

	@Test
	public void testMatchPattern2withInfo() throws Exception {
		Pattern pattern = patterns.compile("(([^:]+)://)?([^:/]+)(:([0-9]+))?(/.*)", REMAINDER, new InsertInfo());
		assertTrue(pattern.matcher("http://www.linux.com/").matches());
		assertTrue(pattern.matcher("http://www.thelinuxshow.com/main.php3").matches());
	}

	@Test
	@PatternCompilationMode.Exclude
	public void testFindOrdinaryPattern1() throws Exception {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("ab*c");
		java.util.regex.Matcher matcher = pattern.matcher("xxxabbbbcxxxx");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(3));
		assertThat(matcher.end(), equalTo(9));
		assertThat(matcher.group(), equalTo("abbbbc"));
	}

	@Test
	public void testFindPattern1() throws Exception {
		Pattern pattern = patterns.compile("ab*c");
		Finder matcher = pattern.finder("xxxabbbbcxxxx");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(3l));
		assertThat(matcher.end(), equalTo(9l));
		assertThat(matcher.group(), equalTo("abbbbc"));
	}

	@Test
	public void testFindPattern1withInfo() throws Exception {
		Pattern pattern = patterns.compile("ab*c", new InsertInfo());
		Finder matcher = pattern.finder("xxxabbbbcxxxx");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(3l));
		assertThat(matcher.end(), equalTo(9l));
		assertThat(matcher.group(), equalTo("abbbbc"));
	}

	@Test
	@PatternCompilationMode.Exclude
	public void testFindOrdinaryPattern3() throws Exception {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("ab*c");
		java.util.regex.Matcher matcher = pattern.matcher("abbxxxabbcxxxx");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(6));
		assertThat(matcher.end(), equalTo(10));
		assertThat(matcher.group(), equalTo("abbc"));
	}

	@Test
	public void testFindPattern3() throws Exception {
		Pattern pattern = patterns.compile("ab*c");
		Finder matcher = pattern.finder("abbxxxabbcxxxx");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(6l));
		assertThat(matcher.end(), equalTo(10l));
		assertThat(matcher.group(), equalTo("abbc"));
	}

	@Test
	public void testFindPattern3withInfo() throws Exception {
		Pattern pattern = patterns.compile("ab*c", new InsertInfo());
		Finder matcher = pattern.finder("abbxxxabbcxxxx");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(6l));
		assertThat(matcher.end(), equalTo(10l));
		assertThat(matcher.group(), equalTo("abbc"));
	}

	@Test
	public void testFindPattern4() throws Exception {
		Pattern pattern = patterns.compile("(gc)*(g|c|a|t){0,10}ccggaaagcattcgcaaa", new InsertInfo());
		Finder matcher = pattern.finder("actgcgcgcagctacgatccggaaagcattcgcaaattt");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(3l));
		assertThat(matcher.end(), equalTo(36l));
		assertThat(matcher.group(), equalTo("gcgcgcagctacgatccggaaagcattcgcaaa"));
	}
	
	@Test
	public void testCharClassWithSingleDash() throws Exception {
		Pattern pattern = patterns.compile("[b-]");
		assertTrue(pattern.matcher("b").matches());
		assertTrue(pattern.matcher("-").matches());
		assertFalse(pattern.matcher("a").matches());
	}

	@Test
	public void testCharClassWithEscapedChar() throws Exception {
		Pattern pattern = patterns.compile("[\\n]");
		assertTrue(pattern.matcher("\n").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("a").matches());
	}

	@Test
	public void testCharClassWithComplement() throws Exception {
		Pattern pattern = patterns.compile("[^bd]");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("c").matches());
		assertTrue(pattern.matcher("e").matches());
		assertFalse(pattern.matcher("").matches());
		assertFalse(pattern.matcher("b").matches());
		assertFalse(pattern.matcher("d").matches());
	}

	@Test
	public void testCompClassWithMinValue() throws Exception {
		Pattern pattern = patterns.compile("[^" + MIN_VALUE + "]", PatternFlag.DOTALL);
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher(String.valueOf(MIN_VALUE_INC)).matches());
		assertTrue(pattern.matcher(String.valueOf(MAX_VALUE)).matches());
		assertFalse(pattern.matcher(String.valueOf(MIN_VALUE)).matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testCompClassWithMinValueInc() throws Exception {
		Pattern pattern = patterns.compile("[^" + MIN_VALUE_INC + "]", PatternFlag.DOTALL);
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher(String.valueOf(MIN_VALUE)).matches());
		assertTrue(pattern.matcher(String.valueOf(MAX_VALUE)).matches());
		assertFalse(pattern.matcher(String.valueOf(MIN_VALUE_INC)).matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testCompClassWithMaxValue() throws Exception {
		Pattern pattern = patterns.compile("[^" + MAX_VALUE + "]");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher(String.valueOf(MAX_VALUE_DEC)).matches());
		assertTrue(pattern.matcher(String.valueOf(MIN_VALUE)).matches());
		assertFalse(pattern.matcher(String.valueOf(MAX_VALUE)).matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testCompClassWithMaxValueDec() throws Exception {
		Pattern pattern = patterns.compile("[^" + MAX_VALUE_DEC + "]");
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher(String.valueOf(MAX_VALUE)).matches());
		assertTrue(pattern.matcher(String.valueOf(MIN_VALUE)).matches());
		assertFalse(pattern.matcher(String.valueOf(MAX_VALUE_DEC)).matches());
		assertFalse(pattern.matcher("").matches());
	}

	@Test
	public void testOverlappingCharClassRanges() throws Exception {
		Pattern pattern = patterns.compile("[b-cc-d]");
		assertTrue(pattern.matcher("b").matches());
		assertTrue(pattern.matcher("c").matches());
		assertTrue(pattern.matcher("d").matches());
		assertFalse(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("e").matches());
	}

	@Test
	public void testOverlappingCharClassRangesSameStart() throws Exception {
		Pattern pattern = patterns.compile("[b-cb-d]");
		assertTrue(pattern.matcher("b").matches());
		assertTrue(pattern.matcher("c").matches());
		assertTrue(pattern.matcher("d").matches());
		assertFalse(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("e").matches());
	}

	@Test
	public void testBoundedLoop() throws Exception {
		Pattern pattern = patterns.compile("a{4,6}");
		assertTrue(pattern.matcher("aaaa").matches());
		assertTrue(pattern.matcher("aaaaa").matches());
		assertTrue(pattern.matcher("aaaaaa").matches());
		assertFalse(pattern.matcher("aaa").matches());
		assertFalse(pattern.matcher("aaaaaaa").matches());
		assertFalse(pattern.matcher("aaaaaaaa").matches());
		assertFalse(pattern.matcher("aaaaaaaaaaaa").matches());
	}

	@Test
	public void testLargeBoundedLoop() throws Exception {
		Pattern pattern = patterns.compile("a{4,60}");
		assertTrue(pattern.matcher("aaaa").matches());
		assertTrue(pattern.matcher(new String(new char[60]).replace('\0', 'a')).matches());
		assertFalse(pattern.matcher("aaa").matches());
		assertFalse(pattern.matcher(new String(new char[61]).replace('\0', 'a')).matches());
	}

	@Test(expected = RuntimeException.class)
	public void testUnclosedCharClass() throws Exception {
		patterns.compile("[a-b");
	}

	@Test(expected = RuntimeException.class)
	public void testUnclosedLoop() throws Exception {
		patterns.compile("a{1,2");
	}

	@Test(expected = RuntimeException.class)
	public void testOpenBracket() throws Exception {
		patterns.compile("[");
	}

	@Test
	public void testBug1() throws Exception {
		Pattern pattern = patterns.compile("ggc(g|a)*cg");
		Finder matcher = pattern.finder("gccaggcttccggcaggcgcggcaggcggtcgcggaaatcggcgcggtagcgagcggtatctccgg");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(11l));
		assertThat(matcher.end(), equalTo(19l));
		assertThat(matcher.group(), equalTo("ggcaggcg"));

		success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(20l));
		assertThat(matcher.end(), equalTo(28l));
		assertThat(matcher.group(), equalTo("ggcaggcg"));
	}

	@Test
	public void testBug2() throws Exception {
		Pattern pattern = patterns.compile("ED(EE)?E+D+");
		Finder matcher = pattern.finder("EEDEDEEEDEGKGVSSEKVKKAKAKSR");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(1l));
		assertThat(matcher.end(), equalTo(5l));
		assertThat(matcher.group(), equalTo("EDED"));

		success = matcher.find();
		assertFalse(success);
	}
	
	@Test
	public void testBug3() throws Exception {
		Pattern pattern = patterns.compile("(a*tgc*|t*acg*)*(cg){1,20}(a|t)*");
		Finder matcher = pattern.finder("ggggggaatgcccgcgcgataattaagggggggggggggg");
		boolean success = matcher.find();
		assertTrue(success);
		assertThat(matcher.start(), equalTo(6l));
		assertThat(matcher.end(), equalTo(26l));
		assertThat(matcher.group(), equalTo("aatgcccgcgcgataattaa"));

		assertFalse(matcher.find());
	}
	
	

	private static class InsertInfo extends GenericAutomatonBuilder {
		@Override
		public GenericAutomaton buildFrom(RegexNode node) {
			return insertInfo(super.buildFrom(node), Info.INFO);
		}

		@Override
		public GenericAutomaton buildFrom(RegexNode node, TokenType type) {
			return insertInfo(super.buildFrom(node, type), Info.INFO);
		}

		public GenericAutomaton insertInfo(GenericAutomaton automaton, final TokenType info) {
			new TreeWalker<TreeWalker<?>>(automaton.getStart()) {

				public TreeWalker<?> self() {
					return this;
				}

				@Override
				public void apply(State state) {
					state.setType(info);
				}
			}.apply();
			return automaton;
		}
	}

}