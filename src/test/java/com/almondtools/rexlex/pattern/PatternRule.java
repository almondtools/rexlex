package com.almondtools.rexlex.pattern;

import static com.almondtools.rexlex.pattern.PatternOptionUtil.list;

import org.junit.rules.TestRule;

import com.almondtools.rexlex.pattern.Pattern;
import com.almondtools.rexlex.pattern.PatternOption;

public class PatternRule extends PatternCompilationModeRule implements TestRule {
	
	public Pattern compile(String string, PatternOption... options) {
		return Pattern.compile(string, list(mode.getMatcherBuilder(), options));
	}

}
