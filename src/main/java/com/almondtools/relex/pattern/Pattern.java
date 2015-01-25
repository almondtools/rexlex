package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.Pattern.ProCharNode.toCharNodes;
import static com.almondtools.relex.pattern.PatternOptionUtil.list;
import static com.almondtools.relex.pattern.PatternOptionUtil.splitFirst;
import static com.almondtools.relex.pattern.PatternOptionUtil.splitOf;
import static java.util.EnumSet.copyOf;
import static java.util.EnumSet.noneOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.almondtools.relex.automaton.Automaton;
import com.almondtools.relex.automaton.AutomatonBuilder;
import com.almondtools.relex.automaton.GenericAutomaton;
import com.almondtools.relex.automaton.GenericAutomatonBuilder;
import com.almondtools.relex.automaton.ToAutomaton;
import com.almondtools.util.collections.HashMaps;
import com.almondtools.util.collections.HashSets;
import com.almondtools.util.text.StringUtils;

public class Pattern {

	private static final char OR = '|';
	private static final char AND = '&';
	private static final char INV = '~';
	private static final char OBRK = '[';
	private static final char NOT = '^';
	private static final char DASH = '-';
	private static final char CBRK = ']';
	private static final char OBRC = '{';
	private static final char COMMA = ',';
	private static final char CBRC = '}';
	private static final char OPT = '?';
	private static final char STAR = '*';
	private static final char PLUS = '+';
	private static final char OPAR = '(';
	private static final char CPAR = ')';
	private static final char DOT = '.';
	private static final char ESCAPE = '\\';

	private static final Set<Character> OPERATORS = HashSets.of(
		OR, AND,
		OBRK, DASH, CBRK,
		OBRC, COMMA, CBRC,
		OPT, STAR, PLUS,
		OPAR, CPAR,
		DOT, ESCAPE
		);
	private static final Map<Character, Character> ESCAPES = HashMaps.<Character, Character> hashed()
		.put('t', '\t')
		.put('b', '\b')
		.put('n', '\n')
		.put('r', '\r')
		.put('f', '\f')
		.put('\\', '\\')
		.build();
	private static final Map<Character, Character> ESCAPES_REVERSE = HashMaps.<Character, Character> invert(ESCAPES).build();
	private static final Comparator<CharNode> CHAR_NODE_COMPARATOR = createCharNodeComparator();
	private static final Map<Character, SpecialCharClassNode> CHAR_GROUPS = HashMaps.<Character, SpecialCharClassNode> hashed()
		.put('s', createWhiteSpaceEscapes())
		.put('S', createWhiteSpaceEscapes().invert())
		.put('w', createAlphaNumericEscapes())
		.put('W', createAlphaNumericEscapes().invert())
		.put('d', createDigitEscapes())
		.put('D', createDigitEscapes().invert())
		.build();

	private static final GenericAutomatonBuilder DEFAULT_AUTOMATON_BUILDER = new GenericAutomatonBuilder();
	private static final DefaultMatcherBuilder DEFAULT_MATCHER_BUILDER = new DefaultMatcherBuilder();

	private String pattern;
	private MatcherBuilder builder;

	private Pattern(String pattern, MatcherBuilder builder) {
		this.pattern = pattern;
		this.builder = builder;
	}

	public String pattern() {
		return pattern;
	}

	public static GenericAutomaton compileGenericAutomaton(String pattern, List<PatternOption> options) {
		List<PatternFlag> patternFlags = splitOf(options, PatternFlag.class);
		PatternNode node = new PatternParser(pattern, patternFlags).parse();
		RemainderTokenType tokenType = splitFirst(options, RemainderTokenType.class);
		if (tokenType == null) {
			return automatonBuilder(options).buildFrom(node);
		} else {
			return automatonBuilder(options).buildFrom(node, tokenType.getRemainder());
		}
	}

	public static GenericAutomaton compileGenericAutomaton(String pattern, PatternOption... options) {
		return compileGenericAutomaton(pattern, list(options));
	}

	public static <T extends Automaton> T compileAutomaton(String pattern, ToAutomaton<GenericAutomaton, T> transformer, List<PatternOption> list) {
		GenericAutomaton genericAutomaton = compileGenericAutomaton(pattern, list);
		return transformer.transform(genericAutomaton);
	}

	public static <T extends Automaton> T compileAutomaton(String pattern, ToAutomaton<GenericAutomaton, T> transformer, PatternOption... options) {
		return compileAutomaton(pattern, transformer, list(options));
	}

