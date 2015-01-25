package com.almondarts.relex.pattern;

import static com.almondarts.relex.pattern.PatternOptionUtil.list;

import org.junit.rules.TestRule;

public class PatternRule extends PatternCompilationModeRule implements TestRule {
	
	public Pattern compile(String string, PatternOption... options) {
		return Pattern.compile(string, list(mode.getMatcherBuilder(), options));
	}

}
