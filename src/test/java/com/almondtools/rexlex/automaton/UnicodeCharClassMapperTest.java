package com.almondtools.rexlex.automaton;

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.almondtools.rexlex.automaton.UnicodeCharClassMapper;


public class UnicodeCharClassMapperTest {

	@Test
	public void testGetIndex() throws Exception {
		UnicodeCharClassMapper mapper = new UnicodeCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('b'), equalTo(0));
		assertThat(mapper.getIndex('c'), equalTo(1));
		assertThat(mapper.getIndex('g'), equalTo(1));
		assertThat(mapper.getIndex('h'), equalTo(2));
		assertThat(mapper.getIndex('n'), equalTo(2));
		assertThat(mapper.getIndex('o'), equalTo(3));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(3));
	}

	@Test
	public void testGetRelevantChars() throws Exception {
		UnicodeCharClassMapper mapper = new UnicodeCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(charList(mapper.getRelevantChars()), contains(MIN_VALUE, 'c','h','o'));
	}

	private List<Character> charList(char[] chars) {
		List<Character> list = new ArrayList<Character>();
		for (char c : chars) {
			list.add(c);
		}
		return list;
	}

	@Test
	public void testIndexCount() throws Exception {
		UnicodeCharClassMapper mapper = new UnicodeCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(mapper.indexCount(), equalTo(4));
	}

	@Test
	public void testRepresentative() throws Exception {
		UnicodeCharClassMapper mapper = new UnicodeCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
		assertThat(mapper.representative(1), equalTo('c'));
		assertThat(mapper.representative(2), equalTo('h'));
		assertThat(mapper.representative(3), equalTo('o'));
	}

}