	public static Pattern compile(String pattern, List<PatternOption> options) {
		GenericAutomaton genericAutomaton = compileGenericAutomaton(pattern, options);
		return new Pattern(pattern, matcherBuilder(options).initWith(genericAutomaton));
	}

	public static Pattern compile(String pattern, PatternOption... options) {
		return compile(pattern, list(options));
	}

	private static AutomatonBuilder automatonBuilder(List<PatternOption> options) {
		AutomatonBuilder builder = splitFirst(options, AutomatonBuilder.class);
		if (builder == null) {
			return DEFAULT_AUTOMATON_BUILDER;
		}
		return builder;
	}

	private static MatcherBuilder matcherBuilder(List<PatternOption> options) {
		MatcherBuilder builder = splitFirst(options, MatcherBuilder.class);
		if (builder == null) {
			return DEFAULT_MATCHER_BUILDER;
		}
		return builder;
	}

	private static List<CharNode> computeComplement(List<CharNode> nodes) {
		Collections.sort(nodes, CHAR_NODE_COMPARATOR);
		List<CharNode> remainderNodes = new LinkedList<CharNode>();
		char current = Character.MIN_VALUE;
		for (CharNode node : nodes) {
			char from = node.getFrom();
			char to = node.getTo();
			if (current + 1 == from) {
				remainderNodes.add(new SingleCharNode(current));
			} else if (current < from) {
				remainderNodes.add(new RangeCharNode(current, (char) (from - 1)));
			}
			current = (char) (to + 1);
		}
		if (current == Character.MAX_VALUE) {
			remainderNodes.add(new SingleCharNode(current));
		} else if (current == (char) (Character.MAX_VALUE + 1)) {
			// overflow from previous loop => do nothing
		} else if (current < Character.MAX_VALUE) {
			remainderNodes.add(new RangeCharNode(current, Character.MAX_VALUE));
		}
		return remainderNodes;
	}

	private static String escapeOperators(char ch) {
		if (OPERATORS.contains(ch)) {
			return new StringBuilder().append(ESCAPE).append(ch).toString();
		} else if (ESCAPES_REVERSE.containsKey(ch)) {
			return new StringBuilder().append(ESCAPE).append(ESCAPES_REVERSE.get(ch)).toString();
		} else {
			return String.valueOf(ch);
		}
	}

	public Finder finder(String input) {
		return builder.buildFinder(input);
	}

	public Matcher matcher(String input) {
		return builder.buildMatcher(input);
	}

	private static Comparator<CharNode> createCharNodeComparator() {
		return new Comparator<CharNode>() {

			@Override
			public int compare(CharNode o1, CharNode o2) {
				int result = o1.getFrom() - o2.getFrom();
				if (result == 0) {
					result = o1.getTo() - o2.getTo();
				}
				return result;
			}
		};
	}

	private static SpecialCharClassNode createWhiteSpaceEscapes() {
		return new SpecialCharClassNode('s', Arrays.<CharNode> asList(
			new SingleCharNode(' '),
			new SingleCharNode('\t'),
			new SingleCharNode('\r'),
			new SingleCharNode('\n')
			));
	}

	private static SpecialCharClassNode createAlphaNumericEscapes() {
		return new SpecialCharClassNode('w', Arrays.<CharNode> asList(
			new RangeCharNode('0', '9'),
			new RangeCharNode('a', 'z'),
			new RangeCharNode('A', 'Z')
			));
	}

	private static SpecialCharClassNode createDigitEscapes() {
		return new SpecialCharClassNode('d', Arrays.<CharNode> asList(
			new RangeCharNode('0', '9')
			));
	}

	static class PatternParser {

		private String pattern;
		private int pos;
		private Set<PatternFlag> options;

		public PatternParser(String pattern) {
			this.pattern = pattern;
			this.pos = 0;
			this.options = noneOf(PatternFlag.class);
		}

		public PatternParser(String pattern, List<PatternFlag> options) {
			this.pattern = pattern;
			this.pos = 0;
			this.options = options.isEmpty() ? noneOf(PatternFlag.class) : copyOf(options);
		}

		public PatternParser(String pattern, Set<PatternFlag> options) {
			this.pattern = pattern;
			this.pos = 0;
			this.options = options;
		}

		public PatternNode parse() {
			if (pattern == null) {
				return null;
			} else if (pattern.isEmpty()) {
				return new EmptyNode();
			} else {
				return parseAlternatives();
			}
		}

