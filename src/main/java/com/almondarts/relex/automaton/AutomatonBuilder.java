package com.almondarts.relex.automaton;

import com.almondarts.relex.TokenType;
import com.almondarts.relex.pattern.Pattern.PatternNode;
import com.almondarts.relex.pattern.PatternOption;

public interface AutomatonBuilder extends PatternOption {

	GenericAutomaton buildFrom(PatternNode node);

	GenericAutomaton buildFrom(PatternNode node, TokenType type);

}
