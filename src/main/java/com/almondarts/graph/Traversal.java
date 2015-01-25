package com.almondarts.graph;


public interface Traversal<K,V> {

	void traverse();
	void traverseNode(GraphNode<K> node);
	void setData(GraphNode<K> node, V data);
	V getData(GraphNode<K> node, Class<V> clazz);

}
