package com.almondarts.relex.pattern;

import com.almondarts.relex.stringsearch.StringSearchAlgorithm;

public interface WordSearchAlgorithmFactory {

	StringSearchAlgorithm of(String pattern);

}
