package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.DefaultTokenType.ACCEPT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.almondtools.relex.automaton.GenericAutomaton;
import com.almondtools.relex.pattern.Finder;
import com.almondtools.relex.pattern.Match;
import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.pattern.SearchMatcherBuilder;

public class SearchMatcherBuilderForAutomatonTest {

	@Test
	public void testFind() throws Exception {
		GenericAutomaton automaton = Pattern.compileGenericAutomaton("c");
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(automaton);
		Finder matcher = builder.buildFinder("abc");
		assertThat(matcher.find(), is(true));
	}

	@Test
	public void testFindNot() throws Exception {
		GenericAutomaton automaton = Pattern.compileGenericAutomaton("c");
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(automaton);
		Finder matcher = builder.buildFinder("ab");
		assertThat(matcher.find(), is(false));
	}

	@Test
	public void testFindDFA() throws Exception {
		GenericAutomaton automaton = Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+");
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(automaton);
		Finder matcher = builder.buildFinder("xxxabcdefg");
		assertThat(matcher.find(), is(true));
		assertThat(matcher.match.text(), equalTo("abcdefg"));
	}
	
	@Test
	public void testFindDFAwithMismatches() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxbcdexxxxxxxxxxbcdebcdebcdebcdexxxbcdexxxab");
		List<Match> all = matcher.findAll();
		assertThat(all, hasSize(1));
		assertThat(all.get(0).text(), equalTo("ab"));
	}

	@Test
	public void testPattern2() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxabcdefgxxxxxxxxxabgxxxagabxxx");
		List<Match> findAll = matcher.findAll();
		assertThat(findAll, contains(new Match(35, "abcdefg", ACCEPT), new Match(51, "abg", ACCEPT), new Match(57, "agab", ACCEPT)));
	}
}
