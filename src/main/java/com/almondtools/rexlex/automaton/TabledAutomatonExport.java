package com.almondtools.rexlex.automaton;

import static com.almondtools.rexlex.automaton.TabledAutomaton.ERROR;
import static com.almondtools.rexlex.automaton.TabledAutomaton.START;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.almondtools.rexlex.TokenType;

import net.amygdalum.util.text.CharUtils;

public class TabledAutomatonExport implements AutomatonExport {

	private TabledAutomaton automaton;
	private String name;

	public TabledAutomatonExport(TabledAutomaton automaton, String name) {
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
		w.write("start -> " + START + ";\n");
	}

	public void writeAutomaton(Writer writer) throws IOException {
		for (int state = 0; state < automaton.getStateCount(); state++) {
			if (state == ERROR) {
				// skip this state
				continue;
			} else {
				writeState(writer, state);
			}
			for (int charClass = 0; charClass < automaton.getCharClassCount(); charClass++) {
				int target = automaton.getTarget(state, charClass);
				if (target == ERROR) {
					// skip this transition
					continue;
				} else {
					writeTransition(writer, state, charClass, target);
				}
			}
		}
	}

	private void writeState(Writer writer, int state) throws IOException {
		TokenType type = automaton.getAccept()[state];
		if (type == null) {
			String format = " [shape=circle]";
			writer.write(state + format + ";\n");
		} else {
			String format = " [shape=doublecircle label=\"" + state + "\\n(" + type.toString() + ")\"]";
			writer.write(state + format + ";\n");
		}
	}

	private void writeTransition(Writer writer, int state, int charClass, int target) throws IOException {
		CharClassMapper charClasses = automaton.getCharClassMapper();
		char from = charClasses.lowerBound(charClass);
		char to = charClasses.upperBound(charClass);
		String format = (from == to) ? " [label=\"" + charToString(from) + "\"]" : " [label=\"" + charToString(from) + "-" + charToString(to) + "\"]";
		writer.write(state + " -> " + target + format + ";\n");
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
