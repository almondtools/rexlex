package com.almondarts.relex.automaton;

import static com.almondarts.relex.pattern.DefaultTokenType.ACCEPT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.almondarts.relex.TokenType;
import com.almondarts.relex.automaton.GenericAutomaton.EpsilonTransition;
import com.almondarts.relex.automaton.GenericAutomaton.ExactTransition;
import com.almondarts.relex.automaton.GenericAutomaton.RangeTransition;
import com.almondarts.relex.automaton.GenericAutomaton.State;
import com.almondarts.relex.automaton.ThompsonAutomatonBuilder.ThompsonAutomaton;
import com.almondarts.relex.pattern.Pattern;
import com.almondarts.relex.pattern.Pattern.AlternativesNode;
import com.almondarts.relex.pattern.Pattern.ComplementNode;
import com.almondarts.relex.pattern.Pattern.ConcatNode;
import com.almondarts.relex.pattern.Pattern.ConjunctiveNode;
import com.almondarts.relex.pattern.Pattern.EmptyNode;
import com.almondarts.relex.pattern.Pattern.GroupNode;
import com.almondarts.relex.pattern.Pattern.LoopNode;
import com.almondarts.relex.pattern.Pattern.OptionalNode;
import com.almondarts.relex.pattern.Pattern.PatternNode;
import com.almondarts.relex.pattern.Pattern.ProCharNode;
import com.almondarts.relex.pattern.Pattern.RangeCharNode;
import com.almondarts.relex.pattern.Pattern.SingleCharNode;
import com.almondarts.relex.pattern.Pattern.StringNode;

public class ThompsonAutomatonBuilder implements Pattern.PatternNodeVisitor<ThompsonAutomaton>, AutomatonBuilder {

	public ThompsonAutomatonBuilder() {
	}

