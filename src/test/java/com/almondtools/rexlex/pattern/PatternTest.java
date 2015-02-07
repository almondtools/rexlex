package com.almondtools.rexlex.pattern;

import static com.almondtools.rexlex.pattern.ProCharMatcher.containsChars;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.almondtools.rexlex.pattern.Pattern;
import com.almondtools.rexlex.pattern.Pattern.CharClassNode;
import com.almondtools.rexlex.pattern.Pattern.CharNode;
import com.almondtools.rexlex.pattern.Pattern.RangeCharNode;
import com.almondtools.rexlex.pattern.Pattern.SingleCharNode;

public class PatternTest {

	@Test
	public void testPattern() throws Exception {
		assertThat(Pattern.compile("ab*").pattern(), equalTo("ab*"));
	}

	@Test
	public void testFinder() throws Exception {
		Pattern pattern = Pattern.compile("ab*");
		assertThat(pattern.matcher("a").matches(), is(true));
		assertThat(pattern.matcher("ab").matches(), is(true));
		assertThat(pattern.matcher("abb").matches(), is(true));
		assertThat(pattern.matcher("").matches(), is(false));
	}

	@Test
	public void testNullPattern() throws Exception {
		Pattern pattern = Pattern.compile(null);
		assertThat(pattern.matcher("").matches(), is(false));
		assertThat(pattern.matcher("a").matches(), is(false));
	}

	@Test
	public void testEmptyPattern() throws Exception {
		Pattern pattern = Pattern.compile("");
		assertThat(pattern.matcher("").matches(), is(true));
		assertThat(pattern.matcher("a").matches(), is(false));
	}

