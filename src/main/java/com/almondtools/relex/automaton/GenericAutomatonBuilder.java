package com.almondtools.relex.automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.almondtools.relex.TokenType;
import com.almondtools.relex.automaton.GenericAutomaton.EpsilonTransition;
import com.almondtools.relex.automaton.GenericAutomaton.EventTransition;
import com.almondtools.relex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.relex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.relex.automaton.GenericAutomaton.State;
import com.almondtools.relex.automaton.GenericAutomaton.Transition;
import com.almondtools.relex.pattern.DefaultTokenType;
import com.almondtools.relex.pattern.Pattern;
import com.almondtools.relex.pattern.TokenTypeFactory;
import com.almondtools.relex.pattern.Pattern.AlternativesNode;
import com.almondtools.relex.pattern.Pattern.ComplementNode;
import com.almondtools.relex.pattern.Pattern.ConcatNode;
import com.almondtools.relex.pattern.Pattern.ConjunctiveNode;
import com.almondtools.relex.pattern.Pattern.EmptyNode;
import com.almondtools.relex.pattern.Pattern.GroupNode;
import com.almondtools.relex.pattern.Pattern.LoopNode;
import com.almondtools.relex.pattern.Pattern.OptionalNode;
import com.almondtools.relex.pattern.Pattern.PatternNode;
import com.almondtools.relex.pattern.Pattern.ProCharNode;
import com.almondtools.relex.pattern.Pattern.RangeCharNode;
import com.almondtools.relex.pattern.Pattern.SingleCharNode;
import com.almondtools.relex.pattern.Pattern.StringNode;

public class GenericAutomatonBuilder implements Pattern.PatternNodeVisitor<GenericAutomaton>, AutomatonBuilder {

	public GenericAutomatonBuilder() {
	}

