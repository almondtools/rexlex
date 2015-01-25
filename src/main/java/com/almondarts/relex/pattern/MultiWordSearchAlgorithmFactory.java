package com.almondarts.relex.pattern;

import java.util.List;

import com.almondarts.relex.stringsearch.StringSearchAlgorithm;

public interface MultiWordSearchAlgorithmFactory {

	StringSearchAlgorithm of(List<String> patterns);

}
