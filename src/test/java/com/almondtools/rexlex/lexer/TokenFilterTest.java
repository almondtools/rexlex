package com.almondtools.rexlex.lexer;

import static com.almondtools.rexlex.pattern.DefaultTokenType.IGNORE;
import static com.almondtools.rexlex.tokens.Accept.A;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.almondtools.rexlex.pattern.DefaultTokenType;
import com.almondtools.rexlex.tokens.TestToken;

public class TokenFilterTest {

	@Test
	public void testHasNextEmpty() throws Exception {
		Iterator<TestToken> tokens = Collections.<TestToken>emptyList().iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenFilter.hasNext(), is(false));
	}

	@Test
	public void testHasNextInitial() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("a", A)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenFilter.hasNext(), is(true));
	}
	
	@Test
	public void testHasNextSkipsInvalid() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("  ", IGNORE)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenFilter.hasNext(), is(false));
	}
	
	@Test
	public void testHasNextDoesNotModifyInternalState() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("a", A)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		tokenFilter.hasNext();
		assertThat(tokenFilter.hasNext(), is(true));
	}

	@Test
	public void testNextInitial() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("a", A)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenFilter.next().getLiteral(), equalTo("a"));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testNextOnEmpty() throws Exception {
		Iterator<TestToken> tokens = Collections.<TestToken>emptyList().iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		tokenFilter.next();
	}
	
	@Test
	public void testNextAfterHasNextSkipsInvalid() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("  ", IGNORE), new TestToken("a", A)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenFilter.hasNext(), is(true));
		assertThat(tokenFilter.next().getLiteral(), equalTo("a"));
		assertThat(tokenFilter.hasNext(), is(false));
	}
	
	@Test
	public void testNextSkipsInvalid() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("  ", IGNORE), new TestToken("a", A)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenFilter.next().getLiteral(), equalTo("a"));
		assertThat(tokenFilter.hasNext(), is(false));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveOnUnmodifiableIterator() throws Exception {
		Iterator<TestToken> tokens = Arrays.asList(new TestToken("a", A)).iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		tokenFilter.next();
		tokenFilter.remove();
	}

	@Test
	public void testRemoveOnModifiableIterator() throws Exception {
		List<TestToken> tokenList = new ArrayList<TestToken>();
		tokenList.add(new TestToken("a", A));
		Iterator<TestToken> tokens = tokenList.iterator();
		TestTokenFilter tokenFilter = new TestTokenFilter(tokens);
		assertThat(tokenList, hasSize(1));
		tokenFilter.next();
		tokenFilter.remove();
		assertThat(tokenList, empty());
	}
	
	private class TestTokenFilter extends TokenFilter<TestToken> {

		public TestTokenFilter(Iterator<TestToken> tokens) {
			super(tokens);
		}

		@Override
		public boolean isValid(TestToken token) {
			return token.getType() != DefaultTokenType.IGNORE;
		}

	}

}
