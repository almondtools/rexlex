package com.almondarts.relex.tokens;

import com.almondarts.relex.Token;
import com.almondarts.relex.TokenType;

public class TestToken implements Token {

	private String literal;
	private TokenType type;

	public TestToken(String literal, TokenType type) {
		this.literal = literal;
		this.type = type;
	}
	
	@Override
	public String getLiteral() {
		return literal;
	}
	
	@Override
	public TokenType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return literal.hashCode() * 31 + type.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestToken other = (TestToken) obj;
		return this.type == other.type && this.literal.equals(other.literal);
	}
	
	@Override
	public String toString() {
		return literal + '(' + type.toString() + ')';
	}
	
}