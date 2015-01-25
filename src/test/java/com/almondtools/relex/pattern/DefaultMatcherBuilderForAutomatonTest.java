package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.DefaultTokenType.ACCEPT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.almondtools.relex.automaton.FromGenericAutomaton.ToCompactGenericAutomaton;
import com.almondtools.relex.pattern.DefaultMatcherBuilder;
import com.almondtools.relex.pattern.Finder;
import com.almondtools.relex.pattern.Match;
import com.almondtools.relex.pattern.Pattern;

public class DefaultMatcherBuilderForAutomatonTest {

	@Test
	public void testFind() throws Exception {
		DefaultMatcherBuilder builder = DefaultMatcherBuilder.from(Pattern.compileGenericAutomaton("c"));
		Finder matcher = builder.buildFinder("abc");
		assertThat(matcher.find(), is(true));
		assertThat(matcher.match, notNullValue());
	}

	@Test
	public void testFindNot() throws Exception {
		DefaultMatcherBuilder builder = DefaultMatcherBuilder.from(Pattern.compileGenericAutomaton("c"));
		Finder matcher = builder.buildFinder("ab");
		assertThat(matcher.find(), is(false));
		assertThat(matcher.match, nullValue());
	}

	@Test
	public void testFindDFA() throws Exception {
		DefaultMatcherBuilder builder = DefaultMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxabcdefg");
		List<String> matches = new ArrayList<String>();
		while (matcher.find()) {
			matches.add(matcher.match.text());
		}
		assertThat(matches, hasItem("abcdefg"));
	}

	@Test
	public void testFindPosix() throws Exception {
		DefaultMatcherBuilder builder = (DefaultMatcherBuilder) new DefaultMatcherBuilder(new ToCompactGenericAutomaton()).initWith(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxabcdefg");
		assertThat(matcher.find(), is(true));
		assertThat(matcher.match.text(), equalTo("abcdefg"));
	}

	@Test
	public void testPattern2() throws Exception {
		DefaultMatcherBuilder builder = DefaultMatcherBuilder.from(Pattern.compileGenericAutomaton("(ab|a|bcdef|g)+"));
		Finder matcher = builder.buildFinder("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxabcdefgxxxxxxxxxabgxxxagabxxx");
		assertThat(matcher.findAll(), contains(new Match(35, "abcdefg", ACCEPT), new Match(51, "abg", ACCEPT), new Match(57, "agab", ACCEPT)));
	}
}