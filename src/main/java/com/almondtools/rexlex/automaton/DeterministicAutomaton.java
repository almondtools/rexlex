package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.automaton.AutomatonProperty.ACYCLIC;
import static com.almondtools.rexlex.automaton.AutomatonProperty.CYCLIC;
import static com.almondtools.rexlex.automaton.AutomatonProperty.LINEAR;
import static com.almondtools.rexlex.automaton.AutomatonProperty.UNKNOWN;
import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.almondtools.rexlex.Token;
import com.almondtools.rexlex.TokenFactory;
import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.FromDeterministicAutomaton.ToGenericAutomaton;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToMinimalDeterministicAutomaton;
import com.almondtools.rexlex.pattern.DefaultTokenType;
import com.almondtools.rexlex.pattern.TokenIterator;

import net.amygdalum.util.io.CharProvider;
import net.amygdalum.util.io.StringCharProvider;
import net.amygdalum.util.builders.Sets;

public class DeterministicAutomaton implements Automaton {

	private State start;
	private State error;

	private AutomatonProperty property;

	public DeterministicAutomaton(State start) {
		this.start = start;
		this.property = UNKNOWN;
	}

	public DeterministicAutomaton(State start, State error) {
		this.start = start;
		this.error = error;
		this.property = UNKNOWN;
	}

	@Override
	public String getId() {
		if (start == null) {
			return "null";
		} else {
			return start.getId();
		}
	}

	State getStart() {
		return start;
	}

	void setError(State error) {
		this.error = error;
	}

	State getError() {
		return error;
	}

	@Override
	public TokenType getErrorType() {
		if (error != null && error.getType() != null) {
			return error.getType();
		} else {
			return DefaultTokenType.ERROR;
		}
	}

	public <T extends Automaton> T toAutomaton(ToAutomaton<DeterministicAutomaton, T> builder) {
		return builder.transform(this);
	}

	SortedSet<Character> computeRelevantCharacters() {
		SortedSet<Character> relevant = new TreeSet<Character>();
		for (State state : findAllStates()) {
			relevant.addAll(state.getRelevantCharacters());
		}
		return relevant;
	}

	Set<State> findAcceptStates() {
		return start.findAcceptStates();
	}

	Set<State> findLiveStates() {
		return start.findLiveStates();
	}

	Set<State> findAllStates() {
		return start.findReachableStates();
	}

	Set<State> findDeadStates() {
		return Sets.hashed(findAllStates()).minus(findLiveStates()).build();
	}

	@Override
	public AutomatonProperty getProperty() {
		if (property.isUnknown()) {
			property = computeProperties();
		}
		return property;
	}

	private AutomatonProperty computeProperties() {
		Set<State> visited = new HashSet<State>();
		List<State> nexts = new LinkedList<State>();
		nexts.add(start);
		AutomatonProperty property = LINEAR;
		loop: while (!nexts.isEmpty()) {
			State next = nexts.remove(0);
			visited.add(next);

			List<Transition> transitions = next.getTransitions();
			int validTargets = 0;
			for (Transition transition : transitions) {
				State target = transition.getTarget();
				if (!target.error()) {
					validTargets += transition.getTo() - transition.getFrom() + 1;
					if (validTargets > 1) {
						property = ACYCLIC;
					}
					if (visited.contains(target)) {
						if (findPath(target, next) != null) {
							property = CYCLIC;
							break loop;
						}
					} else {
						nexts.add(target);
					}
				}
			}
		}
		return property;
	}

	public String findPathTo(State state) {
		return findPath(start, state);
	}

	private String findPath(State from, State to) {
		Map<State, String> paths = new IdentityHashMap<State, String>();
		paths.put(from, "");
		List<State> nexts = new LinkedList<State>();
		nexts.add(from);
		while (!nexts.isEmpty()) {
			State next = nexts.remove(0);
			if (next == to) {
				return paths.get(next);
			}

			List<Transition> transitions = next.getTransitions();
			for (Transition transition : transitions) {
				State target = transition.getTarget();
				if (!paths.containsKey(target)) {
					String path = paths.get(next) + transition.getFrom();
					paths.put(target, path);
					nexts.add(target);
				}
			}
		}
		return null;
	}

	public State findState(String path) {
		State state = start;
		StringCharProvider chars = new StringCharProvider(path, 0);
		while (!chars.finished()) {
			state = state.next(chars.next());
		}
		return state;
	}

	@Override
	public Iterable<String> getSamples(final int limit) {
		return new Iterable<String>() {

			@Override
			public Iterator<String> iterator() {
				return new SampleIterator(limit);
			}
		};
	}

	@Override
	public AutomatonMatcher matcher() {
		return new Matcher();
	}

	@Override
	public <T extends Token> Iterator<T> tokenize(CharProvider chars, TokenFactory<T> factory) {
		return new TokenIterator<T>(this, chars, factory);
	}

