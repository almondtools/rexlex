package com.almondtools.rexlex.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class DotGraphMatcher extends TypeSafeDiagnosingMatcher<String> {

	private String prefix;
	private Map<String, Integer> nodes;
	private Map<String, Integer> arcs;

	public DotGraphMatcher() {
		this.nodes = new HashMap<String, Integer>();
		this.arcs = new HashMap<String, Integer>();
	}

	public static DotGraphMatcher startsWith(String prefix) {
		return new DotGraphMatcher().startingWith(prefix);
	}

	private DotGraphMatcher startingWith(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public DotGraphMatcher withNodes(int number, String label) {
		nodes.put(label, number);
		return this;
	}

	public DotGraphMatcher withArcs(int number, String label) {
		arcs.put(label, number);
		return this;
	}

	@Override
	protected boolean matchesSafely(String item, Description mismatchDescription) {
		if (prefix != null && !item.startsWith(prefix)) {
			return false;
		}
		for (Map.Entry<String, Integer> entry : nodes.entrySet()) {
			String shape = entry.getKey();
			int number = entry.getValue();
			int found = find(item, nodePattern(shape));
			if (found != number) {
				mismatchDescription.appendText(found + " times node with shape ").appendValue(shape);
				return false;
			}
		}
		for (Map.Entry<String, Integer> entry : arcs.entrySet()) {
			String label = entry.getKey();
			int number = entry.getValue();
			int found = find(item, arcPattern(label));
			if (found != number) {
				mismatchDescription.appendText(found + " times arc with label ").appendValue(label);
				return false;
			}
		}
		return true;
	}

	private String nodePattern(String shape) {
		return "\\d+ \\[shape=" + Pattern.quote(shape) + ".*?\\];";
	}

	private String arcPattern(String label) {
		if (label.isEmpty()) {
			return "\\d+ -> \\d+;";
		} else {
			return "\\d+ -> \\d+ \\[label=\"" + Pattern.quote(label) + "\".*?\\];";
		}
	}

	private int find(String item, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(item);
		int foundNumber = 0;
		while (m.find()) {
			foundNumber++;
		}
		return foundNumber;
	}

	@Override
	public void describeTo(Description description) {
		if (prefix != null) {
			description.appendText("starting with ").appendValue(prefix);
		}
		for (Map.Entry<String, Integer> entry : nodes.entrySet()) {
			description.appendText("containing " + entry.getValue() + " lines of ").appendValue(nodePattern(entry.getKey()));
		}
		for (Map.Entry<String, Integer> entry : arcs.entrySet()) {
			description.appendText("containing " + entry.getValue() + " lines of ").appendValue(arcPattern(entry.getKey()));
		}
	}

}
