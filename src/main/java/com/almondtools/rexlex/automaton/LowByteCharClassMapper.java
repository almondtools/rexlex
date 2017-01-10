package com.almondtools.rexlex.automaton;

import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;

import java.util.List;

public final class LowByteCharClassMapper implements CharClassMapper {

	private char[] chars;
	private int[] lowbyte;

	public LowByteCharClassMapper(char[] chars) {
		this.chars = chars;
		this.lowbyte = computeLowByte(chars);
	}

	static int[] computeLowByte(char[] chars) {
		int[] lowbytes = new int[256];
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			int low = c & 0xff;
			lowbytes[low] = i;
		}

		int lastValue = 0;
		for (int j = 0; j < lowbytes.length; j++) {
			if (lowbytes[j] > lastValue) {
				lastValue = lowbytes[j];
			} else {
				lowbytes[j] = lastValue;
			}
		}

		return lowbytes;
	}

	public int getIndex(char ch) {
		int l = ch & 0xff;
		return lowbyte[l];
	}

	public char[] getRelevantChars() {
		return chars;
	}

	public int indexCount() {
		return chars.length;
	}

	public char representative(int i) {
		return chars[i];
	}

	public char[] mapped(int i) {
		char start = i >= 0 ? chars[i] : Character.MIN_VALUE;
		char next = i < chars.length - 1 ? chars[i + 1] : after(Character.MAX_VALUE);
		char stop = before(next);
		char[] mapped = new char[next - start];
		for (char c = start; c <= stop; c++) {
			mapped[c - start] = c;
		}
		return mapped;
	}

	public char lowerBound(int i) {
		return i >= 0 ? chars[i] : Character.MIN_VALUE;
	}

	public char upperBound(int i) {
		if (i < chars.length - 1) {
			return before(chars[i + 1]);
		} else {
			return Character.MAX_VALUE;
		}
	}

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

	public LowByteCharClassMapper of(char[] relevantChars) {
		LowByteCharClassMapper mapper = new LowByteCharClassMapper(relevantChars);
		if (mapper.lowbyte.length == 1) {
			return new LowByteCharClassMapper(relevantChars);
		} else {
			return mapper;
		}
	}
}
