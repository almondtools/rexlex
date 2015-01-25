package com.almondtools.relex.pattern;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.almondtools.relex.pattern.Pattern.CharNode;
import com.almondtools.relex.pattern.Pattern.ProCharNode;

public class ProCharMatcher extends TypeSafeMatcher<ProCharNode> {

	private char[] chars;

	public ProCharMatcher(char[] chars) {
		this.chars = chars;
	}

	@Override
	public void describeTo(Description description) {
	}

	@Override
	protected boolean matchesSafely(ProCharNode item) {
		List<CharNode> nodes = item.toCharNodes();
		if (size(nodes) != chars.length) {
			return false;
		}
		nextChar: for (char c : chars) {
			for (CharNode node : nodes) {
				if (node.contains(c)) {
					continue nextChar;
				}
			}
			return false;
		}
		return true;
	}

	private int size(List<CharNode> nodes) {
		int size = 0;
		for (CharNode node : nodes) {
			size += node.getTo() - node.getFrom() + 1;
		}
		return size;
	}

	public static ProCharMatcher containsChars(char... chars) {
		return new ProCharMatcher(chars);
	}

}
