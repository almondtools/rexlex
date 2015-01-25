package com.almondtools.util.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class ReversePostOrderWorklistTraversal<K, V> extends AbstractTraversal<K, V> implements Traversal<K, V> {

	private Class<V> clazz;
	private Set<GraphNode<K>> visited;
	private List<GraphNode<K>> ordered;
	private Set<GraphNode<K>> worklist;

	public ReversePostOrderWorklistTraversal(Graph<K> graph, Class<V> clazz) {
		super(graph);
		this.clazz = clazz;
		this.visited = new HashSet<GraphNode<K>>();
		this.ordered = new LinkedList<GraphNode<K>>();
	}

	@Override
	public void traverse() {
		super.traverse();
		this.worklist = new LinkedHashSet<GraphNode<K>>();
		worklist.addAll(getGraph().getNodes());
		while (!worklist.isEmpty()) {
			if (worklist.size() == 1) {
				Iterator<GraphNode<K>> iterator = worklist.iterator();
				GraphNode<K> node = iterator.next();
				iterator.remove();
				visitGraphNodeWorklist(node);
			} else {
				for (GraphNode<K> node : ordered) {
					if (worklist.contains(node)) {
						worklist.remove(node);
						visitGraphNodeWorklist(node);
					}
				}
			}
		}
	}

	private void visitGraphNodeWorklist(GraphNode<K> node) {
		V oldData = getData(node, clazz);
		visitGraphNode(node);
		V newData = getData(node, clazz);
		if (oldData == newData) {
			return;
		} else if (oldData == null) {
			worklist.addAll(node.getSuccessors());
		} else if (newData == null) {
			worklist.addAll(node.getSuccessors());
		} else if (oldData.equals(newData)) {
			return;
		} else {
			worklist.addAll(node.getSuccessors());
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
