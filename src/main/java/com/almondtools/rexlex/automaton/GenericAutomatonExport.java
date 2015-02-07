package com.almondtools.rexlex.automaton;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.almondtools.rexlex.automaton.GenericAutomaton.EventTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.EventlessTransition;
import com.almondtools.rexlex.automaton.GenericAutomaton.State;
import com.almondtools.rexlex.automaton.GenericAutomaton.Transition;
import com.almondtools.util.text.CharUtils;

public class GenericAutomatonExport implements AutomatonExport {

	private GenericAutomaton automaton;
	private String name;

	public GenericAutomatonExport(GenericAutomaton automaton, String name) {
		this.automaton = automaton;
		this.name = name;
	}

	@Override
	public void to(OutputStream out) throws IOException {
		OutputStreamWriter w = null;
		try {
			w = new OutputStreamWriter(out, "UTF-8");
			w.write("digraph \"" + name + "\" {\n");
			writeStart(w);
			writeAutomaton(w);
			w.write("}");
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

	private void writeStart(OutputStreamWriter writer) throws IOException {
		writer.write("start [shape=point];\n");
		writer.write("start -> " + automaton.getStart().getId() + ";\n");
	}

	public void writeAutomaton(Writer writer) throws IOException {
		for (State state : automaton.findAllStates()) {
			if (state.error()) {
				// skip this state
				continue;
			} else {
				writeState(writer, state);
			}
			for (Transition transition : state.getTransitions()) {
				State target = transition.getTarget();
				if (target.error()) {
					// skip this transition
					continue;
				} else {
					writeTransition(writer, state, transition, target);
				}
			}
		}
	}

	private void writeState(Writer writer, State state) throws IOException {
		String stateId = state.getId();
		if (state.accept()) {
			String format = " [shape=doublecircle label=\"" + stateId + "\\n(" + state.getType().toString() + ")\"]";
			writer.write(stateId + format + ";\n");
		} else {
			String format = " [shape=circle]";
			writer.write(stateId + format + ";\n");
		}
	}

	private void writeTransition(Writer writer, State state, Transition transition, State target) throws IOException {
		String stateId = state.getId();
		String targetId = target.getId();
		if (transition instanceof EventTransition) {
			writeEvent(writer, (EventTransition) transition, stateId, targetId);
		} else if (transition instanceof EventlessTransition) {
			writeEpsilon(writer, (EventlessTransition) transition, stateId, targetId);
		}
	}

	private void writeEvent(Writer writer, EventTransition transition, String stateId, String targetId) throws IOException {
		char from = transition.getFrom();
		char to = transition.getTo();
		String format = (from == to) ? " [label=\"" + charToString(from) + "\"]" : " [label=\"" + charToString(from) + "-" + charToString(to) + "\"]";
		writer.write(stateId + " -> " + targetId + format + ";\n");
	}

	private void writeEpsilon(Writer writer, EventlessTransition transition, String stateId, String targetId) throws IOException {
		String format = " [label=\"&epsilon;\"]";
		writer.write(stateId + " -> " + targetId + format + ";\n");
	}

	private String charToString(char ch) {
		if (CharUtils.isAsciiPrintable(ch)) {
			return String.valueOf(ch);
		} else {
			StringBuilder buffer = new StringBuilder("\\u");
			String hex = Integer.toHexString((int) ch);
			for (int i = 0; i < 4 - hex.length(); i++) {
				buffer.append('0');
			}
			buffer.append(hex);
			return buffer.toString();
		}
	}

}
