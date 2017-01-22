package com.almondtools.rexlex.pattern;

import net.amygdalum.regexparser.RegexParserOption;

public class PatternFlag implements PatternOption {
	
	public static final PatternFlag DOTALL = new PatternFlag(RegexParserOption.DOT_ALL);
	
	private RegexParserOption option;

	public PatternFlag(RegexParserOption option) {
		this.option = option;
	}
	
	public RegexParserOption getOption() {
		return option;
	}
	
}