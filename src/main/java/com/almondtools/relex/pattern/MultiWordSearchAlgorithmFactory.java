package com.almondtools.relex.pattern;

import java.util.List;

import com.almondtools.relex.stringsearch.StringSearchAlgorithm;

public interface MultiWordSearchAlgorithmFactory {

	StringSearchAlgorithm of(List<String> patterns);

}
