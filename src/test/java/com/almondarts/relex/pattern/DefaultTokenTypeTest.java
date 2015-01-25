package com.almondarts.relex.pattern;

import static com.almondarts.relex.pattern.DefaultTokenType.ACCEPT;
import static com.almondarts.relex.pattern.DefaultTokenType.ERROR;
import static com.almondarts.relex.pattern.DefaultTokenType.IGNORE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.almondarts.relex.TokenType;


public class DefaultTokenTypeTest {

	private DefaultTokenType.Factory tokenTypes;

	@Before
	public void before() {
		tokenTypes = new DefaultTokenType.Factory();
	}

	@Test
	public void testError() throws Exception {
		assertThat(ERROR.error(), is(true));
		assertThat(IGNORE.error(), is(false));
		assertThat(ACCEPT.error(), is(false));
	}
	
	@Test
	public void testIgnoreLessThanAcceptAndError() throws Exception {
		assertThat(tokenTypes.union(IGNORE, ACCEPT), sameInstance((TokenType) ACCEPT));
		assertThat(tokenTypes.union(IGNORE, ERROR), sameInstance((TokenType) ERROR));
	}
	
	@Test
	public void testAcceptLessThanError() throws Exception {
		assertThat(tokenTypes.union(ACCEPT, ERROR), sameInstance((TokenType) ERROR));
	}
	
	@Test
	public void testUnionOfAllTypesReturnsMostDominantType() throws Exception {
		assertThat(tokenTypes.union(Arrays.asList(IGNORE, ACCEPT, ERROR)), sameInstance((TokenType) ERROR));
	}
	
	@Test
	public void testUnionRightIgnoresOtherTypes() throws Exception {
		assertThat(tokenTypes.union(IGNORE, tokenType()), sameInstance((TokenType) IGNORE));
	}
	
	@Test
	public void testUnionLeftIgnoresOtherTypes() throws Exception {
		assertThat(tokenTypes.union(tokenType(), IGNORE), sameInstance((TokenType) IGNORE));
	}
	
	@Test
	public void testUnionOtherTypesReturnsNull() throws Exception {
		assertThat(tokenTypes.union(tokenType(), tokenType()), nullValue());
	}
	
	private TokenType tokenType() {
		return new TokenType() {
			
			@Override
			public boolean error() {
				return false;
			}
			
			@Override
			public boolean accept() {
				return true;
			}
		};
	}

}
