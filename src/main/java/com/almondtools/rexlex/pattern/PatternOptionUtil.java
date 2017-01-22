package com.almondtools.rexlex.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.amygdalum.util.builders.ArrayLists;

public class PatternOptionUtil {

	public static List<PatternOption> splitOf(List<PatternOption> base, PatternOption... minus) {
		List<PatternOption> split = new ArrayList<PatternOption>();
		for (PatternOption option : minus) {
			boolean removed = base.remove(option);
			if (removed) {
				split.add(option);
			}
		}
		return split;
	}

	public static <T extends PatternOption> List<T> splitOf(List<PatternOption> base, Class<T> minus) {
		List<T> split = new ArrayList<T>();
		Iterator<PatternOption> iBase = base.iterator();
		while (iBase.hasNext()) {
			PatternOption option = iBase.next();
			if (minus.isInstance(option)) {
				split.add(minus.cast(option));
				iBase.remove();
			}
		}
		return split;
	}

	public static <T extends PatternOption> T splitFirst(List<PatternOption> base, Class<T> minus) {
		Iterator<PatternOption> iBase = base.iterator();
		while (iBase.hasNext()) {
			PatternOption option = iBase.next();
			if (minus.isInstance(option)) {
				iBase.remove();
				return minus.cast(option);
			}
		}
		return null;
	}

	public static List<PatternOption> list(PatternOption option, PatternOption... options) {
		return ArrayLists.list(option).addAll(options).build();
	}

	public static List<PatternOption> list(PatternOption... options) {
		if (options == null) {
			return Collections.emptyList();
		}
		return ArrayLists.of(options);
	}

}
