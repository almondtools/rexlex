package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.automaton.Automatons.matchSamples;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.automaton.GlushkovAutomatonBuilder;
import com.almondtools.rexlex.pattern.Pattern;
import com.almondtools.rexlex.pattern.PatternFlag;
import com.almondtools.rexlex.pattern.PatternOption;

public class GlushkovAutomatonBuilderTest {

	@Test
	public void testMatchChar() throws Exception {
		GenericAutomaton a = automatonOf("a");
		assertThat(matchSamples(a,"a"), contains("a"));
		assertThat(matchSamples(a,"aa"), empty());
		assertThat(matchSamples(a,"b"), empty());
		assertThat(matchSamples(a,""), empty());
	}

	@Test
	public void testMatchString() throws Exception {
		GenericAutomaton abc = automatonOf("abc");
		assertThat(matchSamples(abc,"abc"), contains("abc"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"bc"), empty());
		assertThat(matchSamples(abc,"ac"), empty());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"abcd"), empty());
	}

	@Test
	public void testMatchCharRange() throws Exception {
		GenericAutomaton abc = automatonOf("[a-c]");
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"b"), contains("b"));
		assertThat(matchSamples(abc,"c"), contains("c"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"bc"), empty());
		assertThat(matchSamples(abc,"ac"), empty());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"abcd"), empty());
	}

	@Test
	public void testMatchReverseCharRange() throws Exception {
		GenericAutomaton abc = automatonOf("[c-a]");
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"b"), contains("b"));
		assertThat(matchSamples(abc,"c"), contains("c"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"bc"), empty());
		assertThat(matchSamples(abc,"ac"), empty());
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"abcd"), empty());
	}

	@Test
	public void testMatchAnyCharOrdinary() throws Exception {
		GenericAutomaton abc = automatonOf(".");
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"z"), contains("z"));
		assertThat(matchSamples(abc,"1"), contains("1"));
		assertThat(matchSamples(abc,"&"), contains("&"));
		assertThat(matchSamples(abc,"\n"), empty());
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"aa"), empty());
		assertThat(matchSamples(abc,"a\n"), empty());
		assertThat(matchSamples(abc,"a&b"), empty());
		assertThat(matchSamples(abc,""), empty());
	}

	@Test
	public void testMatchAnyCharDotAll() throws Exception {
		GenericAutomaton abc = automatonOf(".", PatternFlag.DOTALL);
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"z"), contains("z"));
		assertThat(matchSamples(abc,"1"), contains("1"));
		assertThat(matchSamples(abc,"&"), contains("&"));
		assertThat(matchSamples(abc,"\n"), contains("\n"));
		assertThat(matchSamples(abc,"ab"), empty());
		assertThat(matchSamples(abc,"aa"), empty());
		assertThat(matchSamples(abc,"a\n"), empty());
		assertThat(matchSamples(abc,"a&b"), empty());
		assertThat(matchSamples(abc,""), empty());
	}
	
	@Test
	public void testMatchAnyCharOf() throws Exception {
		GenericAutomaton abc = automatonOf("[ab]");
		assertThat(matchSamples(abc,"a"), contains("a"));
		assertThat(matchSamples(abc,"b"), contains("b"));
		assertThat(matchSamples(abc,"c"), empty());
		assertThat(matchSamples(abc,"cc"), empty());
		assertThat(matchSamples(abc,""), empty());
	}

	@Test
	public void testMatchNothing() throws Exception {
		GenericAutomaton abc = automatonOf(null);
		assertThat(matchSamples(abc,""), empty());
		assertThat(matchSamples(abc,"a"), empty());
		assertThat(matchSamples(abc,"ab"), empty());
	}

	@Test
	public void testMatchEmpty() throws Exception {
		GenericAutomaton abc = automatonOf("");
		assertThat(matchSamples(abc,""), contains(""));
		assertThat(matchSamples(abc,"a"), empty());
		assertThat(matchSamples(abc,"ab"), empty());
	}

	@Test
	public void testOptional() throws Exception {
		GenericAutomaton ab_question = automatonOf("(ab)?");
		assertThat(matchSamples(ab_question,""), contains(""));
		assertThat(matchSamples(ab_question,"ab"), contains("ab"));
		assertThat(matchSamples(ab_question,"a"), empty());
		assertThat(matchSamples(ab_question,"b"), empty());
		assertThat(matchSamples(ab_question,"abc"), empty());
	}

	@Test
	public void testUnlimitedLoop0() throws Exception {
		GenericAutomaton a_star = automatonOf("a*");
		assertThat(matchSamples(a_star,""), contains(""));
		assertThat(matchSamples(a_star,"a"), contains("a"));
		assertThat(matchSamples(a_star,"aa"), contains("aa"));
		assertThat(matchSamples(a_star,"aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_star,"ab"), empty());
	}

	@Test
	public void testUnlimitedLoop1() throws Exception {
		GenericAutomaton a_plus = automatonOf("a+");
		assertThat(matchSamples(a_plus,"a"), contains("a"));
		assertThat(matchSamples(a_plus,"aa"), contains("aa"));
		assertThat(matchSamples(a_plus,"aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_plus,""), empty());
		assertThat(matchSamples(a_plus,"ab"), empty());
	}

	@Test
	public void testUnlimitedLoopN() throws Exception {
		GenericAutomaton a_minN = automatonOf("a{4,}");
		assertThat(matchSamples(a_minN,"aaaa"), contains("aaaa"));
		assertThat(matchSamples(a_minN,"aaaaaaaaaaaaaaaaaaaaa"), contains("aaaaaaaaaaaaaaaaaaaaa"));
		assertThat(matchSamples(a_minN,""), empty());
		assertThat(matchSamples(a_minN,"aaa"), empty());
		assertThat(matchSamples(a_minN,"ab"), empty());
	}

	@Test
	public void testRangeLoop() throws Exception {
		GenericAutomaton a_1_2 = automatonOf("a{1,2}");
		assertThat(matchSamples(a_1_2,"a"), contains("a"));
		assertThat(matchSamples(a_1_2,"aa"), contains("aa"));
		assertThat(matchSamples(a_1_2,""), empty());
		assertThat(matchSamples(a_1_2,"aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_1_2,"ab"), empty());
	}

	@Test
	public void testFixedRangeLoop() throws Exception {
		GenericAutomaton a_1_1 = automatonOf("a{1,1}");
		assertThat(matchSamples(a_1_1,"a"), contains("a"));
		assertThat(matchSamples(a_1_1,"aa"), empty());
		assertThat(matchSamples(a_1_1,""), empty());
		assertThat(matchSamples(a_1_1,"aaaaaaaaaaaaaaaaaaaaa"), empty());
	}

	@Test
	public void testMatchFixedLoop() throws Exception {
		GenericAutomaton a_2 = automatonOf("a{2}");
		assertThat(matchSamples(a_2,"aa"), contains("aa"));
		assertThat(matchSamples(a_2,""), empty());
		assertThat(matchSamples(a_2,"a"), empty());
		assertThat(matchSamples(a_2,"aaaaaaaaaaaaaaaaaaaaa"), empty());
		assertThat(matchSamples(a_2,"ab"), empty());
	}

	@Test
	public void testMatchConcatenation() throws Exception {
		GenericAutomaton aAndb = automatonOf("(a)(b)");
		assertThat(matchSamples(aAndb,"ab"), contains("ab"));
		assertThat(matchSamples(aAndb,""), empty());
		assertThat(matchSamples(aAndb,"a"), empty());
		assertThat(matchSamples(aAndb,"b"), empty());
		assertThat(matchSamples(aAndb,"abab"), empty());
	}

	@Test
	public void testMatchAlternatives() throws Exception {
		GenericAutomaton aOrb = automatonOf("a|b");
		assertThat(matchSamples(aOrb,"a"), contains("a"));
		assertThat(matchSamples(aOrb,"b"), contains("b"));
		assertThat(matchSamples(aOrb,""), empty());
		assertThat(matchSamples(aOrb,"ab"), empty());
		assertThat(matchSamples(aOrb,"abab"), empty());
	}
	
	private static GenericAutomaton automatonOf(String pattern, PatternOption... option) {
		List<PatternOption> options = new ArrayList<PatternOption>();
		options.add(new GlushkovAutomatonBuilder());
		options.addAll(Arrays.asList(option));
		return Pattern.compileGenericAutomaton(pattern, options);
	}
	
}

