package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.TokenType;

public class Match {

	private int start;
	private String text;
	private TokenType type;

	public Match(int start, String text) {
		this.start = start;
		this.text = text;
		this.type = DefaultTokenType.IGNORE;
	}

	public Match(int start, String text, TokenType type) {
		this.start = start;
		this.text = text;
		this.type = type;
	}

	public TokenType getType() {
		return type;
	}

	public int start() {
		return start;
	}

	public int end() {
		return start + text.length();
	}

	public String text() {
		return text;
	}

	@Override
	public String toString() {
		return String.valueOf(start) + ":" + text;
	}

	@Override
	public int hashCode() {
		return 31 +  start * 7 + text.hashCode() * 3 + (type != null ? type.hashCode() : 0);
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
			&& equals(this.type, that.type);
	}

	private boolean equals(TokenType t1, TokenType t2) {
		if (t1 == t2) {
			return true;
		}
		if (t1 == null) {
			return false;
		} else if (t2 == null) {
			return false;
		} else {
			return t1.equals(t2);
		}
	}
	
}