	public static ThompsonAutomaton match(char value) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		s.addTransition(new ExactTransition(value, e));
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton match(String value) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		char[] chars = value.toCharArray();
		State c = s;
		for (int i = 0; i < chars.length - 1; i++) {
			State n = new State();
			c.addTransition(new ExactTransition(chars[i], n));
			c = n;
		}
		c.addTransition(new ExactTransition(chars[chars.length - 1], e));
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton match(char from, char to) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		if (from > to) {
			char temp = from;
			from = to;
			to = temp;
		}
		s.addTransition(new RangeTransition(from, to, e));
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchAnyChar() {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		s.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, e));
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchAnyOf(char... chars) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		for (char c : chars) {
			s.addTransition(new ExactTransition(c, e));
		}
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchEmpty() {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		return new ThompsonAutomaton(automaton, s, s);
	}

	public static GenericAutomaton matchNothing() {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		return automaton;
	}

	public static ThompsonAutomaton matchOptional(ThompsonAutomaton a) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		s.addTransition(new EpsilonTransition(e));
		s.addTransition(new EpsilonTransition(a.start));
		a.end.addTransition(new EpsilonTransition(e));
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchUnlimitedLoop(ThompsonAutomaton a, int start) {
		if (start == 0) {
			return matchStarLoop(a);
		} else {
			ThompsonAutomaton[] as = copyOf(a, new ThompsonAutomaton[start + 1], start);
			as[start] = matchStarLoop(a.clone());
			return matchConcatenation(as);
		}
	}

	public static ThompsonAutomaton matchStarLoop(ThompsonAutomaton a) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		s.addTransition(new EpsilonTransition(a.start));
		s.addTransition(new EpsilonTransition(e));
		a.end.addTransition(new EpsilonTransition(a.start));
		a.end.addTransition(new EpsilonTransition(e));
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchRangeLoop(ThompsonAutomaton a, int start, int end) {
		if (start == end) {
			return matchFixedLoop(a, start);
		} else {
			ThompsonAutomaton aFixed = matchFixedLoop(a, start);
			ThompsonAutomaton aUpToN = matchUpToN(a.clone(), end - start);
			ThompsonAutomaton matchConcatenation = matchConcatenation(aFixed, aUpToN);
			return matchConcatenation;
		}
	}

	public static ThompsonAutomaton matchUpToN(ThompsonAutomaton a, int count) {
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		automaton.setStart(s);
		State e = new State();
		s.addTransition(new EpsilonTransition(e));
		
		State current = s;
		for (int i = 0; i < count; i++) {
			ThompsonAutomaton ai = a.clone();
			current.addTransition(new EpsilonTransition(ai.start));
			ai.end.addTransition(new EpsilonTransition(e));
			current = ai.end;
		}
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchFixedLoop(ThompsonAutomaton a, int count) {
		ThompsonAutomaton[] as = copyOf(a, new ThompsonAutomaton[count], count);
		return matchConcatenation(as);
	}

	public static ThompsonAutomaton matchAlternatives(ThompsonAutomaton... as) {
		return matchAlternatives(Arrays.asList(as));
	}

	public static ThompsonAutomaton matchAlternatives(List<ThompsonAutomaton> as) {
		if (as.size() == 1) {
			return as.get(0);
		}
		GenericAutomaton automaton = new GenericAutomaton();
		State s = new State();
		State e = new State();
		automaton.setStart(s);
		for (ThompsonAutomaton a : as) {
			State n = a.start;
			s.addTransition(new EpsilonTransition(n));
			a.end.addTransition(new EpsilonTransition(e));
		}
		return new ThompsonAutomaton(automaton, s, e);
	}

	public static ThompsonAutomaton matchConcatenation(ThompsonAutomaton... as) {
		return matchConcatenation(Arrays.asList(as));
	}

	public static ThompsonAutomaton matchConcatenation(List<ThompsonAutomaton> as) {
		if (as.size() == 1) {
			return as.get(0);
		}
		
		GenericAutomaton automaton = new GenericAutomaton();
		State s = as.get(0).start;
		automaton.setStart(s);
		State e = as.get(as.size() - 1).end;
		
		State last = null;
		ListIterator<ThompsonAutomaton> aIterator = as.listIterator();
		while (aIterator.hasNext()) {
			ThompsonAutomaton a = aIterator.next();
			if (last != null) {
				last.addTransition(new EpsilonTransition(a.start));
			}
			last = a.end;
		}
		return new ThompsonAutomaton(automaton, s, e);
	}

	@Override
	public ThompsonAutomaton visitAlternative(AlternativesNode node) {
		List<ThompsonAutomaton> as = apply(node.getSubNodes());
		return matchAlternatives(as);
	}

	@Override
	public ThompsonAutomaton visitConjunctive(ConjunctiveNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ThompsonAutomaton visitConcat(ConcatNode node) {
		List<ThompsonAutomaton> as = apply(node.getSubNodes());
		return matchConcatenation(as);
	}

	@Override
	public ThompsonAutomaton visitLoop(LoopNode node) {
		ThompsonAutomaton a = node.getSubNode().apply(this);
		int from = node.getFrom();
		int to = node.getTo();
		if (to == LoopNode.INFINITY) {
			return matchUnlimitedLoop(a, from);
		} else {
			return matchRangeLoop(a, from, to);
		}
	}

	@Override
	public ThompsonAutomaton visitOptional(OptionalNode node) {
		ThompsonAutomaton a = node.getSubNode().apply(this);
		return matchOptional(a);
	}

	@Override
	public ThompsonAutomaton visitComplement(ComplementNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ThompsonAutomaton visitProChar(ProCharNode node) {
		List<ThompsonAutomaton> as = apply(node.toCharNodes());
		return matchAlternatives(as);
	}

	@Override
	public ThompsonAutomaton visitRangeChar(RangeCharNode node) {
		return match(node.getFrom(), node.getTo());
	}

	@Override
	public ThompsonAutomaton visitSingleChar(SingleCharNode node) {
		return match(node.getValue());
	}

	@Override
	public ThompsonAutomaton visitString(StringNode node) {
		return match(node.getValue());
	}

	@Override
	public ThompsonAutomaton visitEmpty(EmptyNode node) {
		return matchEmpty();
	}

	@Override
	public ThompsonAutomaton visitGroup(GroupNode node) {
		return node.getSubNode().apply(this);
	}

	private List<ThompsonAutomaton> apply(List<? extends PatternNode> nodes) {
		List<ThompsonAutomaton> as = new ArrayList<ThompsonAutomaton>(nodes.size());
		for (PatternNode node : nodes) {
			as.add(node.apply(this));
		}
		return as;
	}

	@Override
	public GenericAutomaton buildFrom(PatternNode node) {
		return buildFrom(node, ACCEPT);
	}

	@Override
	public GenericAutomaton buildFrom(PatternNode node, TokenType type) {
		if (node == null) {
			return matchNothing();
		}
		ThompsonAutomaton automaton = node.apply(this);
		automaton.end.setType(type);
		return automaton.automaton;
	}

	private static ThompsonAutomaton[] copyOf(ThompsonAutomaton a, ThompsonAutomaton[] as, int count) {
		if (count > 0 && as.length > 0) {
			as[0] = a;
		}
		for (int i = 1; i < as.length; i++) {
			as[i] = a.clone();
		}
		return as;
	}

	public static enum ThompsonToken implements TokenType {
		ACCEPT;

		@Override
		public boolean error() {
			return false;
		}
		
		@Override
		public boolean accept() {
			return true;
		}
	}
	
	public static class ThompsonAutomaton implements Cloneable {
		public GenericAutomaton automaton;
		public State start;
		public State end;

		public ThompsonAutomaton(GenericAutomaton automaton, State start, State end) {
			this.automaton = automaton;
			this.start = start;
			this.end = end;
		}
		
		@Override
		protected ThompsonAutomaton clone() {
			try {
				ThompsonAutomaton clone = (ThompsonAutomaton) super.clone();
				TokenType oldTokenType = end.getType();
				end.setType(ThompsonToken.ACCEPT);
				clone.automaton = automaton.clone();
				clone.start = clone.automaton.getStart();
				clone.end = clone.automaton.findAcceptStates().iterator().next();
				end.setType(oldTokenType);
				clone.end.setType(oldTokenType);
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
		
	}
	
}