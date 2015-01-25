package com.almondarts.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class ReversePostOrderTraversal<K, V> extends AbstractTraversal<K, V> implements Traversal<K, V> {

	private Set<GraphNode<K>> visited;
	private List<GraphNode<K>> ordered;

	public ReversePostOrderTraversal(Graph<K> graph) {
		super(graph);
		this.visited = new HashSet<GraphNode<K>>();
		this.ordered = new LinkedList<GraphNode<K>>();
	}

	@Override
	public void traverse() {
		super.traverse();
		for (GraphNode<K> node : ordered) {
			visitGraphNode(node);
		}
	}

	@Override
	public void traverseNode(GraphNode<K> node) {
		if (!visited.contains(node)) {
			visited.add(node);
			for (GraphNode<K> next : node.getSuccessors()) {
				next.apply(this);
			}
			record(node);
		}
	}

	private void record(GraphNode<K> node) {
		ordered.add(0, node);
	}

	public abstract void visitGraphNode(GraphNode<K> node);

}
