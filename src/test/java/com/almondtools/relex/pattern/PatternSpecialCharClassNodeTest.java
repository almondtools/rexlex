package com.almondtools.relex.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.almondtools.relex.pattern.Pattern.CharNode;
import com.almondtools.relex.pattern.Pattern.RangeCharNode;
import com.almondtools.relex.pattern.Pattern.SpecialCharClassNode;

public class PatternSpecialCharClassNodeTest {

	@Test
	public void testToString() throws Exception {
		SpecialCharClassNode specialCharNode = new SpecialCharClassNode('d', Arrays.asList((CharNode) new RangeCharNode('0', '9')));
		assertThat(specialCharNode.toString(), equalTo("\\d"));
	}

	@Test
	public void testGetSymbol() throws Exception {
		SpecialCharClassNode specialCharNode = new SpecialCharClassNode('d', Arrays.asList((CharNode) new RangeCharNode('0', '9')));
		assertThat(specialCharNode.getSymbol(), equalTo('d'));
	}

	@Test
	public void testToCharNodes() throws Exception {
		SpecialCharClassNode specialCharNode = new SpecialCharClassNode('d', Arrays.asList((CharNode) new RangeCharNode('0', '9')));
		assertThat(specialCharNode.toCharNodes(), hasSize(1));
		assertThat(specialCharNode.toCharNodes().get(0).toString(), equalTo("0-9"));
	}

	@Test
	public void testInvert() throws Exception {
		SpecialCharClassNode specialCharNode = new SpecialCharClassNode('d', Arrays.asList((CharNode) new RangeCharNode('0', '9')));
		SpecialCharClassNode intertedCharNode = specialCharNode.invert();
		assertThat(intertedCharNode.toString(), equalTo("\\D"));
		assertThat(intertedCharNode.toCharNodes(), hasSize(2));
		assertThat(intertedCharNode.toCharNodes().get(0).getFrom(), equalTo(Character.MIN_VALUE));
		assertThat(intertedCharNode.toCharNodes().get(0).getTo(), equalTo((char) ('0' - 1)));
		assertThat(intertedCharNode.toCharNodes().get(1).getFrom(), equalTo((char) ('9' + 1)));
		assertThat(intertedCharNode.toCharNodes().get(1).getTo(), equalTo(Character.MAX_VALUE));
	}
}