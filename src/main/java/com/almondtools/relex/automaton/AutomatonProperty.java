package com.almondtools.relex.automaton;

public enum AutomatonProperty {
	LINEAR(true,true),ACYCLIC(true),CYCLIC,UNKNOWN;

	private boolean linear;
	private boolean acyclic;

	private AutomatonProperty() {
		this(false);
	}
	
	private AutomatonProperty(boolean acyclic) {
		this(false, acyclic);
	}
	
	private AutomatonProperty(boolean linear, boolean acyclic) {
		this.linear = linear;
		this.acyclic = acyclic;
	}
	
	public boolean isLinear() {
		return linear;
	}

	public boolean isAcyclic() {
		return acyclic;
	}
	
	public boolean isUnknown() {
		return this == UNKNOWN;
	}
}
