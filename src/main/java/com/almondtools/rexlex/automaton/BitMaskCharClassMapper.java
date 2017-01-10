package com.almondtools.rexlex.automaton;

import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;

import java.util.List;

import net.amygdalum.util.map.CharIntMap;

public final class BitMaskCharClassMapper implements CharClassMapper {
	
	private char[] chars;
	private int[] highbyte;
	private int[][] lowbyte;

	public BitMaskCharClassMapper(char[] chars) {
		this.chars = chars;
		this.highbyte = computeHighByte(chars);
		this.lowbyte = computeLowByte(chars);
	}

	static int[][] computeLowByte(char[] chars) {
		CharIntMap map = new CharIntMap(-1);
		
		int index = 0;
		int value = 0;
		for (char c : chars) {
			int high = (c >> 8) & 0xff;
			if (index > high) {
				map.add(c, value - 1);
				continue;
			}
			map.add(c, value);
			while (index <= high) {
				index++;
			}
			value++;
		}
				
		int[][] lowbytes = new int[value + 1][256];
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			int row = map.get(c);
			int low = c & 0xff;
			lowbytes[row][low] = i;
		}
		
		int lastValue = 0;
		for (int i = 0; i < lowbytes.length; i++) {
			int[] row = lowbytes[i];
			for (int j = 0; j < row.length; j++) {
				if (row[j] > lastValue) {
					lastValue = row[j];
				} else {
					row[j] = lastValue;
				}
			}
		}
		
		return lowbytes;
	}


	static int[] computeHighByte(char[] chars) {
		int[] highbytes = new int[256];
		int index = 0;
		int value = 0;
		for (char c : chars) {
			int high = (c >> 8) & 0xff;
			if (index > high) {
				continue;
			}
			while (index <= high) {
				highbytes[index] = value;
				index++;
			}
			value++;
		}
		while (index < 256) {
			highbytes[index] = value;
			index++;
		}
		return highbytes;
	}


	public int getIndex(char ch) {
		int h = (ch >> 8) & 0xff; 
		int l = ch & 0xff; 
		int i = highbyte[h];
		int ii = lowbyte[i][l];
		return ii;
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
		char next = i < chars.length - 1 ? chars[i+1] : after(Character.MAX_VALUE);
		char stop = before(next);
		char[] mapped = new char[next-start];
		for (char c = start; c <= stop; c++) {
			mapped[c-start] = c;
		}
		return mapped;
	}
	
	public char lowerBound(int i) {
		return i >= 0 ? chars[i] : Character.MIN_VALUE;
	}
	
	public char upperBound(int i) {
		if (i < chars.length - 1) {
			return before(chars[i+1]);
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

}