	@Override
	public DeterministicAutomaton revert() {
		return toAutomaton(new ToGenericAutomaton()).revert().toAutomaton(new ToMinimalDeterministicAutomaton(error.getType()));
	}

	@Override
	public DeterministicAutomatonExport store(String name) {
		return new DeterministicAutomatonExport(this, name);
	}

	static interface StateVisitor<T> {

		T visitState(State state);

	}

	static class State implements Cloneable {

		private TokenType type;
		private List<Transition> transitions;

		public State() {
			this.transitions = new ArrayList<Transition>();
		}

		public State(TokenType type) {
			this.type = type;
			this.transitions = new ArrayList<Transition>();
		}

		public boolean accept() {
			return type != null && type.accept();
		}

		public boolean error() {
			return type != null && type.error();
		}

		public String getId() {
			return String.valueOf(System.identityHashCode(this));
		}

		void setType(TokenType type) {
			this.type = type;
		}

		public TokenType getType() {
			return type;
		}

		public void addTransition(Transition transition) {
			this.transitions.add(transition);
		}

		public List<Transition> getTransitions() {
			return transitions;
		}

		public SortedSet<Character> getRelevantCharacters() {
			SortedSet<Character> relevant = new TreeSet<Character>();
			relevant.add(Character.MIN_VALUE);
			for (Transition transition : transitions) {
				char from = transition.getFrom();
				relevant.add(from);

				char to = transition.getTo();
				if (to < Character.MAX_VALUE) {
					relevant.add(after(to));
				}
			}
			return relevant;
		}

		public Set<State> getDirectlyReachableStates() {
			Set<State> reachable = new LinkedHashSet<State>();
			for (Transition transition : transitions) {
				reachable.add(transition.getTarget());
			}
			return reachable;
		}

		public State next(char ch) {
			for (Transition transition : transitions) {
				if (transition.matches(ch)) {
					return transition.getTarget();
				}
			}
			return null;
		}

		void mergeAdjacentTransitions() {
			SortedSet<Transition> sortedTransitions = computeSortedTransitions();
			LinkedList<Transition> mergedTransitions = new LinkedList<Transition>();

			for (Transition transition : sortedTransitions) {
				if (mergedTransitions.isEmpty()) {
					mergedTransitions.add(transition);
				} else {
					Transition last = mergedTransitions.getLast();
					char lastFrom = last.getFrom();
					char lastTo = last.getTo();
					char nextFrom = transition.getFrom();
					char nextTo = transition.getTo();
					if (lastTo + 1 >= nextFrom && last.getTarget() == transition.getTarget()) {
						mergedTransitions.removeLast();
						char from = lastFrom < nextFrom ? lastFrom : nextFrom;
						char to = lastTo < nextTo ? nextTo : lastTo;
						mergedTransitions.add(new RangeTransition(from, to, last.getTarget()));
					} else {
						mergedTransitions.add(transition);
					}
				}
			}
			this.transitions = new ArrayList<Transition>(mergedTransitions);
		}

