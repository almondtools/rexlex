package com.almondarts.relex.lexer;

import static com.almondarts.relex.tokens.Accept.A;
import static com.almondarts.relex.tokens.Accept.B;
import static com.almondarts.relex.tokens.Accept.REMAINDER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.almondarts.collections.HashMaps;
import com.almondarts.relex.TokenType;
import com.almondarts.relex.tokens.TestToken;
import com.almondarts.relex.tokens.TestTokenFactory;

public class DynamicLexerTest {

	private TestTokenFactory factory;

	@Before
	public void before() {
		this.factory = new TestTokenFactory();
	}

	@Test
	public void testSimplePatternOnChar() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("a", A)
			.put("b", B)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, REMAINDER, factory);
		assertThat(lexer.lex("a").next(), equalTo(new TestToken("a", A)));
		assertThat(lexer.lex("b").next(), equalTo(new TestToken("b", B)));
		assertThat(lexer.lex("c").next(), equalTo(new TestToken("c", REMAINDER)));
	}

	@Test
	public void testSimplePatternOnString() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("a", A)
			.put("b", B)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, REMAINDER, factory);
		Iterator<TestToken> tokens = lexer.lex("abcba");
		assertThat(tokens.next(), equalTo(new TestToken("a", A)));
		assertThat(tokens.next(), equalTo(new TestToken("b", B)));
		assertThat(tokens.next(), equalTo(new TestToken("c", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", B)));
		assertThat(tokens.next(), equalTo(new TestToken("a", A)));
	}

	@Test
	public void testComplexPatternOnString() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("ab*c", REMAINDER)
			.put("b", REMAINDER)
			.put("a", REMAINDER)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, factory);
		Iterator<TestToken> tokens = lexer.lex("abcba");
		assertThat(tokens.next(), equalTo(new TestToken("abc", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("a", REMAINDER)));
	}

	@Test
	public void testComplexPatternOnStringPriorityOnFirstAndBacktracking() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("ab*c", REMAINDER)
			.put("b", REMAINDER)
			.put("a", REMAINDER)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, factory);
		Iterator<TestToken> tokens = lexer.lex("abcbab");
		assertThat(tokens.next(), equalTo(new TestToken("abc", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("a", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", REMAINDER)));

		tokens = lexer.lex("abcbabc");
		assertThat(tokens.next(), equalTo(new TestToken("abc", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("abc", REMAINDER)));
	}

	@Test
	public void testComplexPatternOnStringIgnoreParts() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("ab*c", REMAINDER)
			.put("b", null)
			.put("a", REMAINDER)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, factory);
		Iterator<TestToken> tokens = lexer.lex("abcbab");
		assertThat(tokens.next(), equalTo(new TestToken("abc", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("a", REMAINDER)));
		assertFalse(tokens.hasNext());
	}

	@Test
	public void testCharacterClasses() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("a", REMAINDER)
			.put("\\s+", null)
			.put("b", REMAINDER)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, factory);
		Iterator<TestToken> tokens = lexer.lex("a b  ab");
		assertThat(tokens.next(), equalTo(new TestToken("a", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("a", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("b", REMAINDER)));
	}

	@Test
	public void testBBCodeTags() throws Exception {
		Map<String, TokenType> patternToTypes = HashMaps.<String, TokenType>linked()
			.put("\\[strong\\]", REMAINDER)
			.put("\\[/strong\\]", REMAINDER)
			.put("\\[quote \\w+='[^']*'\\]", REMAINDER)
			.put("\\[/quote\\]", REMAINDER)
			.put("\\s+", REMAINDER)
			.build();
		DynamicLexer<TestToken> lexer = new DynamicLexer<TestToken>(patternToTypes, REMAINDER, factory);
		Iterator<TestToken> tokens = lexer.lex("[strong]strong[/strong]  [quote author='xyz']quote[/quote]");
		assertThat(tokens.next(), equalTo(new TestToken("[strong]", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("strong", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("[/strong]", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("  ", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("[quote author='xyz']", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("quote", REMAINDER)));
		assertThat(tokens.next(), equalTo(new TestToken("[/quote]", REMAINDER)));
	}

}
