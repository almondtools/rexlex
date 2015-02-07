package com.almondtools.rexlex.pattern;

import static com.almondtools.rexlex.pattern.DefaultTokenType.ACCEPT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.rexlex.automaton.GenericAutomaton;
import com.almondtools.rexlex.pattern.Finder;
import com.almondtools.rexlex.pattern.Match;
import com.almondtools.rexlex.pattern.OptimizedMatcherBuilder;
import com.almondtools.rexlex.pattern.Pattern;

public class OptimizedMatcherBuilderForAutomatonTest {

	@Test
	public void testFind() throws Exception {
		GenericAutomaton automaton = Pattern.compileGenericAutomaton("c");
		OptimizedMatcherBuilder builder = OptimizedMatcherBuilder.from(automaton);
		Finder matcher = builder.buildFinder("abc");
		assertThat(matcher.find(), is(true));
	}

	@Test
	public void testFindNot() throws Exception {
		GenericAutomaton automaton = Pattern.compileGenericAutomaton("c");
		OptimizedMatcherBuilder builder = OptimizedMatcherBuilder.from(automaton);
		Finder matcher = builder.buildFinder("ab");
		assertThat(matcher.find(), is(false));
	}

	@Test
	public void testFindDFA() throws Exception {
		GenericAutomaton automaton = Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+");
		OptimizedMatcherBuilder builder = OptimizedMatcherBuilder.from(automaton);
		Finder matcher = builder.buildFinder("xxxabcdefg");
		assertThat(matcher.find(), is(true));
		assertThat(matcher.match.text(), equalTo("abcdefg"));
	}
	
	@Test
	public void testFindDFAwithMismatches() throws Exception {
		OptimizedMatcherBuilder builder = OptimizedMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxbcdexxxxxxxxxxbcdebcdebcdebcdexxxbcdexxxab");
		assertThat(matcher.find(), is(true));
		assertThat(matcher.match.text(), equalTo("ab"));
	}

	@Test
	public void testPattern2() throws Exception {
		OptimizedMatcherBuilder builder = OptimizedMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxabcdefgxxxxxxxxxabgxxxagabxxx");
		assertThat(matcher.findAll(), contains(new Match(35, "abcdefg", ACCEPT), new Match(51, "abg", ACCEPT), new Match(57, "agab", ACCEPT)));
	}
}
