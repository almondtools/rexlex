package com.almondtools.rexlex.pattern;

import java.util.List;

import com.almondtools.rexlex.stringsearch.StringSearchAlgorithm;

public interface MultiWordSearchAlgorithmFactory {

	StringSearchAlgorithm of(List<String> patterns);

}