		@Override
		public State clone() {
			try {
				State clone = (State) super.clone();
				clone.type = type;
				clone.transitions = new ArrayList<Transition>(transitions.size());
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		public com.almondtools.rexlex.automaton.GenericAutomaton.State toNFA() {
			com.almondtools.rexlex.automaton.GenericAutomaton.State nfastate = new com.almondtools.rexlex.automaton.GenericAutomaton.State();
			nfastate.setType(type);
			return nfastate;
		}

		public <T> T apply(StateVisitor<T> visitor) {
			return visitor.visitState(this);
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder("state(").append(getId()).append(")\n");
			for (Transition transition : transitions) {
				buffer.append('\t').append(transition.toString()).append('\n');
			}
			return buffer.toString();
		}

		void addErrorTransitions(State error) {
			SortedSet<Transition> sortedTransitions = computeSortedTransitions();
			char current = Character.MIN_VALUE;
			for (Transition transition : sortedTransitions) {
				char from = transition.getFrom();
				char to = transition.getTo();
				if (from == current + 1) {
					transitions.add(new ExactTransition(current, error));
				} else if (from > current) {
					transitions.add(new RangeTransition(current, before(from), error));
				}
				current = after(to);
			}
			if (current == Character.MAX_VALUE) {
				transitions.add(new ExactTransition(Character.MAX_VALUE, error));
			} else {// current < Character.MAX_VALUE
				transitions.add(new RangeTransition(current, Character.MAX_VALUE, error));
			}
		}

		SortedSet<Transition> computeSortedTransitions() {
			SortedSet<Transition> sortedTransitions = new TreeSet<Transition>(new Comparator<Transition>() {

				@Override
				public int compare(Transition o1, Transition o2) {
					int result = o1.getFrom() - o2.getFrom();
					if (result == 0) {
						result = o1.getTo() - o2.getTo();
					}
					return result;
				}
			});
			sortedTransitions.addAll(transitions);
			return sortedTransitions;
		}

		public Set<State> findAcceptStates() {
			return apply(new FindAcceptStates());
		}

		public Set<State> findReachableStates() {
			return apply(new FindStates());
		}

		public Set<State> findLiveStates() {
			Set<State> states = findReachableStates();
			Map<State, Set<State>> map = new HashMap<State, Set<State>>();
			for (State s : states) {
				map.put(s, new HashSet<State>());
			}
			for (State s : states) {
				for (Transition transition : s.getTransitions()) {
					map.get(transition.getTarget()).add(s);
				}
			}
			Set<State> live = new HashSet<State>(findAcceptStates());
			LinkedList<State> worklist = new LinkedList<State>(live);
			while (!worklist.isEmpty()) {
				State current = worklist.removeFirst();
				for (State previous : map.get(current)) {
					if (!live.contains(previous)) {
						live.add(previous);
						worklist.add(previous);
					}
				}
			}
			return live;
		}

	}

	static abstract class Transition implements Cloneable {

		private State target;

		public Transition(State target) {
			this.target = target;
		}

		public State getTarget() {
			return target;
		}

		public abstract char getFrom();

		public abstract char getTo();

		public abstract boolean matches(char ch);

		@Override
		public Transition clone() {
			try {
				return (Transition) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		public Transition clone(State target) {
			Transition clone = clone();
			clone.target = target;
			return clone;
		}

		public abstract com.almondtools.rexlex.automaton.GenericAutomaton.Transition toNFA(com.almondtools.rexlex.automaton.GenericAutomaton.State target);

	}

	static class RangeTransition extends Transition {

		private char from;
		private char to;

		public RangeTransition(char from, char to, State e) {
			super(e);
			this.from = from;
			this.to = to;
		}

		@Override
		public char getFrom() {
			return from;
		}

		@Override
		public char getTo() {
			return to;
		}

		@Override
		public boolean matches(char ch) {
			return ch >= from && ch <= to;
		}

		@Override
		public String toString() {
			return new StringBuilder(" -<").append(from).append("..").append(to).append(">-> ").append(getTarget().getId()).toString();
		}

		@Override
		public com.almondtools.rexlex.automaton.GenericAutomaton.Transition toNFA(com.almondtools.rexlex.automaton.GenericAutomaton.State target) {
			return new com.almondtools.rexlex.automaton.GenericAutomaton.RangeTransition(from, to, target);
		}
	}

	static class ExactTransition extends Transition {

		private char value;

		public ExactTransition(char value, State e) {
			super(e);
			this.value = value;
		}

		public char getValue() {
			return value;
		}

		@Override
		public char getFrom() {
			return value;
		}

		@Override
		public char getTo() {
			return value;
		}

		@Override
		public boolean matches(char ch) {
			return value == ch;
		}

		@Override
		public String toString() {
			return new StringBuilder(" -<").append(value).append(">-> ").append(getTarget().getId()).toString();
		}

		@Override
		public com.almondtools.rexlex.automaton.GenericAutomaton.Transition toNFA(com.almondtools.rexlex.automaton.GenericAutomaton.State target) {
			return new com.almondtools.rexlex.automaton.GenericAutomaton.ExactTransition(value, target);
		}
	}

	static class FindAcceptStates implements StateVisitor<Set<State>> {

		private Set<State> visited;
		private Set<State> accepted;

		public FindAcceptStates() {
			this.visited = new HashSet<State>();
			this.accepted = new LinkedHashSet<State>();
		}

		@Override
		public Set<State> visitState(State state) {
			if (state.accept()) {
				accepted.add(state);
			}
			visited.add(state);
			Set<State> statesToVisit = Sets.hashed(state.getDirectlyReachableStates()).minus(visited).build();
			for (State stateToVisit : statesToVisit) {
				accepted.addAll(stateToVisit.apply(this));
			}
			return accepted;
		}

	}

	static class FindStates implements StateVisitor<Set<State>> {

		private Set<State> visited;
		private Set<State> accepted;

		public FindStates() {
			this.visited = new HashSet<State>();
			this.accepted = new LinkedHashSet<State>();
		}

		@Override
		public Set<State> visitState(State state) {
			accepted.add(state);
			visited.add(state);
			Set<State> statesToVisit = Sets.hashed(state.getDirectlyReachableStates()).minus(visited).build();
			for (State stateToVisit : statesToVisit) {
				accepted.addAll(stateToVisit.apply(this));
			}
			return accepted;
		}

	}

	class Matcher implements AutomatonMatcher {

		private AutomatonMatcherListener listener;

		private CharProvider chars;
		private long matchStart;

		private State start;
		private State state;

		public Matcher() {
			this.listener = new BaseListener();
			this.start = DeterministicAutomaton.this.start;
		}

		@Override
		public Matcher withListener(AutomatonMatcherListener listener) {
			this.listener = listener;
			return this;
		}

		@Override
		public AutomatonMatcherListener applyTo(CharProvider chars) {
			this.chars = chars;
			resume(chars, this.start, chars.current());
			return listener;
		}

		@Override
		public boolean isSuspended() {
			return start != null && chars != null;
		}

		@Override
		public AutomatonMatcherListener resume() {
			resume(chars, state, matchStart);
			return listener;
		}

		private void resume(CharProvider chars, State state, long matchStart) {
			if (state == null) {
				return;
			}
			while (true) {
				if (state == error) {
					boolean suspend = listener.recoverMismatch(chars, matchStart);
					state = start;
					matchStart = chars.current();
					if (suspend) {
						this.matchStart = matchStart;
						this.state = state;
						return;
					}
					if (chars.finished()) {
						this.state = null;
						return;
					}
				} else {
					if (state.accept()) {
						chars.mark();
						boolean suspend = listener.reportMatch(chars, matchStart, state.getType());
						if (chars.changed()) {
							state = start;
						}
						if (chars.finished()) {
							this.state = null;
							return;
						}
						state = state.next(chars.next());
						if (suspend) {
							this.matchStart = matchStart;
							this.state = state;
							return;
						}
					} else if (chars.finished()) {
						boolean suspend = listener.recoverMismatch(chars, matchStart);
						state = start;
						matchStart = chars.current();
						if (suspend) {
							this.matchStart = matchStart;
							this.state = state;
							return;
						}
						if (chars.finished()) {
							this.state = null;
							return;
						}
					} else {
						state = state.next(chars.next());
					}
				}
			}
		}
	}

	private class SampleIterator implements Iterator<String> {

		private int limit;
		private Deque<SampleItem> stack;
		private List<String> buffer;

		public SampleIterator(int limit) {
			this.limit = limit;
			this.stack = new ArrayDeque<SampleItem>();
			this.buffer = new LinkedList<String>();
			stack.add(new SampleItem(start));
			nextStack();
		}

		@Override
		public boolean hasNext() {
			if (limit <= 0) {
				return false;
			}
			if (buffer.isEmpty()) {
				fillBuffer();
			}
			return !buffer.isEmpty();
		}

		@Override
		public String next() {
			if (limit <= 0) {
				throw new NoSuchElementException();
			}
			if (buffer.isEmpty()) {
				fillBuffer();
			}
			if (buffer.isEmpty()) {
				throw new NoSuchElementException();
			}
			limit--;
			return buffer.remove(0);
		}

		private void fillBuffer() {
			buffer = new ArrayList<String>();
			if (stack.isEmpty()) {
				return;
			}
			buffer.add("");
			Iterator<SampleItem> i = stack.descendingIterator();
			while (i.hasNext()) {
				SampleItem item = i.next();
				char[] events = item.getEvents();
				if (events.length != 0) {
					List<String> newbuffer = new ArrayList<String>();
					events: for (char event : events) {
						for (String old : buffer) {
							newbuffer.add(old + event);
							if (newbuffer.size() >= limit) {
								break events;
							}
						}
					}
					buffer = new LinkedList<String>(newbuffer);
				}
			}
			nextStack();
		}

		private void nextStack() {
			while (!stack.isEmpty()) {
				SampleItem peek = stack.peek();
				if (peek.accept()) {
					return;
				} else if (peek.nextDecision() != -1) {
					State state = peek.getTarget();
					stack.push(new SampleItem(state));
				} else {
					stack.pop();
				}
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private class SampleItem {

		private State state;
		private int decision;

		public SampleItem(State state) {
			this.state = state;
			if (state.accept()) {
				this.decision = -2;
			} else {
				this.decision = -1;
			}
		}

		public char[] getEvents() {
			if (decision < 0) {
				return new char[0];
			}
			char from = getTransition().getFrom();
			char to = getTransition().getTo();
			char[] events = new char[to - from + 1];
			for (char c = from; c <= to; c++) {
				events[c - from] = c;
			}
			return events;
		}

		public State getTarget() {
			return getTransition().getTarget();
		}

		private Transition getTransition() {
			return state.getTransitions().get(decision);
		}

		private int getSize() {
			return state.getTransitions().size();
		}

		public boolean accept() {
			boolean accept = decision == -2;
			if (accept) {
				decision++;
			}
			return accept;
		}

		public int nextDecision() {
			do {
				decision++;
			} while (decision < getSize() && getTarget().error());
			if (decision == getSize()) {
				decision = -1;
			}
			return decision;
		}

	}

}
