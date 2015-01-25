package com.almondarts.graph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Graph<K> {

	private GraphNode<K> root;
	private Map<K, GraphNode<K>> nodes;
	
	public Graph() {
		this.nodes = new LinkedHashMap<K, GraphNode<K>>();
	}
	
	public GraphNode<K> getRoot() {
		return root;
	}

	public GraphNode<K> createRoot(K key) {
		root = createNode(key);
		return root;
	}
	
	public GraphNode<K> getNode(K key) {
		return nodes.get(key);
	}

	public GraphNode<K> createNode(K key) {
		GraphNode<K> node = nodes.get(key);
		if (node == null) {
			node = new GraphNode<K>(key);
			nodes.put(key, node);
		}
		return node;
	}
	
	public Collection<GraphNode<K>> getNodes() {
		return nodes.values();
	}

	public void connectNodes(K from, K to) {
		GraphNode<K> toNode = nodes.get(to);
		GraphNode<K> fromNode = nodes.get(from);
		fromNode.addSuccessor(toNode);
		toNode.addPredecessor(fromNode);
	}

}
