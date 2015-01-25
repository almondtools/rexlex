package com.almondtools.relex.automaton;

import java.util.List;

public final class UnicodeCharClassMapper implements CharClassMapper {
	
	private char[] chars;
	private char lowerBound;
	private int min;
	private char upperBound;
	private int max;
	private int[] charToClass;

	public UnicodeCharClassMapper(char[] chars) {
		this.chars = chars;
		this.lowerBound = chars.length > 1 ? chars[1] : 0;
		this.upperBound = chars.length > 1 ? (char) (chars[chars.length - 1] - 1) : 0;
		this.min = 0;
		this.max = chars.length <= 1 ? 0 : chars.length - 1;  
		this.charToClass = computeCharClasses(chars);
	}


	private static int[] computeCharClasses(char[] relevantChars) {
		char low = relevantChars.length <= 1 ? 0 : relevantChars[1];
		char high = relevantChars.length <= 1 ? 0 : relevantChars[relevantChars.length - 1];
		int range = high - low;
		int[] charToClass = new int[range];
		int charClass = 1;
		for (int ch = 0; ch < range; ch++) {
			if (charClass + 1 < relevantChars.length && (ch + low) == relevantChars[charClass + 1]) {
				charClass++;
			}
			charToClass[ch] = charClass;
		}
		return charToClass;
	}


	@Override
	public int getIndex(char ch) {
		if (ch < lowerBound) {
			return min;
		} else if (ch > upperBound) {
			return max;
		}
		return charToClass[ch - lowerBound];
	}


	@Override
	public char[] getRelevantChars() {
		return chars;
	}


	@Override
	public int indexCount() {
		return chars.length;
	}


	@Override
	public char representative(int i) {
		return chars[i];
	}
	
	@Override
	public char[] mapped(int i) {
		char start = i >= 0 ? chars[i] : Character.MIN_VALUE;
		char next = i < chars.length - 1 ? chars[i+1] : (char) (Character.MAX_VALUE + 1);
		char stop = (char) (next - 1);
		char[] mapped = new char[next-start];
		for (char c = start; c <= stop; c++) {
			mapped[c-start] = c;
		}
		return mapped;
	}
	
	@Override
	public char lowerBound(int i) {
		return i >= 0 ? chars[i] : Character.MIN_VALUE;
	}
	
	@Override
	public char upperBound(int i) {
		if (i < chars.length - 1) {
			return (char) (chars[i+1] - 1);
		} else {
			return Character.MAX_VALUE;
		}
	}
	
	@Override
	public String representatives(List<Integer> indexes) {
		if (indexes == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		for (Integer i : indexes) {
			buffer.append(chars[i]);
		}
		return buffer.toString();
	}

}
