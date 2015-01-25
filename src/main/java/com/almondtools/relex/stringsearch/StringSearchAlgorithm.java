package com.almondtools.relex.stringsearch;

import com.almondtools.relex.io.CharProvider;

public interface StringSearchAlgorithm {

	StringFinder createFinder(CharProvider chars);

	int getPatternLength();

}
