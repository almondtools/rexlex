package com.almondtools.rexlex.automaton;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.almondtools.rexlex.automaton.DeterministicAutomaton.State;
import com.almondtools.rexlex.automaton.DeterministicAutomaton.Transition;
import net.amygdalum.util.text.CharUtils;

public class DeterministicAutomatonExport implements AutomatonExport {

	private DeterministicAutomaton automaton;
	private String name;

	public DeterministicAutomatonExport(DeterministicAutomaton automaton, String name) {
		this.automaton = automaton;
		this.name = name;
	}

	@Override
	public void to(OutputStream out) throws IOException {
		Writer w = null;
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

	private void writeStart(Writer w) throws IOException {
		w.write("start [shape=point];\n");
		w.write("start -> " + automaton.getStart().getId() + ";\n");
	}

	public void writeAutomaton(Writer writer) throws IOException {
		for (State state : automaton.findAllStates()) {
			if (state == automaton.getError()) {
				// skip this state
				continue;
			} else {
				writeState(writer, state);
			}
			String stateId = state.getId();
			for (Transition transition : state.getTransitions()) {
				State target = transition.getTarget();
				String targetId = target.getId();
				if (target == automaton.getError()) {
					// skip this transition
					continue;
				} else {
					writeTransition(writer, stateId, transition, targetId);
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

	private void writeTransition(Writer writer, String stateId, Transition transition, String targetId) throws IOException {
		char from = transition.getFrom();
		char to = transition.getTo();
		String format = (from == to) ? " [label=\"" + charToString(from) + "\"]" : " [label=\"" + charToString(from) + "-" + charToString(to) + "\"]";
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
