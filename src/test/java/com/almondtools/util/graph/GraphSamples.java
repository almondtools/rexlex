package com.almondtools.util.graph;


public class GraphSamples {

	public static Graph<String> createGraph() {
		Graph<String> graph = new Graph<String>();
		graph.createRoot("A");
		graph.createNode("B");
		graph.createNode("C");
		graph.createNode("D");
		graph.createNode("E");
		graph.connectNodes("A", "B");
		graph.connectNodes("A", "C");
		graph.connectNodes("B", "D");
		graph.connectNodes("C", "D");
		graph.connectNodes("D", "E");
		return graph;
	}
	
}
