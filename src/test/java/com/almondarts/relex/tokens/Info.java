package com.almondarts.relex.tokens;

import com.almondarts.relex.TokenType;

public enum Info implements TokenType {
	INFO;

	@Override
	public boolean error() {
		return false;
	}
	
	@Override
	public boolean accept() {
		return false;
	}
}