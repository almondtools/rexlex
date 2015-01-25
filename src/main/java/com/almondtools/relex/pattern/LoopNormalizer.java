package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.Pattern.ProCharNode.compact;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.relex.pattern.Pattern.AlternativesNode;
import com.almondtools.relex.pattern.Pattern.CharNode;
import com.almondtools.relex.pattern.Pattern.ComplementNode;
import com.almondtools.relex.pattern.Pattern.ConcatNode;
import com.almondtools.relex.pattern.Pattern.ConjunctiveNode;
import com.almondtools.relex.pattern.Pattern.EmptyNode;
import com.almondtools.relex.pattern.Pattern.GroupNode;
import com.almondtools.relex.pattern.Pattern.LoopNode;
import com.almondtools.relex.pattern.Pattern.OptionalNode;
import com.almondtools.relex.pattern.Pattern.PatternNode;
import com.almondtools.relex.pattern.Pattern.PatternNodeVisitor;
import com.almondtools.relex.pattern.Pattern.ProCharNode;
import com.almondtools.relex.pattern.Pattern.RangeCharNode;
import com.almondtools.relex.pattern.Pattern.SingleCharNode;
import com.almondtools.relex.pattern.Pattern.StringNode;

public class LoopNormalizer implements PatternNodeVisitor<PatternNode> {

	public LoopNormalizer() {
	}

	@Override
	public PatternNode visitAlternative(AlternativesNode node) {
		List<PatternNode> oldNodes = node.getSubNodes();
		List<PatternNode> subNodes = apply(oldNodes);
		if (!oldNodes.equals(subNodes)) {
			return new AlternativesNode(subNodes);
		} else {
			return node;
		}
	}

	@Override
	public PatternNode visitConjunctive(ConjunctiveNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PatternNode visitConcat(ConcatNode node) {
		List<PatternNode> oldNodes = node.getSubNodes();
		List<PatternNode> subNodes = apply(oldNodes);
		if (!oldNodes.equals(subNodes)) {
			return new ConcatNode(subNodes);
		} else {
			return node;
		}
	}

	@Override
	public PatternNode visitLoop(LoopNode node) {
		PatternNode oldNode = node.getSubNode();
		PatternNode subNode = oldNode.apply(this);
		int from = node.getFrom();
		int to = node.getTo();
		if (from == 1 && to == 1) {
			return subNode;
		} else if (from == 0 && to == 1) {
			return new OptionalNode(subNode);
		} else if (from == 0) {
			if (to == LoopNode.INFINITY) {
				return new LoopNode(subNode, from, to);
			} else {
				return new ConcatNode(optionalList(subNode, to));
			}
		} else {
			List<PatternNode> base = fixedList(subNode, from);
			if (to == LoopNode.INFINITY) {
				return ConcatNode.join(new ConcatNode(base), new LoopNode(subNode.clone(), from, to));
			} else {
				return ConcatNode.join(new ConcatNode(base), new ConcatNode(optionalList(subNode.clone(), to - from)));
			}
		}
	}

	private List<PatternNode> fixedList(PatternNode node, int size) {
		List<PatternNode> fixed = new ArrayList<PatternNode>(size);
		fixed.add(node);
		for (int i = 1; i < size; i++) {
			fixed.add(node.clone());
		}
		return fixed ;
	}

	private List<PatternNode> optionalList(PatternNode node, int size) {
		List<PatternNode> optional = new ArrayList<PatternNode>(size);
		optional.add(new OptionalNode(node));
		for (int i = 1; i < size; i++) {
			optional.add(new OptionalNode(node.clone()));
		}
		return optional ;
	}
	
	@Override
	public PatternNode visitOptional(OptionalNode node) {
		PatternNode oldNode = node.getSubNode();
		PatternNode subNode = oldNode.apply(this);
		if (subNode != oldNode) {
			return new OptionalNode(subNode);
		} else {
			return node;
		}
	}

	@Override
	public PatternNode visitComplement(ComplementNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PatternNode visitProChar(ProCharNode node) {
		List<CharNode> charNodes = node.toCharNodes();
		return new AlternativesNode(compact(charNodes));
	}

	@Override
	public PatternNode visitRangeChar(RangeCharNode node) {
		return node;
	}

	@Override
	public PatternNode visitSingleChar(SingleCharNode node) {
		return node;
	}

	@Override
	public PatternNode visitString(StringNode node) {
		return node;
	}

	@Override
	public PatternNode visitEmpty(EmptyNode node) {
		return node;
	}

	@Override
	public PatternNode visitGroup(GroupNode node) {
		PatternNode subnode = node.getSubNode().apply(this);
		return subnode;
	}

	private List<PatternNode> apply(List<? extends PatternNode> nodes) {
		List<PatternNode> as = new ArrayList<PatternNode>(nodes.size());
		for (PatternNode node : nodes) {
			as.add(node.apply(this));
		}
		return as;
	}

}
