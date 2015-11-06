package com.almondtools.util.graph;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class GraphTest {

	private Graph<String> graph;
	
	@Before
	public void before() {
		graph = new Graph<String>();
	}
	
	@Test
	public void testCreateRoot() throws Exception {
		GraphNode<String> root = graph.createRoot("Root");
		assertThat(root.getKey(), equalTo("Root"));
		assertThat(graph.getRoot(), equalTo(root));
		assertThat(graph.getNode("Root"), sameInstance(root));
	}

	@Test
	public void testCreateNode() throws Exception {
		GraphNode<String> node = graph.createNode("Node");
		assertThat(node.getKey(), equalTo("Node"));
		assertThat(graph.getRoot(), not(equalTo(node)));
		assertThat(graph.getNode("Node"), sameInstance(node));
	}

	@Test
	public void testConnectNodes() throws Exception {
		GraphNode<String> node1 = graph.createNode("Node1");
		GraphNode<String> node2 = graph.createNode("Node2");
		graph.connectNodes("Node1", "Node2");
		assertThat(node1.getSuccessors(), hasItem(node2));
		assertThat(node1.getPredecessors().size(), equalTo(0));
		assertThat(node2.getPredecessors(), hasItem(node1));
		assertThat(node2.getSuccessors().size(), equalTo(0));
	}

}
