package com.almondarts.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphNode<K> {

	private K key;
	private Map<Class<?>, Object> data;
	private List<GraphNode<K>> predecessors;
	private List<GraphNode<K>> successors;

	
	public GraphNode(K key) {
		this.key = key;
		this.data = new HashMap<Class<?>, Object>();
		this.predecessors = new LinkedList<GraphNode<K>>();
		this.successors = new LinkedList<GraphNode<K>>();
	}

	public K getKey() {
		return key;
	}
	
	public <T> T getData(Class<T> clazz) {
		return clazz.cast(data.get(clazz));
	}
	
	public void setData(Object data) {
		this.data.put(data.getClass(), data);
	}

	public void addPredecessor(GraphNode<K> pre) {
		if (!predecessors.contains(pre)) {
			predecessors.add(pre);
		}
	}
	
	public List<GraphNode<K>> getPredecessors() {
		return predecessors;
	}

	public void addSuccessor(GraphNode<K> suc) {
		if (!successors.contains(suc)) {
			successors.add(suc);
		}
	}

	public List<GraphNode<K>> getSuccessors() {
		return successors;
	}
	
	public <V> void apply(Traversal<K,V> traversal) {
		traversal.traverseNode(this);
	}

}
