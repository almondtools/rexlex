package com.almondarts.relex.lexer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.almondarts.relex.Lexer;
import com.almondarts.relex.LexerBuilder;
import com.almondarts.relex.tokens.Accept;
import com.almondarts.relex.tokens.TestToken;
import com.almondarts.relex.tokens.TestTokenFactory;

public class DynamicLexerBuilderTest {

	private LexerBuilder<TestToken> lexBuilder;
	
	@Before
	public void before() {
		this.lexBuilder = new DynamicLexerBuilder<TestToken>(new TestTokenFactory());
	}
	
	@Test
	public void testAddSingleCharToken() throws Exception {
		lexBuilder.matchPattern("a", Accept.A);
		Lexer<TestToken> lex = lexBuilder.build();
		assertThat(lex.lex("a").next(), equalTo(new TestToken("a", Accept.A)));
		assertFalse(lex.lex("b").hasNext());
	}
	
	@Test
	public void testAddSingleCharTokenAndElse() throws Exception {
		lexBuilder.matchPattern("a", Accept.A);
		lexBuilder.matchRemainder(Accept.REMAINDER);
		Lexer<TestToken> lex = lexBuilder.build();
		assertThat(lex.lex("a").next(), equalTo(new TestToken("a", Accept.A)));
		assertThat(lex.lex("b").next(), equalTo(new TestToken("b", Accept.REMAINDER)));
	}
}
