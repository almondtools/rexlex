package com.almondtools.rexlex.pattern;

import static com.almondtools.rexlex.pattern.DefaultTokenType.ACCEPT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.almondtools.rexlex.automaton.GenericAutomaton;

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
		assertThat(matcher.match.text, equalTo("abcdefg"));
	}
	
	@Test
	public void testFindDFAwithMismatches() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxbcdexxxxxxxxxxbcdebcdebcdebcdexxxbcdexxxab");
		List<Match> all = findAll(matcher);
		assertThat(all, hasSize(1));
		assertThat(all.get(0).text, equalTo("ab"));
	}

	@Test
	public void testPattern2() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxabcdefgxxxxxxxxxabgxxxagabxxx");
		List<Match> findAll = findAll(matcher);
		assertThat(findAll, contains(Match.create(35, "abcdefg", ACCEPT), Match.create(51, "abg", ACCEPT), Match.create(57, "agab", ACCEPT)));
	}
	
	@Test
	public void testMatchPattern3() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("(([^:]+)://)?([^:/]+)(:([0-9]+))?(/.*)"));
		Finder matcher = builder.buildFinder("http://www.linux.com/\n"
			+ "http://www.thelinuxshow.com/main.php3\n"
			+ "http");
		List<Match> findAll = findAll(matcher);
		assertThat(findAll, contains(Match.create(0, "http://www.linux.com/", ACCEPT), Match.create(22, "http://www.thelinuxshow.com/main.php3", ACCEPT)));
	}

	@Test
	public void testBug2() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("ED(EE)?E+D+"));
		Finder matcher = builder.buildFinder("EEDEDEEEDEGKGVSSEKVKKAKAKSR");
		List<Match> findAll = findAll(matcher);
		assertThat(findAll, contains(Match.create(1, "EDED", ACCEPT)));
	}
	
	@Test
	public void testBug3() throws Exception {
		SearchMatcherBuilder builder = SearchMatcherBuilder.from(Pattern.compileGenericAutomaton("ggc(g|a)*cg"));
		Finder matcher = builder.buildFinder("ctgatattgcaaggggcgaccacgcttttggttttcttcatcggcaaggcgagcggcgcgtacatgaggcggcacattacgctg");
		List<Match> findAll = findAll(matcher);
		assertThat(findAll, contains(Match.create(42, "ggcaaggcg", ACCEPT), Match.create(54, "ggcgcg", ACCEPT)));
	}

	public List<Match> findAll(Finder matcher) {
		List<Match> matches = new ArrayList<Match>();
		while (matcher.find()) {
			matches.add(matcher.match.copy());
		}
		return matches;
	}

}
