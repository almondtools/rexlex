package com.almondtools.relex.automaton;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.pattern.PatternOption;
import com.almondtools.relex.pattern.Pattern.PatternNode;

public interface AutomatonBuilder extends PatternOption {

	GenericAutomaton buildFrom(PatternNode node);

	GenericAutomaton buildFrom(PatternNode node, TokenType type);

}
