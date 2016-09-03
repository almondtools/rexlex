package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.automaton.AutomatonProperty.ACYCLIC;
import static com.almondtools.rexlex.automaton.AutomatonProperty.CYCLIC;
import static com.almondtools.rexlex.automaton.AutomatonProperty.LINEAR;
import static com.almondtools.rexlex.automaton.AutomatonProperty.UNKNOWN;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.almondtools.rexlex.Token;
import com.almondtools.rexlex.TokenFactory;
import com.almondtools.rexlex.TokenType;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.State;
import com.almondtools.rexlex.automaton.FromGenericAutomaton.ToTabledAutomaton;
import com.almondtools.rexlex.automaton.FromTabledAutomaton.ToGenericAutomaton;
import com.almondtools.rexlex.io.CharClassProvider;
import com.almondtools.rexlex.io.MappedCharClassProvider;
import com.almondtools.rexlex.pattern.DefaultTokenType;
import com.almondtools.rexlex.pattern.TokenIterator;
import net.amygdalum.stringsearchalgorithms.io.CharProvider;
import net.amygdalum.stringsearchalgorithms.io.StringCharProvider;

public class TabledAutomaton implements Automaton {

	static final int START = 0;
	static final int ERROR = 1;

	private int startState;
	private CharClassMapper charClassMapper;
	private TokenType[] accept; // #states
	private int[] transitions; // #states x #character classes (0 = start, 1 = error)

	private AutomatonProperty property;
	private int charClassCount;

	public TabledAutomaton(char[] relevantChars, State start, State error) {
		this(relevantChars, start, error, UNKNOWN);
	}
	
	public TabledAutomaton(char[] relevantChars, State start, State error, AutomatonProperty property) {
		this.startState = computeStartState(start, error);
		this.charClassMapper = new UnicodeCharClassMapper(relevantChars);
		this.property = property;
		this.charClassCount = relevantChars.length;
		initTables(start, error);
	}

	private int computeStartState(State start, State error) {
		if (start == error) {
			return ERROR;
		} else {
			return START;
		}
	}

	int getStartState() {
		return startState;
	}

	public int getErrorState() {
		return ERROR;
	}

	int[] getTransitions() {
		return transitions;
	}

	int getTarget(int state, int i) {
		return transitions[state * charClassCount + i];
	}

	public TokenType[] getAccept() {
		return accept;
	}

	public CharClassMapper getCharClassMapper() {
		return charClassMapper;
	}
	
	public int getStateCount() {
		return accept.length;
	}
	
	public int getCharClassCount() {
		return charClassCount;
	}

	private void initTables(State start, State error) {
		Set<State> states = start.findReachableStates();
		states.add(error); // does only change if error is not reachable
		Map<State, Integer> statesToIndex = new IdentityHashMap<State, Integer>();
		int statesCount = states.size();
		accept = new TokenType[statesCount];
		transitions = new int[statesCount * charClassCount];

		states.remove(start);
		states.remove(error);

		if (start == error) {
			createRowIndex(statesToIndex, error, START);
			initRow(statesToIndex, error);
		} else {
			createRowIndex(statesToIndex, start, START);
			createRowIndex(statesToIndex, error, ERROR);

			initRow(statesToIndex, start);
			initRow(statesToIndex, error);
		}

		for (State state : states) {
			initRow(statesToIndex, state);
		}
	}

	private void initRow(Map<State, Integer> statesToIndex, State state) {
		Integer rowIndex = fetchRowIndex(statesToIndex, state);
		int from = rowIndex * charClassCount;
		int to = from + charClassCount;
		for (int i = from; i < to; i++) {
			char representative = charClassMapper.representative(i-from);
			State next = state.next(representative);
			if (next != null) {
				Integer nextIndex = fetchRowIndex(statesToIndex, next);
				transitions[i] = nextIndex;
			} else {
				transitions[i] = ERROR;
			}
		}
	}

	private Integer fetchRowIndex(Map<State, Integer> statesToIndex, State state) {
		Integer rowIndex = statesToIndex.get(state);
		if (rowIndex == null) {
			rowIndex = statesToIndex.size();
			createRowIndex(statesToIndex, state, rowIndex);
		}
		return rowIndex;
	}

	void createRowIndex(Map<State, Integer> statesToIndex, State state, Integer rowIndex) {
		statesToIndex.put(state, rowIndex);
		accept[rowIndex] = state.getType();
	}

	@Override
	public String getId() {
		return String.valueOf(startState);
	}

	@Override
	public TokenType getErrorType() {
		if (accept.length > ERROR) {
			return accept[ERROR] == null ? DefaultTokenType.ERROR : accept[ERROR];
		} else {
			return DefaultTokenType.ERROR;
		}
	}

	@Override
	public AutomatonProperty getProperty() {
		if (property.isUnknown()) {
			property = computeProperties();
		}
		return property;
	}

