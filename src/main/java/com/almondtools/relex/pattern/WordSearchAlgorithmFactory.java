package com.almondtools.relex.pattern;

import com.almondtools.relex.stringsearch.StringSearchAlgorithm;

public interface WordSearchAlgorithmFactory {

	StringSearchAlgorithm of(String pattern);

}
