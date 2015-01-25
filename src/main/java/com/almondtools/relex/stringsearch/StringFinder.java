package com.almondtools.relex.stringsearch;

import java.util.List;

public interface StringFinder {

	StringMatch findNext();
	List<StringMatch> findAll();
	void skipTo(int pos);
}