	private AutomatonProperty computeProperties() {
		Set<Integer> visited = new HashSet<Integer>();
		List<Integer> nexts = new LinkedList<Integer>();
		nexts.add(startState);
		AutomatonProperty property = LINEAR;
		loop: while (!nexts.isEmpty()) {
			int next = nexts.remove(0);
			visited.add(next);
			int from = next * charClassCount;
			int to = from + charClassCount;
			int validTargets = 0;
			for (int i = from; i < to; i++) {
				int target = transitions[i];
				if (target != ERROR) {
					validTargets++;
					if (validTargets > 1) {
						property = ACYCLIC; // not linear
					}
					if (visited.contains(target)) {
						if (findPath(target, next) != null) {
							property = CYCLIC; // not acyclic
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

	public String findPathTo(int state) {
		List<Integer> path = findPath(startState, state);
		return charClassMapper.representatives(path);
	}

	private List<Integer> findPath(int from, int to) {
		Map<Integer, List<Integer>> paths = new HashMap<Integer, List<Integer>>();
		paths.put(from, new ArrayList<Integer>(0));
		List<Integer> nexts = new LinkedList<Integer>();
		nexts.add(from);
		while (!nexts.isEmpty()) {
			int next = nexts.remove(0);
			if (next == to) {
				return paths.get(next);
			}
			int ifrom = next * charClassCount;
			int ito = ifrom + charClassCount;
			for (int i = ifrom; i < ito; i++) {
				int target = transitions[i];
				if (!paths.containsKey(target)) {
					List<Integer> base = paths.get(next);
					List<Integer> path = new ArrayList<Integer>(base.size() + 1);
					path.addAll(base);
					path.add(i - ifrom);
					paths.put(target, path);
					nexts.add(target);
				}
			}
		}
		return null;
	}

	public int findState(String path) {
		int state = startState;
		CharClassProvider charClasses = new MappedCharClassProvider(new StringCharProvider(path, 0), charClassMapper);

		while (!charClasses.finished()) {
			state = next(state, charClasses.next());
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

	public AutomatonMatcher matcher(int state) {
		return new Matcher(state);
	}

	private int next(int current, int charClass) {
		return transitions[current * charClassCount + charClass];
	}

	public int next(int current, char ch) {
		int charClass = charClassMapper.getIndex(ch);
		return transitions[current * charClassCount + charClass];
	}

	public TokenType getType(int state) {
		return accept[state];
	}

	@Override
	public <T extends Token> Iterator<T> tokenize(CharProvider chars, TokenFactory<T> factory) {
		return new TokenIterator<T>(this, chars, factory);
	}

	public <T extends Automaton> T toAutomaton(ToAutomaton<TabledAutomaton, T> builder) {
		return builder.transform(this);
	}

	@Override
	public TabledAutomaton revert() {
		return toAutomaton(new ToGenericAutomaton()).revert().toAutomaton(new ToTabledAutomaton(accept[ERROR]));
	}

	@Override
	public TabledAutomatonExport store(String name) {
		return new TabledAutomatonExport(this, name);
	}

	class Matcher implements AutomatonMatcher {

		private AutomatonMatcherListener listener;

		private CharProvider chars;
		private long matchStart;

		private int start;
		private int state;

		public Matcher() {
			this.listener = new BaseListener();
			this.start = startState;
			this.state = -1;
		}

		public Matcher(int state) {
			this.listener = new BaseListener();
			this.start = state;
			this.state = -1;
		}

		@Override
		public Matcher withListener(AutomatonMatcherListener listener) {
			this.listener = listener;
			return this;
		}

		@Override
		public AutomatonMatcherListener applyTo(CharProvider chars) {
			this.chars = chars;
			resume(chars, start, chars.current());
			return listener;
		}

		@Override
		public boolean isSuspended() {
			return state != -1 && chars != null;
		}

		@Override
		public AutomatonMatcherListener resume() {
			resume(chars, state, matchStart);
			return listener;
		}

		private void resume(CharProvider chars, int state, long matchStart) {
			if (state == -1) {
				return;
			}
			while (true) {
				if (state == ERROR) {
					boolean suspend = listener.recoverMismatch(chars, matchStart);
					state = startState;
					matchStart = chars.current();
					if (suspend) {
						this.matchStart = matchStart;
						this.state = state;
						return;
					}
					if (chars.finished()) {
						this.state = -1;
						return;
					}
				} else {
					TokenType accept = getType(state);
					if (accept != null && accept.accept()) {
						chars.mark();
						boolean suspend = listener.reportMatch(chars, matchStart, accept);
						if (chars.changed()) {
							state = start;
						}
						if (chars.finished()) {
							this.state = -1;
							return;
						}
						state = next(state, chars.next());
						if (suspend) {
							this.matchStart = matchStart;
							this.state = state;
							return;
						}
					} else if (chars.finished()) {
						boolean suspend = listener.recoverMismatch(chars, matchStart);
						state = startState;
						matchStart = chars.current();
						if (suspend) {
							this.matchStart = matchStart;
							this.state = state;
							return;
						}
						if (chars.finished()) {
							this.state = -1;
							return;
						}
					} else {
						state = next(state, chars.next());
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
			stack.add(new SampleItem(startState));
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
					List<String> newbuffer = new ArrayList<String>(limit);
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
					int state = peek.getTarget();
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

		private int state;
		private int decision;

		public SampleItem(int state) {
			this.state = state;
			if (accept[state] != null && accept[state].accept()) {
				this.decision = -2;
			} else {
				this.decision = -1;
			}
		}

		public char[] getEvents() {
			if (decision < 0) {
				return new char[0];
			}
			return charClassMapper.mapped(decision);
		}

		public int getTarget() {
			return transitions[state * charClassCount + decision];
		}

		public boolean accept() {
			boolean accept = decision == -2;
			if (accept) {
				decision++;
			}
			return accept;
		}

		public int nextDecision() {
			int base = state * charClassCount;
			while (true) {
				decision++;
				if (decision == charClassCount) {
					decision = -1;
					return decision;
				}
				TokenType token = accept[transitions[base + decision]];
				if (token == null || !token.error()) {
					return decision;
				}
			}
		}
	}

}
