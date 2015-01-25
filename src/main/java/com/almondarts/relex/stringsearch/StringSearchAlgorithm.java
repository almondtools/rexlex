package com.almondarts.relex.stringsearch;

import com.almondarts.relex.io.CharProvider;

public interface StringSearchAlgorithm {

	StringFinder createFinder(CharProvider chars);

	int getPatternLength();

}
