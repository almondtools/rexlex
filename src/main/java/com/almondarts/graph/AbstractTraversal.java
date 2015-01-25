package com.almondarts.graph;

public abstract class AbstractTraversal<K, V> implements Traversal<K, V> {

	private Graph<K> graph;

	public AbstractTraversal(Graph<K> graph) {
		this.graph = graph;
	}
	
	public Graph<K> getGraph() {
		return graph;
	}

	@Override
	public void traverse() {
		graph.getRoot().apply(this);
	}

	@Override
	public V getData(GraphNode<K> node, Class<V> clazz) {
		return clazz.cast(node.getData(clazz));
	}
	
	@Override
	public void setData(GraphNode<K> node, V data) {
		node.setData(data);
	}
	
}
