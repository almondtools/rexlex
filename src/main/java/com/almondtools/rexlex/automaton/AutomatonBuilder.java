package com.almondtools.rexlex.automaton;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.pattern.PatternOption;

import net.amygdalum.regexparser.RegexNode;

public interface AutomatonBuilder extends PatternOption {

	GenericAutomaton buildFrom(RegexNode node);

	GenericAutomaton buildFrom(RegexNode node, TokenType type);

}
