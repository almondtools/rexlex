package com.almondarts.graph;

import static com.almondarts.graph.GraphSamples.createGraph;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class ReversePostOrderWorklistTraversalTest {

	private Graph<String> graph;
	
	@Before
	public void before() {
		graph = createGraph();
	}

	@Test
	public void testReversePostOrderWithNoChanges() throws Exception {
		final StringBuilder buffer = new StringBuilder();
		ReversePostOrderWorklistTraversal<String, String> rpo = new ReversePostOrderWorklistTraversal<String, String>(graph, String.class) {

			@Override
			public void visitGraphNode(GraphNode<String> node) {
				buffer.append(node.getKey());
			}
		};
		rpo.traverse();
		assertThat(buffer.toString(), equalTo("ACBDE"));
	}

	@Test
	public void testReversePostOrderWithAllChanges() throws Exception {
		final StringBuilder buffer = new StringBuilder(":");
		ReversePostOrderWorklistTraversal<String, String> rpo = new ReversePostOrderWorklistTraversal<String, String>(graph, String.class) {

			@Override
			public void visitGraphNode(GraphNode<String> node) {
				buffer.append(node.getKey());
				node.setData(buffer.toString());
			}
		};
		rpo.traverse();
		assertThat(buffer.toString(), equalTo(":ACBDE"));
	}
}
