package com.almondarts.relex.pattern;

import static com.almondarts.relex.automaton.GenericAutomatonBuilder.match;
import static com.almondarts.relex.automaton.GenericAutomatonBuilder.matchOptional;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.almondarts.relex.io.StringCharProvider;
import com.almondarts.relex.tokens.TestToken;
import com.almondarts.relex.tokens.TestTokenFactory;


public class TokenIteratorTest {

	@Test
	public void testHasNextOnEmptyChars() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(false));
	}

	@Test
	public void testHasNextRepeatedly() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("a", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(true));
		assertThat(tokenIterator.hasNext(), is(true));
	}
	
	@Test
	public void testHasNextWithFirstMatching() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("aa", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(true));
	}
	
	@Test
	public void testHasNextWithSecondMatching() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("ba", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(true));
	}
	
	@Test
	public void testHasNextWithNoneMatching() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("bb", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(true));
	}
	
	@Test
	public void testHasNextWithEmptyMatch() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(matchOptional(match('a')), new StringCharProvider("b", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(true));
	}
	
	@Test
	public void testHasNextWithEmptyMatchOnEmptyChars() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(matchOptional(match('a')), new StringCharProvider("", 0), new TestTokenFactory());
		assertThat(tokenIterator.hasNext(), is(false));
	}
	
	@Test
	public void testHasNextWithAllConsumed() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("a", 0), new TestTokenFactory());
		tokenIterator.next();
		assertThat(tokenIterator.hasNext(), is(false));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testNextOnEmptyChars() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("", 0), new TestTokenFactory());
		tokenIterator.next();
	}

	@Test
	public void testNextAndHasNext() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("a", 0), new TestTokenFactory());
		assertThat(tokenIterator.next().getLiteral(), equalTo("a"));
		assertThat(tokenIterator.hasNext(), is(false));
	}
	
	@Test
	public void testDoubleNext() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("aa", 0), new TestTokenFactory());
		assertThat(tokenIterator.next().getLiteral(), equalTo("a"));
		assertThat(tokenIterator.next().getLiteral(), equalTo("a"));
		assertThat(tokenIterator.hasNext(), is(false));
	}
	
	@Test
	public void testNextWithSomeNotMatching() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(match('a'), new StringCharProvider("ba", 0), new TestTokenFactory());
		TestToken b = tokenIterator.next();
		assertThat(b.getLiteral(), equalTo("b"));
		assertThat(b.getType().error(), is(true));
		TestToken a = tokenIterator.next();
		assertThat(a.getLiteral(), equalTo("a"));
	}
	
	@Test
	public void testNextWithNoneMatchingButCharsNotEmpty() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(matchOptional(match('a')), new StringCharProvider("b", 0), new TestTokenFactory());
		TestToken b = tokenIterator.next();
		assertThat(b.getLiteral(), equalTo("b"));
		assertThat(b.getType().error(), is(true));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testRemove() throws Exception {
		TokenIterator<TestToken> tokenIterator = new TokenIterator<TestToken>(matchOptional(match('a')), new StringCharProvider("a", 0), new TestTokenFactory());
		tokenIterator.next();
		tokenIterator.remove();
	}
	
}
