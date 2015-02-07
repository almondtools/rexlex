package com.almondtools.rexlex.automaton;

import java.io.IOException;
import java.io.OutputStream;

public interface AutomatonExport {

	void to(OutputStream out) throws IOException;

}
