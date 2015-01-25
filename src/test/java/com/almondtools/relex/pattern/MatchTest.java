package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.DefaultTokenType.ACCEPT;
import static com.almondtools.relex.pattern.DefaultTokenType.ERROR;
import static com.almondtools.relex.pattern.DefaultTokenType.IGNORE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.pattern.Match;


public class MatchTest {

	@Test
	public void testDefaultMatch() throws Exception {
		Match match = new Match(0, "test");
		assertThat(match.start(), equalTo(0));
		assertThat(match.text(), equalTo("test"));
		assertThat(match.getType(), equalTo((TokenType) IGNORE));
	}

	@Test
	public void testExplicitMatch() throws Exception {
		Match match = new Match(0, "test", ERROR);
		assertThat(match.start(), equalTo(0));
		assertThat(match.text(), equalTo("test"));
		assertThat(match.getType(), equalTo((TokenType) ERROR));
	}

	@Test
	public void testToString() throws Exception {
		Match match = new Match(3, "test", ACCEPT);
		assertThat(match.toString(), equalTo("3:test"));
	}
	
}
