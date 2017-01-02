package com.almondtools.rexlex.pattern;

import java.util.Objects;

import com.almondtools.rexlex.TokenType;

public class Match {

	public long start;
	public long end;
	public String text;
	public TokenType type;

	public Match() {
		this(-1, -1, null, DefaultTokenType.IGNORE);
	}

	private Match(long start, long end, String text, TokenType accepted) {
		this.start = start;
		this.end = end;
		this.text = text;
		this.type = accepted;
	}

	public static Match create(long start, String text, TokenType type) {
		return new Match(start, start + text.length(), text, type);
	}

	public static Match create(long start, long end, String text, TokenType type) {
		return new Match(start, end, text, type);
	}

	public Match consume() {
		Match match = create(start, end, text, type);
		this.start = -1;
		this.end = -1;
		this.text = null;
		this.type = DefaultTokenType.IGNORE;
		return match;
	}

	public Match copy() {
		return create(start, end, text, type);
	}

	public void reset() {
		this.start = -1;
		this.end = -1;
		this.text = null;
		this.type = DefaultTokenType.IGNORE;
	}

	public void init(long start, long end, String text, TokenType type) {
		this.start = start;
		this.end = end;
		this.text = text;
		this.type = type;
	}
	
	public void init(long start, String text, TokenType type) {
		this.start = start;
		this.end = start + text.length();
		this.text = text;
		this.type = type;
	}
	
	public void init(long start, String text) {
		this.start = start;
		this.end = start + text.length();
		this.text = text;
		this.type = DefaultTokenType.IGNORE;
	}
	
	public void moveFrom(Match match) {
		this.start = match.start;
		this.end = match.end;
		this.text = match.text;
		this.type = match.type;
		match.reset();
	}

	public boolean isMatch() {
		return text != null;
	}

	@Override
	public String toString() {
		return String.valueOf(start) + ":" + text;
	}

	@Override
	public int hashCode() {
		return 31 +  (int) start * 7 + text.hashCode() * 3 + (type != null ? type.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Match that = (Match) obj;
		return this.start == that.start
			&& this.text.equals(that.text)
			&& Objects.equals(this.type, that.type);
	}

}
