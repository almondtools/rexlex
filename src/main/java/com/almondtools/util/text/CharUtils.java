package com.almondtools.util.text;

public class CharUtils {

    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

	public static String charToString(char ch) {
		if (isAsciiPrintable(ch)) {
			return String.valueOf(ch);
		} else {
			StringBuilder buffer = new StringBuilder("\\u");
			String hex = Integer.toHexString((int) ch);
			for (int i = 0; i < 4 - hex.length(); i++) {
				buffer.append('0');
			}
			buffer.append(hex);
			return buffer.toString();
		}
	}

}
