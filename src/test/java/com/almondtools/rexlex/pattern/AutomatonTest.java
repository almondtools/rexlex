package com.almondtools.rexlex.pattern;

import static com.almondtools.rexlex.automaton.Automatons.matchSamples;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.almondtools.rexlex.tokens.Accept;
import com.almondtools.rexlex.automaton.Automaton;
import com.almondtools.rexlex.pattern.RemainderTokenType;

public class AutomatonTest {

	private static final RemainderTokenType REMAINDER = new RemainderTokenType(Accept.REMAINDER);

	@Rule
	public AutomatonRule compiler = new AutomatonRule();

	@Test
	public void testNullPattern() throws Exception {
		Automaton pa = compiler.compile((String) null);
		assertThat(matchSamples(pa, ""), empty());
	}

	@Test
	public void testNullPatternWithToken() throws Exception {
		Automaton pa = compiler.compile((String) null, REMAINDER);
		assertThat(matchSamples(pa, ""), empty());
	}

	@Test
	public void testEscapeClassesS() throws Exception {
		Automaton sa = compiler.compile("\\s+", REMAINDER);
		assertThat(matchSamples(sa," "), contains(" "));
		assertThat(matchSamples(sa," \t\n"), contains(" \t\n"));
		assertThat(matchSamples(sa,"a"), empty());
		assertThat(matchSamples(sa,"\\n"), empty());

		Automaton Sa = compiler.compile("\\S+", REMAINDER);
		assertThat(matchSamples(Sa,"(a&b|c*\\\\n)"), contains("(a&b|c*\\\\n)"));
		assertThat(matchSamples(Sa," "), empty());
		assertThat(matchSamples(Sa,"\n \t"), empty());
	}

	@Test
	public void testEscapeClassesW() throws Exception {
		Automaton wa = compiler.compile("\\w+", REMAINDER);
		assertThat(matchSamples(wa,"1aA"), contains("1aA"));
		assertThat(matchSamples(wa,"ß"), empty());
		assertThat(matchSamples(wa,"?"), empty());
		assertThat(matchSamples(wa," "), empty());

		Automaton Wa = compiler.compile("\\W+", REMAINDER);
		assertThat(matchSamples(Wa,"ßü #"), contains("ßü #"));
		assertThat(matchSamples(Wa,"a"), empty());
		assertThat(matchSamples(Wa,"1"), empty());
		assertThat(matchSamples(Wa,"B"), empty());
	}

	@Test
	public void testEscapeClassesD() throws Exception {
		Automaton da = compiler.compile("\\d+", REMAINDER);
		assertThat(matchSamples(da,"1234567890"), contains("1234567890"));
		assertThat(matchSamples(da,"a"), empty());
		assertThat(matchSamples(da,"?"), empty());
		assertThat(matchSamples(da," "), empty());

		Automaton Da = compiler.compile("\\D+", REMAINDER);
		assertThat(matchSamples(Da,"a? "), contains("a? "));
		assertThat(matchSamples(Da,"1"), empty());
		assertThat(matchSamples(Da,"2"), empty());
		assertThat(matchSamples(Da,"3"), empty());
	}

	@Test
	public void testOnCharIsConstant() throws Exception {
		Automaton a = compiler.compile("a");
		assertThat(a.getProperty().isLinear(), equalTo(true));
	}

	@Test
	public void testStringsAreConstant() throws Exception {
		Automaton a = compiler.compile("abcde");
		assertThat(a.getProperty().isLinear(), equalTo(true));
	}

	@Test
	public void testInfiniteCharsAreNotConstant() throws Exception {
		Automaton a = compiler.compile("a*");
		assertThat(a.getProperty().isLinear(), equalTo(false));
	}

	@Test
	public void testAlternativeCharsAreNotConstant() throws Exception {
		Automaton a = compiler.compile("a|b");
		assertThat(a.getProperty().isLinear(), equalTo(false));
	}

	@Test
	public void testConcatsOfConstantAreConstant() throws Exception {
		Automaton a = compiler.compile("(a)(b)");
		assertThat(a.getProperty().isLinear(), equalTo(true));
	}

	@Test
	public void testConcatsOfNonConstantAreNotConstant() throws Exception {
		Automaton a = compiler.compile("(a)(b|c)");
		assertThat(a.getProperty().isLinear(), equalTo(false));
	}

	@Test
	public void testOnCharIsFinite() throws Exception {
		Automaton a = compiler.compile("a");
		assertThat(a.getProperty().isAcyclic(), equalTo(true));
	}

	@Test
	public void testStringsAreFinite() throws Exception {
		Automaton a = compiler.compile("abcde");
		assertThat(a.getProperty().isAcyclic(), equalTo(true));
	}

	@Test
	public void testInfiniteCharsAreNotFinite() throws Exception {
		Automaton a = compiler.compile("a*");
		assertThat(a.getProperty().isAcyclic(), equalTo(false));
	}

	@Test
	public void testAlternativeCharsAreFinite() throws Exception {
		Automaton a = compiler.compile("a|b");
		assertThat(a.getProperty().isAcyclic(), equalTo(true));
	}

	@Test
	public void testAlternativeStringsAreFinite() throws Exception {
		Automaton a = compiler.compile("abc|def");
		assertThat(a.getProperty().isAcyclic(), equalTo(true));
	}

	@Test
	public void testConcatsOfFiniteAreFinite() throws Exception {
		Automaton a = compiler.compile("(abc|def)gh");
		assertThat(a.getProperty().isAcyclic(), equalTo(true));
	}

	@Test
	public void testConcatsOfNonFiniteAreNotFinite() throws Exception {
		Automaton a = compiler.compile("(abc|def*)gh");
		assertThat(a.getProperty().isAcyclic(), equalTo(false));
	}

	@Test
	public void testGetSamplesOfConstant() throws Exception {
		Automaton automaton = compiler.compile("abcde");
		assertThat(automaton.getSamples(1), contains("abcde"));
		assertThat(automaton.getSamples(2), contains("abcde"));
	}

	@Test
	public void testGetPrefixesOfCharClass() throws Exception {
		Automaton automaton = compiler.compile("[ab][cd]");
		assertThat(automaton.getSamples(1), anyOf(contains("ac"), contains("ad"), contains("bc"), contains("bd")));
		assertThat(automaton.getSamples(4), containsInAnyOrder("ac", "ad", "bc", "bd"));
		assertThat(automaton.getSamples(5), containsInAnyOrder("ac", "ad", "bc", "bd"));
	}

	@Test
	public void testGetPrefixesOfAlternative() throws Exception {
		Automaton automaton = compiler.compile("abcde|fgh");
		assertThat(automaton.getSamples(1), contains("abcde"));
		assertThat(automaton.getSamples(2), containsInAnyOrder("abcde", "fgh"));
		assertThat(automaton.getSamples(3), containsInAnyOrder("abcde", "fgh"));
	}

	@Test
	public void testGetPrefixesOfLoop() throws Exception {
		Automaton automaton = compiler.compile("a*");
		assertThat(automaton.getSamples(1), contains(""));
		assertThat(automaton.getSamples(2), contains("", "a"));
		assertThat(automaton.getSamples(3), contains("", "a", "aa"));
	}

	@Test
	public void testGetPrefixesOfMixedFiniteInfinite() throws Exception {
		Automaton automaton = compiler.compile("a*|bb");
		assertThat(automaton.getSamples(1), anyOf(contains(""), contains("bb")));
		assertThat(automaton.getSamples(2), anyOf(containsInAnyOrder("", "a"), containsInAnyOrder("", "bb")));
	}

}