	public static GenericAutomaton match(char value) {
		State s = new State();
		State e = new State(DefaultTokenType.ACCEPT);

		s.addTransition(new ExactTransition(value, e));
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton match(String value) {
		State s = new State();
		State e = new State(DefaultTokenType.ACCEPT);
		
		State current = s;
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length - 1; i++) {
			State next = new State();
			current.addTransition(new ExactTransition(chars[i], next));
			current = next;
		}
		current.addTransition(new ExactTransition(chars[chars.length - 1], e));
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton match(char from, char to) {
		State s = new State();
		State e = new State(DefaultTokenType.ACCEPT);
		if (from > to) {
			char temp = from;
			from = to;
			to = temp;
		}
		s.addTransition(new RangeTransition(from, to, e));
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchAnyChar() {
		State s = new State();
		State e = new State(DefaultTokenType.ACCEPT);
		s.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, e));
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchAnyOf(char... chars) {
		State s = new State();
		State e = new State(DefaultTokenType.ACCEPT);
		for (char c : chars) {
			s.addTransition(new ExactTransition(c, e));
		}
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchEmpty() {
		State s = new State(DefaultTokenType.ACCEPT);
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchNothing() {
		State s = new State();
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchOptional(GenericAutomaton a) {
		State s = new State(DefaultTokenType.ACCEPT);
		s.addTransition(new EpsilonTransition(a.getStart()));
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchComplement(GenericAutomaton a) {
		GenericAutomaton automaton = a.eliminateEpsilons().determinize().totalizeAndClean().minimize();
		for (State current : automaton.findAllStates()) {
			if (current.accept()) {
				current.setType(null);
			} else if (current.error()) {
				current.setType(DefaultTokenType.IGNORE);
				current.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, current));
			} else {
				current.setType(DefaultTokenType.IGNORE);
			}
		}
		return automaton;
	}

	public static GenericAutomaton matchUnlimitedLoop(GenericAutomaton a, int start) {
		if (start == 0) {
			return matchStarLoop(a);
		} else {
			GenericAutomaton[] as = copyOf(a, new GenericAutomaton[start + 1], start);
			as[start] = matchStarLoop(a.clone());
			return matchConcatenation(as);
		}
	}

	public static GenericAutomaton matchStarLoop(GenericAutomaton a) {
		State s = new State(DefaultTokenType.ACCEPT);
		State next = a.getStart();
		s.addTransition(new EpsilonTransition(next));
		for (State p : next.findAcceptStates()) {
			p.addTransition(new EpsilonTransition(s));
		}
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchRangeLoop(GenericAutomaton a, int start, int end) {
		if (start == end) {
			return matchFixedLoop(a, start);
		} else {
			GenericAutomaton aFixed = matchFixedLoop(a.clone(), start);
			GenericAutomaton aUpToN = matchUpToN(a, end - start);
			return matchConcatenation(aFixed, aUpToN);
		}
	}

	public static GenericAutomaton matchUpToN(GenericAutomaton a, int count) {
		State s = new State(DefaultTokenType.ACCEPT);
		
		State current = s;
		if (count > 0) {
			State start = a.getStart();
			current.addTransition(new EpsilonTransition(start));
			for (int i = 1; i < count; i++) {
				State next = start.cloneTree();
				for (State f : current.findAcceptStates()) {
					f.addTransition(new EpsilonTransition(next));
				}
				current = next;
			}
		}
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchFixedLoop(GenericAutomaton a, int count) {
		GenericAutomaton[] as = copyOf(a, new GenericAutomaton[count], count);
		return matchConcatenation(as);
	}

	public static GenericAutomaton matchAlternatives(GenericAutomaton... as) {
		return matchAlternatives(Arrays.asList(as));
	}

	public static GenericAutomaton matchAlternatives(List<GenericAutomaton> as) {
		if (as.size() == 1) {
			return as.get(0);
		}
		State s = new State();
		for (GenericAutomaton a : as) {
			State next = a.getStart();
			s.addTransition(new EpsilonTransition(next));
		}
		return new GenericAutomaton(s);
	}

	public static GenericAutomaton matchConjunctive(GenericAutomaton... as) {
		return matchConjunctive(Arrays.asList(as));
	}

	public static GenericAutomaton matchConjunctive(List<GenericAutomaton> as) {
		if (as.size() == 1) {
			return as.get(0);
		}
		GenericAutomaton a0 = as.get(0);
		TokenTypeFactory tokenTypes = a0.getTokenTypes();
		State current = a0.eliminateEpsilons().eliminateDuplicateFinalStates().eliminateDuplicateTransitions().getStart();
		for (int i = 1; i < as.size(); i++) {
			State next = as.get(i).eliminateEpsilons().eliminateDuplicateFinalStates().eliminateDuplicateTransitions().getStart();
			current = intersectStates(current, next, tokenTypes);
		}
		return new GenericAutomaton(current, tokenTypes);
	}

	public static GenericAutomaton matchAllPrefixes(GenericAutomaton... as) {
		return matchAllPrefixes(Arrays.asList(as));
	}

	public static GenericAutomaton matchAllPrefixes(List<GenericAutomaton> as) {
		if (as.size() == 1) {
			return as.get(0);
		}
		GenericAutomaton a0 = as.get(0);
		TokenTypeFactory tokenTypes = a0.getTokenTypes();
		State current = a0.eliminateEpsilons().eliminateDuplicateFinalStates().eliminateDuplicateTransitions().getStart();
		for (int i = 1; i < as.size(); i++) {
			State next = as.get(i).eliminateEpsilons().eliminateDuplicateFinalStates().eliminateDuplicateTransitions().getStart();
			current = mergePrefixes(current, next, tokenTypes);
		}
		return new GenericAutomaton(current, tokenTypes);
	}

	public static GenericAutomaton matchWithPrefix(GenericAutomaton a, GenericAutomaton prefix) {
		TokenTypeFactory tokenTypes = a.getTokenTypes();
		State as = a.getStart();
		State prefixs = prefix.getStart();
		State s = prefixStates(as, prefixs, tokenTypes);
		return new GenericAutomaton(s, tokenTypes);
	}

	public static GenericAutomaton matchConcatenation(GenericAutomaton... as) {
		return matchConcatenation(Arrays.asList(as));
	}

	public static GenericAutomaton matchConcatenation(List<GenericAutomaton> as) {
		if (as.size() == 1) {
			return as.get(0);
		}
		State s = new State();
		State current = null;
		ListIterator<GenericAutomaton> aIterator = as.listIterator(as.size());
		while (aIterator.hasPrevious()) {
			GenericAutomaton ai = aIterator.previous();
			State next = ai.getStart();
			if (current != null) {
				for (State f : next.findAcceptStates()) {
					f.setType(null);
					f.addTransition(new EpsilonTransition(current));
				}
			}
			current = next;
		}
		if (current != null) {
			s.addTransition(new EpsilonTransition(current));
		}
		return new GenericAutomaton(s);
	}

	static GenericAutomaton atLeastOne(GenericAutomaton a) {
		return matchConjunctive(a, matchUnlimitedLoop(matchAnyChar(), 1));
	}

	@Override
	public GenericAutomaton visitAlternative(AlternativesNode node) {
		List<GenericAutomaton> as = apply(node.getSubNodes());
		return matchAlternatives(as);
	}

	@Override
	public GenericAutomaton visitConjunctive(ConjunctiveNode node) {
		List<GenericAutomaton> as = apply(node.getSubNodes());
		return matchConjunctive(as);
	}

	@Override
	public GenericAutomaton visitConcat(ConcatNode node) {
		List<GenericAutomaton> as = apply(node.getSubNodes());
		return matchConcatenation(as);
	}

	@Override
	public GenericAutomaton visitLoop(LoopNode node) {
		GenericAutomaton a = node.getSubNode().apply(this);
		int from = node.getFrom();
		int to = node.getTo();
		if (to == LoopNode.INFINITY) {
			return matchUnlimitedLoop(a, from);
		} else {
			return matchRangeLoop(a, from, to);
		}
	}

	@Override
	public GenericAutomaton visitOptional(OptionalNode node) {
		GenericAutomaton a = node.getSubNode().apply(this);
		return matchOptional(a);
	}

	@Override
	public GenericAutomaton visitComplement(ComplementNode node) {
		GenericAutomaton a = node.getSubNode().apply(this);
		return matchComplement(a);
	}

	@Override
	public GenericAutomaton visitProChar(ProCharNode node) {
		List<GenericAutomaton> as = apply(node.toCharNodes());
		return matchAlternatives(as);
	}

	@Override
	public GenericAutomaton visitRangeChar(RangeCharNode node) {
		return match(node.getFrom(), node.getTo());
	}

	@Override
	public GenericAutomaton visitSingleChar(SingleCharNode node) {
		return match(node.getValue());
	}

	@Override
	public GenericAutomaton visitString(StringNode node) {
		return match(node.getValue());
	}

	@Override
	public GenericAutomaton visitEmpty(EmptyNode node) {
		return matchEmpty();
	}

	@Override
	public GenericAutomaton visitGroup(GroupNode node) {
		return node.getSubNode().apply(this);
	}

	private List<GenericAutomaton> apply(List<? extends PatternNode> nodes) {
		List<GenericAutomaton> as = new ArrayList<GenericAutomaton>(nodes.size());
		for (PatternNode node : nodes) {
			as.add(node.apply(this));
		}
		return as;
	}

	@Override
	public GenericAutomaton buildFrom(PatternNode node) {
		if (node == null) {
			return matchNothing();
		}
		return node.apply(this);
	}

	@Override
	public GenericAutomaton buildFrom(PatternNode node, TokenType type) {
		if (node == null) {
			return matchNothing();
		}
		GenericAutomaton automaton = node.apply(this);
		for (State state : automaton.findAcceptStates()) {
			state.setType(type);
		}
		return automaton;
	}

	private static GenericAutomaton[] copyOf(GenericAutomaton a, GenericAutomaton[] as, int count) {
		if (count > 0 && as.length > 0) {
			as[0] = a;
		}
		for (int i = 1; i < as.length; i++) {
			as[i] = a.clone();
		}
		return as;
	}

	static State intersectStates(State s1, State s2, TokenTypeFactory tokenTypes) {
		return new StateIntersector(tokenTypes).intersect(s1, s2);
	}

	static State prefixStates(State s, State prefix, TokenTypeFactory tokenTypes) {
		State suffix = new State(DefaultTokenType.IGNORE);
		suffix.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, suffix));
		for (State p : prefix.findAcceptStates()) {
			p.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, suffix));
		}
		State sprefixed = new StateIntersector(tokenTypes).intersect(s, prefix);
		suffix.setType(DefaultTokenType.ERROR);
		return sprefixed;
	}

	static State mergePrefixes(State s1, State s2, TokenTypeFactory tokenTypes) {
		State suffix = new State(DefaultTokenType.IGNORE);
		suffix.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, suffix));
		for (State s : s1.findAcceptStates()) {
			s.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, suffix));
		}
		for (State s : s2.findAcceptStates()) {
			s.addTransition(new RangeTransition(Character.MIN_VALUE, Character.MAX_VALUE, suffix));
		}
		State s12 = new StateIntersector(tokenTypes).intersect(s1, s2);
		suffix.setType(DefaultTokenType.ERROR);
		return s12;
	}

	static class StateIntersector {

		private TokenTypeFactory tokenTypes;
		private List<StateIntersection> worklist;
		private Map<StateIntersection, StateIntersection> newstates;

		public StateIntersector(TokenTypeFactory tokenTypes) {
			this.tokenTypes = tokenTypes;
			this.worklist = new LinkedList<StateIntersection>();
			this.newstates = new HashMap<StateIntersection, StateIntersection>();
		}

		public State intersect(State s1, State s2) {
			if (s1 == s2) {
				return s1;
			}
			StateIntersection start = new StateIntersection(new State(), s1, s2);
			worklist.add(start);
			newstates.put(start, start);
			while (worklist.size() > 0) {
				StateIntersection mergedState = worklist.remove(0);

				State lstate = mergedState.left;
				State rstate = mergedState.right;

				TokenType ltype = tokenTypes.union(lstate.getTypeClosure());
				TokenType rtype = tokenTypes.union(rstate.getTypeClosure());
				mergedState.result.setType(tokenTypes.intersect(ltype, rtype));

				List<EventTransition> ltransitions = lstate.getSortedNextClosure();
				List<EventTransition> rtransitions = rstate.getSortedNextClosure();
				mergedState.result.addTransitions(mergeEventTransitions(ltransitions, rtransitions));
			}
			return start.result;
		}

		private List<Transition> mergeEventTransitions(List<EventTransition> ltransitions, List<EventTransition> rtransitions) {
			List<EventTransition> lctransitions = new ArrayList<EventTransition>(ltransitions);
			List<EventTransition> rctransitions = new ArrayList<EventTransition>(rtransitions);
			List<Transition> mtransitions = new LinkedList<Transition>();
			int il = 0;
			int lsize = ltransitions.size();
			int ir = 0;
			int rsize = rtransitions.size();
			while (il < lsize && ir < rsize) {
				EventTransition tl = lctransitions.get(il);
				EventTransition tr = rctransitions.get(ir);

				int shift = computeShift(tl, tr);
				if (shift == 0) {
					StateIntersection result = fetchStateIntersection(tl.getTarget(), tr.getTarget());
					mtransitions.add((Transition) mergeTransition(tl, tr, result.result));
					il++;
					ir++;
					for (EventTransition ttl : overlapping(tr, lctransitions, il)) {
						StateIntersection lresult = fetchStateIntersection(ttl.getTarget(), tr.getTarget());
						Transition t = mergeTransition(ttl, tr, lresult.result);
						mtransitions.add(t);
					}
					for (EventTransition ttr : overlapping(tl, rctransitions, ir)) {
						StateIntersection rresult = fetchStateIntersection(tl.getTarget(), ttr.getTarget());
						Transition t = mergeTransition(tl, ttr, rresult.result);
						mtransitions.add(t);
					}
				} else if (shift == -1) {
					il++;
				} else if (shift == 1) {
					ir++;
				}
			}
			return mtransitions;
		}

		private List<EventTransition> overlapping(EventTransition t, List<EventTransition> transitions, int start) {
			List<EventTransition> overlap = new ArrayList<EventTransition>();
			for (int i = start; i < transitions.size(); i++) {
				EventTransition tt = transitions.get(i);
				int shift = computeShift(t, tt);
				if (shift == 0) {
					overlap.add(tt);
				} else {
					break;
				}
			}
			return overlap;
		}

		private int computeShift(EventTransition tl, EventTransition tr) {
			if (tl.getTo() < tr.getFrom()) {
				return -1;
			} else if (tr.getTo() < tl.getFrom()) {
				return 1;
			} else {
				return 0;
			}
		}

		private StateIntersection fetchStateIntersection(State state1, State state2) {
			StateIntersection key = new StateIntersection(state1, state2);
			StateIntersection result = newstates.get(key);
			if (result == null) {
				if (state1 == state2) {
					key.result = state1;
				} else {
					key.result = new State();
					worklist.add(key);
				}
				newstates.put(key, key);
				result = key;
			}
			return result;
		}

		private EventTransition mergeTransition(EventTransition transition1, EventTransition transition2, State result) {
			char from = maximum(transition1.getFrom(), transition2.getFrom());
			char to = minimum(transition1.getTo(), transition2.getTo());
			EventTransition transition = null;
			if (from == to) {
				transition = new ExactTransition(from, result);
			} else {
				transition = new RangeTransition(from, to, result);
			}
			return transition;
		}

		private final char minimum(char c1, char c2) {
			return c1 < c2 ? c1 : c2;
		}

		private final char maximum(char c1, char c2) {
			return c1 > c2 ? c1 : c2;
		}

	}

	static class StateIntersection {

		public State result;
		public State left;
		public State right;

		public StateIntersection(State left, State right) {
			this.left = left;
			this.right = right;
		}

		public StateIntersection(State merged, State left, State right) {
			this.result = merged;
			this.left = left;
			this.right = right;
		}

		@Override
		public int hashCode() {
			return left.hashCode() + right.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StateIntersection other = (StateIntersection) obj;
			return this.left == other.left && this.right == other.right;
		}

	}

}