package com.almondtools.rexlex.automaton;

import java.util.HashSet;
import java.util.Set;

public final class CharClassMappers {

	private CharClassMappers() {
	}

	public static CharClassMapper bestFor(char[] chars) {
		CharArrayProperties properties = analyze(chars);
		if (properties.smallRange) {
			return new SmallRangeCharClassMapper(chars);
		} else if (properties.lowByte) {
			return new LowByteCharClassMapper(chars);
		} else {
			return new BitMaskCharClassMapper(chars);
		}
	}

	private static CharArrayProperties analyze(char[] chars) {
		return new CharArrayProperties()
			.computeLowByte(chars)
			.computeSmallRange(chars);

	}

	private static class CharArrayProperties {

		public boolean lowByte;
		public boolean smallRange;

		public CharArrayProperties computeLowByte(char[] chars) {
			Set<Integer> highbytes = new HashSet<>();
			for (char c : chars) {
				int highbyte = c & 0xff00;
				highbytes.add(highbyte);
			}
			this.lowByte = highbytes.size() <= 1;
			return this;
		}

		public CharArrayProperties computeSmallRange(char[] chars) {
			if (chars.length == 0) {
				this.smallRange = true;
			} else {
				char min = chars[0];
				char max = chars[chars.length - 1];
				if (lowByte) {
					this.smallRange = max - min <= 64;
				} else {
					this.smallRange = max - min <= 256;
				}
			}
			return this;
		}

	}
}
