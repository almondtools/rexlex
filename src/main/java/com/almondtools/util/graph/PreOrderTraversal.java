package com.almondtools.util.graph;

import java.util.HashSet;
import java.util.Set;

public abstract class PreOrderTraversal<K, V> extends AbstractTraversal<K, V> implements Traversal<K, V> {

	private Set<GraphNode<K>> visited;

	public PreOrderTraversal(Graph<K> graph) {
		super(graph);
		this.visited = new HashSet<GraphNode<K>>();
	}

	@Override
	public void traverse() {
		super.traverse();
	}

	@Override
	public void traverseNode(GraphNode<K> node) {
		if (!visited.contains(node)) {
			visited.add(node);
			visitGraphNode(node);
			for (GraphNode<K> next : node.getSuccessors()) {
				next.apply(this);
			}
		}
	}

	public abstract void visitGraphNode(GraphNode<K> node);

}
