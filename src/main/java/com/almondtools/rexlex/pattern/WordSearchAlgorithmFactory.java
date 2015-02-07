package com.almondtools.rexlex.pattern;

import com.almondtools.rexlex.stringsearch.StringSearchAlgorithm;

public interface WordSearchAlgorithmFactory {

	StringSearchAlgorithm of(String pattern);

}
