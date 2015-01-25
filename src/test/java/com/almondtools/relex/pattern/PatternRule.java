package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.PatternOptionUtil.list;

import org.junit.rules.TestRule;

import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.pattern.PatternOption;

public class PatternRule extends PatternCompilationModeRule implements TestRule {
	
	public Pattern compile(String string, PatternOption... options) {
		return Pattern.compile(string, list(mode.getMatcherBuilder(), options));
	}

}
