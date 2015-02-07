package com.almondtools.rexlex.io;

public interface CharClassProvider {

	int next();
	int lookahead();
	int lookahead(int i);
	int prev();
	int lookbehind();
	int lookbehind(int i);

	int current();
	void move(int i);
	boolean finished();

	char at(int i);
	String slice(int start, int end);

}