		private char next() {
			char ch = pattern.charAt(pos);
			pos++;
			return ch;
		}

		private boolean match(char c) {
			if (done()) {
				return false;
			} else if (pattern.charAt(pos) == c) {
				pos++;
				return true;
			} else {
				return false;
			}
		}

		private boolean lookahead(String chars) {
			if (done()) {
				return false;
			} else {
				return chars.indexOf(pattern.charAt(pos)) >= 0;
			}
		}

		private boolean done() {
			return pos >= pattern.length();
		}

		private boolean lookaheadIsDigit() {
			return lookahead("0123456789");
		}

		private boolean lookaheadIsBreakConcat() {
			return lookahead("|&)") || done();
		}

		private boolean lookaheadIsCloseCharClass() {
			return lookahead("]") || done();
		}

		private boolean lookaheadIsOpenLoop() {
			return lookahead("?*+{");
		}

		private PatternNode parseAlternatives() {
			PatternNode node = parseConjunctive();
			while (match(OR)) {
				node = AlternativesNode.join(node, parseConjunctive());
			}
			return node;
		}

		private PatternNode parseConjunctive() {
			PatternNode node = parseConcatenation();
			while (match(AND)) {
				node = ConjunctiveNode.join(node, parseConcatenation());
			}
			return node;
		}

		private PatternNode parseConcatenation() {
			PatternNode node = parseLoop();
			while (!lookaheadIsBreakConcat()) {
				node = ConcatNode.join(node, parseLoop());
			}
			return node;
		}

		private PatternNode parseLoop() {
			PatternNode node = parseCharOrComplement();
			while (lookaheadIsOpenLoop()) {
				if (match(OPT)) {
					node = new OptionalNode(node);
				} else if (match(STAR)) {
					node = new LoopNode(node, 0, LoopNode.INFINITY);
				} else if (match(PLUS)) {
					node = new LoopNode(node, 1, LoopNode.INFINITY);
				} else if (match(OBRC)) {
					int from = parseInt();
					int to = from;
					if (match(COMMA)) {
						to = parseInt();
					}
					if (!match(CBRC)) {
						throw new PatternCompileException(pattern, pos, "}");
					}
					node = new LoopNode(node, from, to);
				}
			}
			return node;
		}

		private PatternNode parseCharOrComplement() {
			boolean complement = match(INV);
			PatternNode node = parseCharOrCharClass();
			if (complement) {
				return new ComplementNode(node);
			} else {
				return node;
			}
		}

		private PatternNode parseCharOrCharClass() {
			if (match(OBRK)) {
				boolean complement = match(NOT);
				List<ProCharNode> subNodes = new LinkedList<ProCharNode>();
				subNodes.add(parseCharOrRange());
				while (!lookaheadIsCloseCharClass()) {
					subNodes.add(parseCharOrRange());
				}
				if (!match(CBRK)) {
					throw new PatternCompileException(pattern, pos, "]");
				}
				if (complement) {
					return new CompClassNode(toCharNodes(subNodes));
				} else {
					return new CharClassNode(toCharNodes(subNodes));
				}
			} else if (match(ESCAPE)) {
				return parseEscapedChar();
			} else {
				return parseLeaf();
			}
		}

		private ProCharNode parseCharOrRange() {
			if (match(ESCAPE)) {
				return parseEscapedChar();
			} else {
				char ch = parseChar();
				if (match(DASH)) {
					if (lookaheadIsCloseCharClass()) {
						return new CharClassNode(Arrays.asList((CharNode) new SingleCharNode(ch), (CharNode) new SingleCharNode(DASH)));
					} else {
						char from = ch;
						char to = parseChar();
						return new RangeCharNode(from, to);
					}
				} else {
					return new SingleCharNode(ch);
				}
			}
		}

		private ProCharNode parseEscapedChar() {
			char ch = next();
			if (CHAR_GROUPS.containsKey(ch)) {
				return CHAR_GROUPS.get(ch);
			} else if (ESCAPES.containsKey(ch)) {
				return new SingleCharNode(ESCAPES.get(ch));
			} else {
				return new SingleCharNode(ch);
			}
		}

		private PatternNode parseLeaf() {
			if (match(DOT)) {
				return new AnyCharNode(options.contains(PatternFlag.DOTALL));
			} else if (match(OPAR)) {
				PatternNode node = parseAlternatives();
				if (!match(CPAR)) {
					throw new PatternCompileException(pattern, pos, ")");
				}
				return new GroupNode(node);
			} else {
				return new SingleCharNode(parseChar());
			}
		}

