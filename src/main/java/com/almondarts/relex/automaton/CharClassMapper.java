package com.almondarts.relex.automaton;

import java.util.List;

public interface CharClassMapper {

	char[] getRelevantChars();

	int getIndex(char ch);

	int indexCount();

	char representative(int i);

	String representatives(List<Integer> path);

	char[] mapped(int decision);

	char lowerBound(int charClass);

	char upperBound(int charClass);

}