	@Test
	public void testUnionEqual() throws Exception {
		assertThat(new SingleCharNode('a').union(new SingleCharNode('a')), containsChars('a'));
		assertThat(new SingleCharNode('a').union(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testUnionDisjunctNonAdjacent() throws Exception {
		assertThat(new SingleCharNode('a').union(new SingleCharNode('c')), containsChars('a', 'c'));
		assertThat(new SingleCharNode('a').union(new SingleCharNode('c')).toCharNodes(), hasSize(2));
		assertThat(new SingleCharNode('c').union(new SingleCharNode('a')), containsChars('a', 'c'));
		assertThat(new SingleCharNode('c').union(new SingleCharNode('a')).toCharNodes(), hasSize(2));
	}

	@Test
	public void testUnionDisjunctAdjacent() throws Exception {
		assertThat(new SingleCharNode('a').union(new SingleCharNode('b')), containsChars('a', 'b'));
		assertThat(new SingleCharNode('a').union(new SingleCharNode('b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('b').union(new SingleCharNode('a')), containsChars('a', 'b'));
		assertThat(new SingleCharNode('b').union(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testUnionSubsuming() throws Exception {
		assertThat(new RangeCharNode('a', 'b').union(new SingleCharNode('b')), containsChars('a', 'b'));
		assertThat(new RangeCharNode('a', 'b').union(new SingleCharNode('b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('b').union(new RangeCharNode('a', 'b')), containsChars('a', 'b'));
		assertThat(new SingleCharNode('b').union(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('a').union(new RangeCharNode('a', 'b')), containsChars('a', 'b'));
		assertThat(new SingleCharNode('a').union(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
		assertThat(new RangeCharNode('a', 'b').union(new SingleCharNode('a')), containsChars('a', 'b'));
		assertThat(new RangeCharNode('a', 'b').union(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testUnionOverlapping() throws Exception {
		assertThat(new RangeCharNode('a', 'b').union(new RangeCharNode('b', 'c')), containsChars('a', 'b', 'c'));
		assertThat(new RangeCharNode('a', 'b').union(new RangeCharNode('b', 'c')).toCharNodes(), hasSize(1));
		assertThat(new RangeCharNode('b', 'c').union(new RangeCharNode('a', 'b')), containsChars('a', 'b', 'c'));
		assertThat(new RangeCharNode('b', 'c').union(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testUnionComplex() throws Exception {
		CharClassNode charClassAB_DE = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('a', 'b'),
			new RangeCharNode('d', 'e')
			));
		CharClassNode charClassBC_G = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('b', 'c'),
			new SingleCharNode('g')
			));
		assertThat(charClassAB_DE.union(charClassBC_G), containsChars('a', 'b', 'c', 'd', 'e', 'g'));
		assertThat(charClassAB_DE.union(charClassBC_G).toCharNodes(), hasSize(2));
		assertThat(charClassBC_G.union(charClassAB_DE), containsChars('a', 'b', 'c', 'd', 'e', 'g'));
		assertThat(charClassBC_G.union(charClassAB_DE).toCharNodes(), hasSize(2));
	}

	@Test
	public void testIntersectEqual() throws Exception {
		assertThat(new SingleCharNode('a').intersect(new SingleCharNode('a')), containsChars('a'));
		assertThat(new SingleCharNode('a').intersect(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testIntersectDisjunctNonAdjacent() throws Exception {
		assertThat(new SingleCharNode('a').intersect(new SingleCharNode('c')), nullValue());
		assertThat(new SingleCharNode('c').intersect(new SingleCharNode('a')), nullValue());
	}

	@Test
	public void testIntersectDisjunctAdjacent() throws Exception {
		assertThat(new SingleCharNode('a').intersect(new SingleCharNode('b')), nullValue());
		assertThat(new SingleCharNode('b').intersect(new SingleCharNode('a')), nullValue());
	}

	@Test
	public void testIntersectSubsuming() throws Exception {
		assertThat(new RangeCharNode('a', 'b').intersect(new SingleCharNode('b')), containsChars('b'));
		assertThat(new RangeCharNode('a', 'b').intersect(new SingleCharNode('b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('b').intersect(new RangeCharNode('a', 'b')), containsChars('b'));
		assertThat(new SingleCharNode('b').intersect(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('a').intersect(new RangeCharNode('a', 'b')), containsChars('a'));
		assertThat(new SingleCharNode('a').intersect(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
		assertThat(new RangeCharNode('a', 'b').intersect(new SingleCharNode('a')), containsChars('a'));
		assertThat(new RangeCharNode('a', 'b').intersect(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testIntersectOverlapping() throws Exception {
		assertThat(new RangeCharNode('a', 'b').intersect(new RangeCharNode('b', 'c')), containsChars('b'));
		assertThat(new RangeCharNode('a', 'b').intersect(new RangeCharNode('b', 'c')).toCharNodes(), hasSize(1));
		assertThat(new RangeCharNode('b', 'c').intersect(new RangeCharNode('a', 'b')), containsChars('b'));
		assertThat(new RangeCharNode('b', 'c').intersect(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testintersectComplex() throws Exception {
		CharClassNode charClassAC_EG = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('a', 'c'),
			new RangeCharNode('e', 'g')
			));
		CharClassNode charClassCF_H = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('c', 'f'),
			new SingleCharNode('h')
			));
		assertThat(charClassAC_EG.intersect(charClassCF_H), containsChars('c', 'e', 'f'));
		assertThat(charClassAC_EG.intersect(charClassCF_H).toCharNodes(), hasSize(2));
		assertThat(charClassCF_H.intersect(charClassAC_EG), containsChars('c', 'e', 'f'));
		assertThat(charClassCF_H.intersect(charClassAC_EG).toCharNodes(), hasSize(2));
	}

	@Test
	public void testMinusEqual() throws Exception {
		assertThat(new SingleCharNode('a').minus(new SingleCharNode('a')), nullValue());
	}

	@Test
	public void testMinusDisjunctNonAdjacent() throws Exception {
		assertThat(new SingleCharNode('a').minus(new SingleCharNode('c')), containsChars('a'));
		assertThat(new SingleCharNode('a').minus(new SingleCharNode('c')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('c').minus(new SingleCharNode('a')), containsChars('c'));
		assertThat(new SingleCharNode('c').minus(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testMinusDisjunctAdjacent() throws Exception {
		assertThat(new SingleCharNode('a').minus(new SingleCharNode('b')), containsChars('a'));
		assertThat(new SingleCharNode('a').minus(new SingleCharNode('b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('b').minus(new SingleCharNode('a')), containsChars('b'));
		assertThat(new SingleCharNode('b').minus(new SingleCharNode('a')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testMinusSubsuming() throws Exception {
		assertThat(new RangeCharNode('a', 'b').minus(new SingleCharNode('b')), containsChars('a'));
		assertThat(new RangeCharNode('a', 'b').minus(new SingleCharNode('b')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('b').minus(new RangeCharNode('a', 'b')), nullValue());
		assertThat(new RangeCharNode('a', 'b').minus(new SingleCharNode('a')), containsChars('b'));
		assertThat(new RangeCharNode('a', 'b').minus(new SingleCharNode('a')).toCharNodes(), hasSize(1));
		assertThat(new SingleCharNode('a').minus(new RangeCharNode('a', 'b')), nullValue());
	}

	@Test
	public void testMinusOverlapping() throws Exception {
		assertThat(new RangeCharNode('a', 'b').minus(new RangeCharNode('b', 'c')), containsChars('a'));
		assertThat(new RangeCharNode('a', 'b').minus(new RangeCharNode('b', 'c')).toCharNodes(), hasSize(1));
		assertThat(new RangeCharNode('b', 'c').minus(new RangeCharNode('a', 'b')), containsChars('c'));
		assertThat(new RangeCharNode('b', 'c').minus(new RangeCharNode('a', 'b')).toCharNodes(), hasSize(1));
	}

	@Test
	public void testMinusComplex() throws Exception {
		CharClassNode charClassAC_EG = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('a', 'c'),
			new RangeCharNode('e', 'g')
			));
		CharClassNode charClassAC_EH = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('a', 'c'),
			new RangeCharNode('e', 'h')
			));
		CharClassNode charClassCF_H = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('c', 'f'),
			new SingleCharNode('h')
			));
		CharClassNode charClassCF = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('c', 'f')
			));
		CharClassNode charClassCE_H = new CharClassNode(Arrays.asList((CharNode)
			new RangeCharNode('c', 'e'),
			new SingleCharNode('h')
			));
		assertThat(charClassAC_EG.minus(charClassCF_H), containsChars('a', 'b', 'g'));
		assertThat(charClassAC_EG.minus(charClassCF_H).toCharNodes(), hasSize(2));
		assertThat(charClassCF_H.minus(charClassAC_EG), containsChars('d', 'h'));
		assertThat(charClassCF_H.minus(charClassAC_EG).toCharNodes(), hasSize(2));
		assertThat(charClassAC_EH.minus(charClassCF), containsChars('a', 'b', 'g', 'h'));
		assertThat(charClassAC_EH.minus(charClassCF).toCharNodes(), hasSize(2));
		assertThat(charClassAC_EH.minus(charClassCF_H), containsChars('a', 'b', 'g'));
		assertThat(charClassAC_EH.minus(charClassCF_H).toCharNodes(), hasSize(2));
		assertThat(charClassAC_EG.minus(charClassCE_H), containsChars('a', 'b', 'f', 'g'));
		assertThat(charClassAC_EG.minus(charClassCE_H).toCharNodes(), hasSize(2));
	}

}