		private char parseChar() {
			if (done()) {
				throw new PatternCompileException(pattern, pos, ".");
			} else {
				return next();
			}
		}

		private int parseInt() {
			int start = pos;
			while (lookaheadIsDigit()) {
				next();
			}
			if (start != pos) {
				return Integer.parseInt(pattern.substring(start, pos));
			} else {
				return LoopNode.INFINITY;
			}
		}
	}

	public static interface PatternNodeVisitor<T> {

		T visitAlternative(AlternativesNode node);

		T visitConjunctive(ConjunctiveNode node);

		T visitConcat(ConcatNode node);

		T visitLoop(LoopNode node);

		T visitOptional(OptionalNode node);

		T visitComplement(ComplementNode node);

		T visitProChar(ProCharNode node);

		T visitRangeChar(RangeCharNode node);

		T visitSingleChar(SingleCharNode node);

		T visitString(StringNode node);

		T visitEmpty(EmptyNode node);

		T visitGroup(GroupNode node);

	}

	public static interface PatternNode extends Cloneable {

		<T> T apply(PatternNodeVisitor<T> visitor);

		public PatternNode clone();
	}

	public static interface JoinableNode extends PatternNode {

		String getLiteralValue();

	}

	public static interface DelegatorNode extends PatternNode {

		PatternNode getSubNode();

		void replaceSubNode(PatternNode node, PatternNode by);

	}

	public static interface BranchNode extends PatternNode {

		List<PatternNode> getSubNodes();

		void replaceSubNode(PatternNode node, PatternNode by);

	}

	public static interface LeafNode extends PatternNode {

	}

	public static class AlternativesNode implements BranchNode {

		private List<PatternNode> subNodes;

		public AlternativesNode(List<? extends PatternNode> subNodes) {
			this.subNodes = new ArrayList<PatternNode>(subNodes);
		}

		@Override
		public List<PatternNode> getSubNodes() {
			return subNodes;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			int i = subNodes.indexOf(node);
			if (i >= 0) {
				subNodes.remove(i);
				if (by instanceof AlternativesNode) {
					for (PatternNode sub : ((AlternativesNode) by).getSubNodes()) {
						subNodes.add(i, sub);
						i++;
					}
				} else {
					subNodes.add(i, by);
				}
			}
		}

		public static AlternativesNode join(PatternNode node1, PatternNode node2) {
			List<PatternNode> subNodes = new LinkedList<PatternNode>();
			if (node1 instanceof AlternativesNode) {
				subNodes.addAll(((AlternativesNode) node1).getSubNodes());
			} else {
				subNodes.add(node1);
			}
			if (node2 instanceof AlternativesNode) {
				subNodes.addAll(((AlternativesNode) node2).getSubNodes());
			} else {
				subNodes.add(node2);
			}
			return new AlternativesNode(subNodes);
		}

		@Override
		public String toString() {
			return StringUtils.join(subNodes, '|');
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitAlternative(this);
		}

