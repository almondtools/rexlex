 package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.automaton.GenericAutomatonBuilder.matchNothing;
import static com.almondtools.rexlex.pattern.DefaultTokenType.ACCEPT;
import static java.util.Collections.emptySet;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.GenericAutomaton.ExactTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.RangeTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.automaton.GlushkovAutomatonBuilder.GlushkovAutomaton;
import com.almondtools.rexlex.pattern.CharClassNormalizer;
import com.almondtools.rexlex.pattern.LoopNormalizer;
import com.almondtools.rexlex.pattern.Pattern;
import com.almondtools.rexlex.pattern.Pattern.AlternativesNode;
import com.almondtools.rexlex.pattern.Pattern.ComplementNode;
import com.almondtools.rexlex.pattern.Pattern.ConcatNode;
import com.almondtools.rexlex.pattern.Pattern.ConjunctiveNode;
import com.almondtools.rexlex.pattern.Pattern.EmptyNode;
import com.almondtools.rexlex.pattern.Pattern.GroupNode;
import com.almondtools.rexlex.pattern.Pattern.LoopNode;
import com.almondtools.rexlex.pattern.Pattern.OptionalNode;
import com.almondtools.rexlex.pattern.Pattern.PatternNode;
import com.almondtools.rexlex.pattern.Pattern.ProCharNode;
import com.almondtools.rexlex.pattern.Pattern.RangeCharNode;
import com.almondtools.rexlex.pattern.Pattern.SingleCharNode;
import com.almondtools.rexlex.pattern.Pattern.StringNode;
import com.almondtools.util.collections.HashSets;

public class GlushkovAutomatonBuilder implements Pattern.PatternNodeVisitor<GlushkovAutomaton>, AutomatonBuilder {

	public GlushkovAutomatonBuilder() {
	}

