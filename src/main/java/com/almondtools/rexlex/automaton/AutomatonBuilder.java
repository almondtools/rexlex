package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.pattern.Pattern.PatternNode;
import com.almondtools.rexlex.pattern.PatternOption;

public interface AutomatonBuilder extends PatternOption {

	GenericAutomaton buildFrom(PatternNode node);

	GenericAutomaton buildFrom(PatternNode node, TokenType type);

}