		@Override
		public AlternativesNode clone() {
			try {
				AlternativesNode clone = (AlternativesNode) super.clone();
				clone.subNodes = new ArrayList<PatternNode>(subNodes.size());
				for (PatternNode subNode : subNodes) {
					clone.subNodes.add(subNode.clone());
				}
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class ConjunctiveNode implements BranchNode {

		private List<PatternNode> subNodes;

		public ConjunctiveNode(List<? extends PatternNode> subNodes) {
			this.subNodes = new ArrayList<PatternNode>(subNodes);
		}

		@Override
		public List<PatternNode> getSubNodes() {
			return subNodes;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			int i = subNodes.indexOf(node);
			if (i >= 0) {
				subNodes.set(i, by);
			}
		}

		public static ConjunctiveNode join(PatternNode node1, PatternNode node2) {
			List<PatternNode> subNodes = new LinkedList<PatternNode>();
			if (node1 instanceof ConjunctiveNode) {
				subNodes.addAll(((ConjunctiveNode) node1).getSubNodes());
			} else {
				subNodes.add(node1);
			}
			if (node2 instanceof ConjunctiveNode) {
				subNodes.addAll(((ConjunctiveNode) node2).getSubNodes());
			} else {
				subNodes.add(node2);
			}
			return new ConjunctiveNode(subNodes);
		}

		@Override
		public String toString() {
			return StringUtils.join(subNodes, '&');
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitConjunctive(this);
		}

		@Override
		public ConjunctiveNode clone() {
			try {
				ConjunctiveNode clone = (ConjunctiveNode) super.clone();
				clone.subNodes = new ArrayList<PatternNode>(subNodes.size());
				for (PatternNode subNode : subNodes) {
					clone.subNodes.add(subNode.clone());
				}
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class ConcatNode implements BranchNode {

		private List<PatternNode> subNodes;

		public ConcatNode(List<? extends PatternNode> subNodes) {
			this.subNodes = new ArrayList<PatternNode>(subNodes);
		}

		@Override
		public List<PatternNode> getSubNodes() {
			return subNodes;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			int i = subNodes.indexOf(node);
			if (i >= 0) {
				subNodes.set(i, by);
			}
		}

		public static PatternNode join(PatternNode node1, PatternNode node2) {
			List<PatternNode> subNodes = new LinkedList<PatternNode>();
			if (node1 instanceof ConcatNode) {
				subNodes.addAll(((ConcatNode) node1).getSubNodes());
			} else {
				subNodes.add(node1);
			}
			if (node2 instanceof ConcatNode) {
				subNodes.addAll(((ConcatNode) node2).getSubNodes());
			} else {
				subNodes.add(node2);
			}
			subNodes = joinIfPossible(subNodes);
			if (subNodes.size() == 1) {
				return subNodes.get(0);
			} else {
				return new ConcatNode(subNodes);
			}
		}

		private static List<PatternNode> joinIfPossible(List<PatternNode> nodes) {
			LinkedList<PatternNode> joinedNodes = new LinkedList<PatternNode>();
			for (PatternNode node : nodes) {
				PatternNode last = joinedNodes.isEmpty() ? null : joinedNodes.getLast();
				if (last != null && last instanceof JoinableNode && node instanceof JoinableNode) {
					last = joinChars((JoinableNode) last, (JoinableNode) node);
					joinedNodes.removeLast();
					joinedNodes.add(last);
				} else {
					joinedNodes.add(node);
				}
			}
			return joinedNodes;
		}

		private static StringNode joinChars(JoinableNode node1, JoinableNode node2) {
			return new StringNode(new StringBuilder()
				.append(node1.getLiteralValue())
				.append(node2.getLiteralValue())
				.toString());
		}

		@Override
		public String toString() {
			return StringUtils.join(subNodes);
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitConcat(this);
		}

		@Override
		public ConcatNode clone() {
			try {
				ConcatNode clone = (ConcatNode) super.clone();
				clone.subNodes = new ArrayList<PatternNode>(subNodes.size());
				for (PatternNode subNode : subNodes) {
					clone.subNodes.add(subNode.clone());
				}
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class LoopNode implements DelegatorNode {

		public static int INFINITY = Integer.MIN_VALUE;

		private PatternNode subNode;
		int from;
		int to;

		public LoopNode(PatternNode subNode, int from, int to) {
			this.subNode = subNode;
			this.from = from;
			this.to = to;
		}

		public int getFrom() {
			return from;
		}

		public int getTo() {
			return to;
		}

		@Override
		public PatternNode getSubNode() {
			return subNode;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			if (subNode == node) {
				subNode = by;
			}
		}

		@Override
		public String toString() {
			if (from == 0 && to == INFINITY) {
				return new StringBuilder(subNode.toString()).append(STAR).toString();
			} else if (from == 1 && to == INFINITY) {
				return new StringBuilder(subNode.toString()).append(PLUS).toString();
			} else if (from == to) {
				return new StringBuilder(subNode.toString())
					.append(OBRC).append(from).append(CBRC)
					.toString();
			} else {
				return new StringBuilder(subNode.toString())
					.append(OBRC).append(from).append(COMMA).append(to).append(CBRC)
					.toString();
			}
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitLoop(this);
		}

		@Override
		public LoopNode clone() {
			try {
				LoopNode clone = (LoopNode) super.clone();
				clone.subNode = subNode.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class OptionalNode implements DelegatorNode {

		private PatternNode subNode;

		public OptionalNode(PatternNode subNode) {
			this.subNode = subNode;
		}

		@Override
		public PatternNode getSubNode() {
			return subNode;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			if (subNode == node) {
				subNode = by;
			}
		}

		@Override
		public String toString() {
			return new StringBuilder(subNode.toString()).append(OPT).toString();
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitOptional(this);
		}

		@Override
		public OptionalNode clone() {
			try {
				OptionalNode clone = (OptionalNode) super.clone();
				clone.subNode = subNode.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class ComplementNode implements DelegatorNode {

		private PatternNode subNode;

		public ComplementNode(PatternNode subNode) {
			this.subNode = subNode;
		}

		@Override
		public PatternNode getSubNode() {
			return subNode;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			if (subNode == node) {
				subNode = by;
			}
		}

		@Override
		public String toString() {
			return new StringBuilder().append(INV).append(subNode.toString()).toString();
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitComplement(this);
		}

		@Override
		public ComplementNode clone() {
			try {
				ComplementNode clone = (ComplementNode) super.clone();
				clone.subNode = subNode.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static abstract class ProCharNode implements LeafNode {

		public abstract List<CharNode> toCharNodes();

		public static final char before(char c) {
			return (char) (c - 1);
		}

		public static final char after(char c) {
			return (char) (c + 1);
		}

		public static List<CharNode> toCharNodes(List<ProCharNode> proCharNode) {
			List<CharNode> charNodes = new ArrayList<CharNode>();
			for (ProCharNode node : proCharNode) {
				charNodes.addAll(node.toCharNodes());
			}
			return charNodes;
		}

		public ProCharNode union(ProCharNode node) {
			List<CharNode> union = union(toCharNodes(), node.toCharNodes());
			return makeProCharNode(union);
		}

		private List<CharNode> union(List<CharNode> a, List<CharNode> b) {
			int al = a.size();
			int bl = b.size();

			List<CharNode> union = new ArrayList<CharNode>();
			int ai = 0;
			int bi = 0;
			while (true) {
				if (ai >= al && bi >= bl) {
					return compact(union);
				} else if (ai >= al) {
					union.add(b.get(bi));
					bi++;
				} else if (bi >= bl) {
					union.add(a.get(ai));
					ai++;
				} else {
					CharNode aNode = a.get(ai);
					CharNode bNode = b.get(bi);
					int compare = aNode.compareTo(bNode);
					if (compare < 0) {
						union.add(aNode);
						ai++;
					} else if (compare > 0) {
						union.add(bNode);
						bi++;
					} else {
						char aFrom = aNode.getFrom();
						char bFrom = bNode.getFrom();
						char from = aFrom < bFrom ? aFrom : bFrom;
						char aTo = aNode.getTo();
						char bTo = bNode.getTo();
						char to = aTo < bTo ? bTo : aTo;
						if (from == to) {
							union.add(new SingleCharNode(from));
						} else {
							union.add(new RangeCharNode(from, to));
						}
						ai++;
						bi++;
					}
				}
			}
		}

		public ProCharNode intersect(ProCharNode node) {
			List<CharNode> intersection = intersect(toCharNodes(), node.toCharNodes());
			return makeProCharNode(intersection);
		}

		private List<CharNode> intersect(List<CharNode> a, List<CharNode> b) {
			int al = a.size();
			int bl = b.size();

			List<CharNode> intersection = new ArrayList<CharNode>();
			int ai = 0;
			int bi = 0;
			while (true) {
				if (ai < al && bi < bl) {
					CharNode aNode = a.get(ai);
					CharNode bNode = b.get(bi);
					int compare = aNode.compareTo(bNode);
					if (compare < 0) {
						ai++;
					} else if (compare > 0) {
						bi++;
					} else {
						char aFrom = aNode.getFrom();
						char bFrom = bNode.getFrom();
						char from = aFrom < bFrom ? bFrom : aFrom;
						char aTo = aNode.getTo();
						char bTo = bNode.getTo();
						char to = aTo < bTo ? aTo : bTo;
						if (from == to) {
							intersection.add(new SingleCharNode(from));
						} else {
							intersection.add(new RangeCharNode(from, to));
						}
						if (aTo <= bTo) {
							ai++;
						}
						if (bTo <= aTo) {
							bi++;
						}
					}
				} else {
					return compact(intersection);
				}
			}
		}

		public ProCharNode minus(ProCharNode node) {
			List<CharNode> difference = minus(toCharNodes(), node.toCharNodes());
			return makeProCharNode(difference);
		}

		private List<CharNode> minus(List<CharNode> a, List<CharNode> b) {
			int al = a.size();
			int bl = b.size();

			List<CharNode> difference = new ArrayList<CharNode>();
			int ai = 0;
			int bi = 0;
			char last = (char) 0;
			while (true) {
				if (ai >= al) {
					return compact(difference);
				} else if (bi >= bl) {
					CharNode aNode = a.get(ai);
					char from = aNode.getFrom();
					char to = aNode.getTo();
					if (last <= from) {
						difference.add(aNode);
					} else if (last == to) {
						difference.add(new SingleCharNode(last));
					} else {
						difference.add(new RangeCharNode(last, to));
					}
					ai++;
				} else {
					CharNode aNode = a.get(ai);
					CharNode bNode = b.get(bi);
					int compare = aNode.compareTo(bNode);
					if (compare < 0) {
						char from = aNode.getFrom();
						char to = aNode.getTo();
						if (last <= from) {
							difference.add(aNode);
						} else if (last == to) {
							difference.add(new SingleCharNode(last));
						} else {
							difference.add(new RangeCharNode(last, to));
						}
						ai++;
					} else if (compare > 0) {
						bi++;
					} else {
						char aFrom = aNode.getFrom();
						char bFrom = bNode.getFrom();
						char aTo = aNode.getTo();
						char bTo = bNode.getTo();
						if (last > aFrom) {
							aFrom = last;
						}
						if (aFrom < bFrom) {
							char from = aFrom;
							char to = (char) (bFrom - 1);
							if (from == to) {
								difference.add(new SingleCharNode(from));
							} else {
								difference.add(new RangeCharNode(from, to));
							}
						}
						if (aTo > bTo) {
							last = (char) (bTo + 1);
							bi++;
						} else {
							ai++;
						}
					}
				}
			}
		}

		public static ProCharNode makeProCharNode(List<CharNode> nodes) {
			if (nodes.isEmpty()) {
				return null;
			} else if (nodes.size() == 1) {
				return nodes.get(0);
			} else {
				return new CharClassNode(nodes);
			}
		}

		public static List<CharNode> compact(List<CharNode> nodes) {
			List<CharNode> compacted = new ArrayList<CharNode>();
			CharNode last = null;
			for (CharNode node : nodes) {
				if (last == null) {
					last = node;
				} else if (last.getTo() + 1 == node.getFrom()) {
					last = new RangeCharNode(last.getFrom(), node.getTo());
				} else {
					compacted.add(last);
					last = node;
				}
			}
			if (last != null) {
				compacted.add(last);
			}
			return compacted;
		}

		@Override
		public ProCharNode clone() {
			try {
				return (ProCharNode) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class SpecialCharClassNode extends ProCharNode implements LeafNode {

		private char symbol;
		private List<CharNode> charNodes;

		public SpecialCharClassNode(char symbol, List<CharNode> charNodes) {
			this.symbol = symbol;
			this.charNodes = charNodes;
		}

		public char getSymbol() {
			return symbol;
		}

		public SpecialCharClassNode invert() {
			return new SpecialCharClassNode(Character.toUpperCase(symbol), computeComplement(charNodes));
		}

		@Override
		public List<CharNode> toCharNodes() {
			return charNodes;
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitProChar(this);
		}

		@Override
		public String toString() {
			return new StringBuilder().append('\\').append(symbol).toString();
		}

		@Override
		public SpecialCharClassNode clone() {
			SpecialCharClassNode clone = (SpecialCharClassNode) super.clone();
			clone.charNodes = new ArrayList<CharNode>(charNodes.size());
			for (CharNode subNode : charNodes) {
				clone.charNodes.add(subNode.clone());
			}
			return clone;
		}
	}

	public static class CharClassNode extends ProCharNode implements LeafNode {

		private List<CharNode> charNodes;

		public CharClassNode(List<CharNode> charNodes) {
			this.charNodes = charNodes;
		}

		@Override
		public List<CharNode> toCharNodes() {
			return charNodes;
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(OBRK).append(StringUtils.join(charNodes)).append(CBRK)
				.toString();
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitProChar(this);
		}

		@Override
		public CharClassNode clone() {
			CharClassNode clone = (CharClassNode) super.clone();
			clone.charNodes = new ArrayList<CharNode>(charNodes.size());
			for (CharNode subNode : charNodes) {
				clone.charNodes.add(subNode.clone());
			}
			return clone;
		}
	}

	public static class CompClassNode extends ProCharNode implements LeafNode {

		private List<CharNode> charNodes;

		public CompClassNode(List<CharNode> charNodes) {
			this.charNodes = charNodes;
		}

		@Override
		public List<CharNode> toCharNodes() {
			return computeComplement(charNodes);
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(OBRK).append(NOT).append(StringUtils.join(charNodes)).append(CBRK)
				.toString();
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitProChar(this);
		}

		@Override
		public CompClassNode clone() {
			CompClassNode clone = (CompClassNode) super.clone();
			clone.charNodes = new ArrayList<CharNode>(charNodes.size());
			for (CharNode subNode : charNodes) {
				clone.charNodes.add(subNode.clone());
			}
			return clone;
		}
	}

	static abstract class CharNode extends ProCharNode implements LeafNode, Comparable<CharNode> {

		public abstract char getFrom();

		public abstract char getTo();

		@Override
		public int compareTo(CharNode o) {
			if (getTo() < o.getFrom()) {
				return -1;
			} else if (getFrom() > o.getTo()) {
				return 1;
			} else {
				return 0;
			}
		}

		public boolean contains(char c) {
			return getFrom() <= c && getTo() >= c;
		}

		@Override
		public CharNode clone() {
			return (CharNode) super.clone();
		}
	}

	public static class RangeCharNode extends CharNode implements LeafNode {

		char from;
		char to;

		public RangeCharNode(SingleCharNode from, SingleCharNode to) {
			this(from.getValue(), to.getValue());
		}

		public RangeCharNode(char from, char to) {
			this.from = from < to ? from : to;
			this.to = from < to ? to : from;
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
		public List<CharNode> toCharNodes() {
			return Arrays.asList((CharNode) this);
		}

		@Override
		public String toString() {
			return new StringBuilder()
				.append(from).append(DASH).append(to)
				.toString();
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitRangeChar(this);
		}
	}

	public static class SingleCharNode extends CharNode implements LeafNode, JoinableNode {

		char value;

		public SingleCharNode(char value) {
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
		public List<CharNode> toCharNodes() {
			return Arrays.asList((CharNode) this);
		}

		@Override
		public String toString() {
			return Pattern.escapeOperators(value);
		}

		@Override
		public String getLiteralValue() {
			return String.valueOf(value);
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitSingleChar(this);
		}

	}

	public static class AnyCharNode extends ProCharNode implements LeafNode {

		private static final List<CharNode> DOTALL = Arrays.asList(
			(CharNode) new RangeCharNode(Character.MIN_VALUE, Character.MAX_VALUE)
			);
		private static final List<CharNode> DEFAULT = Arrays.asList(
			(CharNode) new RangeCharNode(Character.MIN_VALUE, before('\n')),
			(CharNode) new RangeCharNode(after('\n'), before('\r')),
			(CharNode) new RangeCharNode(after('\r'), before('\u0085')),
			(CharNode) new RangeCharNode(after('\u0085'), before('\u2028')),
			(CharNode) new RangeCharNode(after('\u2029'), Character.MAX_VALUE)
			);

		private boolean dotall;

		public AnyCharNode(boolean dotall) {
			this.dotall = dotall;
		}

		@Override
		public String toString() {
			return ".";
		}

		@Override
		public List<CharNode> toCharNodes() {
			if (dotall) {
				return DOTALL;
			} else {
				return DEFAULT;
			}
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitProChar(this);
		}

	}

	public static class StringNode implements LeafNode, JoinableNode {

		String value;

		public StringNode(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public List<SingleCharNode> toChars() {
			List<SingleCharNode> chars = new ArrayList<SingleCharNode>();
			for (char c : value.toCharArray()) {
				chars.add(new SingleCharNode(c));
			}
			return chars;
		}

		@Override
		public String getLiteralValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitString(this);
		}

		@Override
		public StringNode clone() {
			try {
				return (StringNode) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class EmptyNode implements LeafNode {

		@Override
		public String toString() {
			return "";
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitEmpty(this);
		}
		
		@Override
		public EmptyNode clone() {
			try {
				return (EmptyNode) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public static class GroupNode implements DelegatorNode {

		private PatternNode subNode;

		public GroupNode(PatternNode node) {
			this.subNode = node;
		}

		@Override
		public PatternNode getSubNode() {
			return subNode;
		}

		@Override
		public void replaceSubNode(PatternNode node, PatternNode by) {
			if (subNode == node) {
				subNode = by;
			}
		}

		@Override
		public <T> T apply(PatternNodeVisitor<T> visitor) {
			return visitor.visitGroup(this);
		}

		@Override
		public String toString() {
			return '(' + subNode.toString() + ')';
		}

		@Override
		public GroupNode clone() {
			try {
				GroupNode clone = (GroupNode) super.clone();
				clone.subNode = subNode.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

}
