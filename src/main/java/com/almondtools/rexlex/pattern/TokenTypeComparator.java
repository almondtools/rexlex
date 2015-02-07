package com.almondtools.rexlex.pattern;

import java.util.Comparator;

import com.almondtools.rexlex.TokenType;

public class TokenTypeComparator implements Comparator<TokenType> {

	@Override
	public int compare(TokenType o1, TokenType o2) {
		return priority(o1) - priority(o2);
	}

	private int priority(TokenType o) {
		if (o.error()) {
			return 4;
		} else if (o.accept()) {
			return 2;
		} else {
			return 1;
		}
	}

}
