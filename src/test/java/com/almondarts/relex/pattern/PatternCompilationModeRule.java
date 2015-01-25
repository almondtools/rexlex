package com.almondarts.relex.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.almondarts.relex.pattern.PatternCompilationMode.Exclude;
import com.almondarts.relex.pattern.PatternCompilationMode.Not;
import com.almondarts.relex.pattern.PatternCompilationMode.Only;

public class PatternCompilationModeRule implements TestRule {

	protected PatternCompilationMode mode;
	
	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				if (description.getAnnotation(Exclude.class) != null)  {
					base.evaluate();
				} else {
					List<PatternCompilationMode> modes = getModes(description);
					Map<PatternCompilationMode, String> failures = new EnumMap<PatternCompilationMode, String>(PatternCompilationMode.class);
					StackTraceElement[] stackTrace = null;
					for (PatternCompilationMode mode : modes) {
						PatternCompilationModeRule.this.mode = mode;
						try {
							base.evaluate();
						} catch (AssertionError e) {
							String message = e.getMessage() == null ? "" : e.getMessage();
							failures.put(mode, message);
							if (stackTrace == null) {
								stackTrace = e.getStackTrace();
							}
						} catch (Throwable e) {
							String message = e.getMessage() == null ? "" : e.getMessage();
							throw new RuntimeException("In mode " + mode.toString() + ": " + message, e);
						}
					}
					if (!failures.isEmpty()) {
						AssertionError ne = new AssertionError(computeMessage(failures));
						ne.setStackTrace(stackTrace);
						throw ne;
					}
				}
			}

		};
	}

	private String computeMessage(Map<PatternCompilationMode, String> failures) {
		StringBuilder buffer = new StringBuilder();
		for (Map.Entry<PatternCompilationMode, String> entry : failures.entrySet()) {
			buffer.append("in mode <").append(entry.getKey()).append(">: ").append(entry.getValue()).append("\n");
		}
		return buffer.toString();
	}

	private List<PatternCompilationMode> getModes(Description description) {
		Only only = description.getAnnotation(Only.class);
		Not not = description.getAnnotation(Not.class);
		List<PatternCompilationMode> modes = new ArrayList<PatternCompilationMode>();
		if (only != null) {
			modes.addAll(Arrays.asList(only.value()));
		} else {
			modes.addAll(EnumSet.allOf(PatternCompilationMode.class));
		}
		if (not != null) {
			modes.removeAll(Arrays.asList(not.value()));
		}
		return modes;
	}

}
