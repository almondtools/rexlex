package com.almondtools.relex.pattern;

import static com.almondtools.relex.pattern.Pattern.ProCharNode.compact;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.almondtools.relex.pattern.Pattern.AlternativesNode;
import com.almondtools.relex.pattern.Pattern.BranchNode;
import com.almondtools.relex.pattern.Pattern.CharNode;
import com.almondtools.relex.pattern.Pattern.ComplementNode;
import com.almondtools.relex.pattern.Pattern.ConcatNode;
import com.almondtools.relex.pattern.Pattern.ConjunctiveNode;
import com.almondtools.relex.pattern.Pattern.DelegatorNode;
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
import com.almondtools.util.collections.ArrayLists;
import com.almondtools.util.collections.Predicates;

public class CharClassNormalizer implements PatternNodeVisitor<PatternNode> {

	@Override
	public PatternNode visitAlternative(AlternativesNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitConjunctive(ConjunctiveNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitConcat(ConcatNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitLoop(LoopNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitOptional(OptionalNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitComplement(ComplementNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitProChar(ProCharNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitRangeChar(RangeCharNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitSingleChar(SingleCharNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitString(StringNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitEmpty(EmptyNode node) {
		return visit(node);
	}

	@Override
	public PatternNode visitGroup(GroupNode node) {
		return visit(node);
	}

	private PatternNode visit(PatternNode node) {
		WithState visitor = new WithState();
		PatternNode newNode = node.apply(visitor);
		return visitor.normalize(newNode);
	}

	private static class WithState implements PatternNodeVisitor<PatternNode> {
		
		private Map<ProCharNode, PatternNode> parents;

		public WithState() {
			parents = new IdentityHashMap<ProCharNode, PatternNode>();
		}

		public PatternNode normalize(PatternNode node) {
			List<ProCharNode> charClasses = new ArrayList<ProCharNode>(parents.keySet());
			for (int i = 0; i < charClasses.size(); i++) {
				ProCharNode proCharI = charClasses.get(i);
				for (int j = i; j < charClasses.size(); j++) {
					ProCharNode proCharJ = charClasses.get(j);
					unify(proCharI, proCharJ);
				}
			}
			return node;
		}

		private void replace(ProCharNode c, ProCharNode... replacement) {
			PatternNode parent = parents.remove(c);
			List<ProCharNode> newnodes = listOf(replacement);
			AlternativesNode splitCharClass = new AlternativesNode(newnodes);
			if (parent instanceof DelegatorNode) {
				((DelegatorNode) parent).replaceSubNode(c, splitCharClass);
			} else if (parent instanceof BranchNode) {
				((BranchNode) parent).replaceSubNode(c, splitCharClass);
			}
			for (ProCharNode node : newnodes) {
				parents.put(node, splitCharClass);
			}
		}

		private ArrayList<ProCharNode> listOf(ProCharNode... replacement) {
			return ArrayLists.of(Predicates.<ProCharNode>notNull(), replacement);
		}

		private void unify(ProCharNode proCharI, ProCharNode proCharJ) {
			ProCharNode intersectionIJ = proCharI.intersect(proCharJ);
			if (intersectionIJ == null) {
				return;
			}

			ProCharNode remainderI = proCharI.minus(intersectionIJ);
			ProCharNode remainderJ = proCharJ.minus(intersectionIJ);

			replace(proCharI, intersectionIJ, remainderI);
			replace(proCharJ, intersectionIJ, remainderJ);
		}

		@Override
		public PatternNode visitAlternative(AlternativesNode node) {
			List<PatternNode> oldNodes = node.getSubNodes();
			List<PatternNode> subNodes = apply(oldNodes);
			for (PatternNode subnode : subNodes) {
				if (subnode instanceof ProCharNode) {
					addCharNode((ProCharNode) subnode, node);
				}
			}
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
			for (PatternNode subnode : subNodes) {
				if (subnode instanceof ProCharNode) {
					addCharNode((ProCharNode) subnode, node);
				}
			}
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
			if (subNode instanceof ProCharNode) {
				addCharNode((ProCharNode) subNode, node);
			}
			if (subNode != oldNode) {
				return new LoopNode(subNode, node.getFrom(), node.getTo());
			} else {
				return node;
			}
		}

		@Override
		public PatternNode visitOptional(OptionalNode node) {
			PatternNode oldNode = node.getSubNode();
			PatternNode subNode = oldNode.apply(this);
			if (subNode instanceof ProCharNode) {
				addCharNode((ProCharNode) subNode, node);
			}
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
			for (SingleCharNode subnode : node.toChars()) {
				addCharNode(subnode, node);
			}
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

		private void addCharNode(ProCharNode child, PatternNode parent) {
			parents.put(child, parent);
		}

		private List<PatternNode> apply(List<? extends PatternNode> nodes) {
			List<PatternNode> as = new ArrayList<PatternNode>(nodes.size());
			for (PatternNode node : nodes) {
				as.add(node.apply(this));
			}
			return as;
		}
	}

}
