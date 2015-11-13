package com.almondtools.rexlex.io;

public interface CharClassProvider {

	int next();
	int lookahead();
	int lookahead(int i);
	int prev();
	int lookbehind();
	int lookbehind(int i);

	long current();
	void move(long i);
	boolean finished();

	char at(long i);
	String slice(long start, long end);

}