	@Override
	public GlushkovAutomaton visitAlternative(AlternativesNode node) {
		List<? extends PatternNode> subNodes = node.getSubNodes();
		GlushkovAutomaton automaton = new GlushkovAutomaton(apply(subNodes));
		
		Set<State> first = new LinkedHashSet<State>();
		Set<State> last = new LinkedHashSet<State>();
		boolean empty = false;
		
		for (PatternNode subNode : subNodes) {
			first.addAll(automaton.getFirst(subNode));
			last.addAll(automaton.getLast(subNode));
			empty |= automaton.isEmpty(subNode);
		}

		automaton.setFirst(node, first);
		automaton.setLast(node, last);
		automaton.setEmpty(node, empty);
		
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitConjunctive(ConjunctiveNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GlushkovAutomaton visitConcat(ConcatNode node) {
		List<? extends PatternNode> subNodes = node.getSubNodes();
		GlushkovAutomaton automaton = new GlushkovAutomaton(apply(subNodes));
		
		Set<State> first = null;
		Set<State> last = null;
		boolean empty = true;

		ListIterator<? extends PatternNode> isubNodes = subNodes.listIterator();
		
		boolean emptyHead = true;
		while (isubNodes.hasNext()) {
			PatternNode subNode = isubNodes.next();
			if (first == null) {
				first = new LinkedHashSet<State>(automaton.getFirst(subNode));
			} else if (emptyHead) {
				first.addAll(automaton.getFirst(subNode));
			}
			emptyHead &= automaton.isEmpty(subNode);
		}
		boolean emptyTail = true;
		while (isubNodes.hasPrevious()) {
			PatternNode subNode = isubNodes.previous();
			if (last == null) {
				last = new LinkedHashSet<State>(automaton.getFirst(subNode));
			} else if (emptyTail) {
				last.addAll(automaton.getFirst(subNode));
			}
			emptyTail &= automaton.isEmpty(subNode);
		}
		
		Set<State> before = new LinkedHashSet<State>();
		for (PatternNode subNode : subNodes) {
			empty &= automaton.isEmpty(subNode);
			for (State s : before) {
				automaton.addFollow(s, automaton.getFirst(subNode));
			}
			before = automaton.getLast(subNode);
		}
		
		automaton.setFirst(node, first);
		automaton.setLast(node, last);
		automaton.setEmpty(node, empty);

		return automaton;
	}

	@Override
	public GlushkovAutomaton visitLoop(LoopNode node) {
		PatternNode subNode = node.getSubNode();
		GlushkovAutomaton automaton = new GlushkovAutomaton(subNode.apply(this));
		
		Set<State> first = new LinkedHashSet<State>(automaton.getFirst(subNode));
		Set<State> last = new LinkedHashSet<State>(automaton.getLast(subNode));
		boolean empty = true;

		for (State s : last) {
			automaton.addFollow(s, automaton.getFirst(subNode));
		}
		
		automaton.setFirst(node, first);
		automaton.setLast(node, last);
		automaton.setEmpty(node, empty);
		
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitOptional(OptionalNode node) {
		PatternNode subNode = node.getSubNode();
		GlushkovAutomaton automaton = new GlushkovAutomaton(subNode.apply(this));
		
		Set<State> first = new LinkedHashSet<State>(automaton.getFirst(subNode));
		Set<State> last = new LinkedHashSet<State>(automaton.getLast(subNode));
		boolean empty = true;

		automaton.setFirst(node, first);
		automaton.setLast(node, last);
		automaton.setEmpty(node, empty);
		
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitComplement(ComplementNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GlushkovAutomaton visitProChar(ProCharNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GlushkovAutomaton visitRangeChar(RangeCharNode node) {
		GlushkovAutomaton automaton = new GlushkovAutomaton(1);
		State state = new State();
		Set<State> first = HashSets.ofLinked(state);
		Set<State> last = HashSets.ofLinked(state);
		boolean empty = false;
		automaton.setFirst(node, first);
		automaton.setLast(node, last);
		automaton.setEmpty(node, empty);
		automaton.setCharRange(state, node.getFrom(), node.getTo());
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitSingleChar(SingleCharNode node) {
		GlushkovAutomaton automaton = new GlushkovAutomaton(1);
		State state = new State();
		Set<State> first = HashSets.of(state);
		Set<State> last = HashSets.of(state);
		boolean empty = false;
		automaton.setFirst(node, first);
		automaton.setLast(node, last);
		automaton.setEmpty(node, empty);
		automaton.setCharRange(state, node.getValue());
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitString(StringNode node) {
		ConcatNode inlined = new ConcatNode(node.toChars());
		GlushkovAutomaton automaton = new GlushkovAutomaton(inlined.apply(this));
		automaton.setFirst(node, automaton.getFirst(inlined));
		automaton.setLast(node, automaton.getLast(inlined));
		automaton.setEmpty(node, automaton.isEmpty(inlined));
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitEmpty(EmptyNode node) {
		GlushkovAutomaton automaton = new GlushkovAutomaton();
		
		automaton.setFirst(node, new LinkedHashSet<State>());
		automaton.setLast(node, new LinkedHashSet<State>());
		automaton.setEmpty(node, true);
		
		return automaton;
	}

	@Override
	public GlushkovAutomaton visitGroup(GroupNode node) {
		return node.apply(this);
	}

	private List<GlushkovAutomaton> apply(List<? extends PatternNode> nodes) {
		List<GlushkovAutomaton> as = new ArrayList<GlushkovAutomaton>(nodes.size());
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
		PatternNode normalized = node.apply(new CharClassNormalizer()).apply(new LoopNormalizer());
		GlushkovAutomaton automaton = normalized.apply(this);
		return automaton.build(normalized, type);
	}

	public enum GlushkovToken implements TokenType {
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
	
	public static class GlushkovAutomaton implements Cloneable {

		private Map<PatternNode, Set<State>> first;
		private Map<PatternNode, Set<State>> last;
		private Map<State, Set<State>> follow;
		private Map<PatternNode, Boolean> empty;
		private Map<State, char[]> charRanges;
		private int length;
		
		public GlushkovAutomaton(List<GlushkovAutomaton> subs) {
			this.first = new IdentityHashMap<Pattern.PatternNode, Set<State>>();
			this.last = new IdentityHashMap<Pattern.PatternNode, Set<State>>();
			this.follow = new IdentityHashMap<GenericAutomaton.State, Set<State>>();
			this.empty = new IdentityHashMap<Pattern.PatternNode, Boolean>();
			this.charRanges = new IdentityHashMap<GenericAutomaton.State, char[]>();
			this.length = 0;
			for (GlushkovAutomaton sub : subs) {
				first.putAll(sub.first);
				last.putAll(sub.last);
				follow.putAll(sub.follow);
				empty.putAll(sub.empty);
				charRanges.putAll(sub.charRanges);
				length += sub.length;
			}
		}

		public GlushkovAutomaton(GlushkovAutomaton sub) {
			this.first = new IdentityHashMap<PatternNode, Set<State>>(sub.first);
			this.last = new IdentityHashMap<PatternNode, Set<State>>(sub.last);
			this.follow = new IdentityHashMap<State, Set<State>>(sub.follow);
			this.empty = new IdentityHashMap<PatternNode, Boolean>(sub.empty);
			this.charRanges = new IdentityHashMap<State, char[]>(sub.charRanges);
			this.length = sub.length;
		}

		public GlushkovAutomaton(int length) {
			this.first = new IdentityHashMap<PatternNode, Set<State>>();
			this.last = new IdentityHashMap<PatternNode, Set<State>>();
			this.follow = new IdentityHashMap<State, Set<State>>();
			this.empty = new IdentityHashMap<PatternNode, Boolean>();
			this.charRanges = new IdentityHashMap<State, char[]>();
			this.length = length;
		}

		public GlushkovAutomaton() {
			this(0);
		}

		public GenericAutomaton build(PatternNode root, TokenType type) {
			State start = new State();
			for (State next : getFirst(root)) {
				char[] event = charRanges.get(next);
				if (event.length == 1) {
					start.addTransition(new ExactTransition(event[0], next));
				} else {
					start.addTransition(new RangeTransition(event[0], event[1], next));
				}
			}
			for (State current : follow.keySet()) {
				for (State next : getFollow(current)) {
					char[] event = charRanges.get(next);
					if (event.length == 1) {
						current.addTransition(new ExactTransition(event[0], next));
					} else {
						current.addTransition(new RangeTransition(event[0], event[1], next));
					}
				}
			}
			if (isEmpty(root)) {
				start.setType(type);
			}
			for (State state : getLast(root)) {
				state.setType(type);
			}
			return new GenericAutomaton(start);
		}

		public boolean isEmpty(PatternNode key) {
			Boolean result = empty.get(key);
			if (result == null) {
				return true;
			}
			return result;
		}
		
		public void setCharRange(State state, char... chars) {
			charRanges.put(state, chars);
		}

		public void setEmpty(PatternNode key, boolean value) {
			empty.put(key, value);
		}
		
		public Set<State> getFirst(PatternNode key) {
			Set<State> list = first.get(key);
			if (list == null) {
				return emptySet();
			}
			return list;
		}
		
		public void setFirst(PatternNode key, Set<State> list) {
			first.put(key, list);
		}

		public Set<State> getLast(PatternNode key) {
			Set<State> list = last.get(key);
			if (list == null) {
				return emptySet();
			}
			return list;
		}

		public void setLast(PatternNode key, Set<State> list) {
			last.put(key, list);
		}

		public Set<State> getFollow(State key) {
			Set<State> set = follow.get(key);
			if (set == null) {
				return emptySet();
			}
			return set;
		}

		public void addFollow(State key, Set<State> states) {
			Set<State> set = follow.get(key);
			if (set == null) {
				set = new LinkedHashSet<State>();
				follow.put(key, set);
			}
			set.addAll(states);
		}

	}
	
}