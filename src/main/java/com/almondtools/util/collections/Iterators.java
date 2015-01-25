package com.almondtools.util.collections;

import java.util.Arrays;
import java.util.Iterator;

public class Iterators {

	public static <T> Iterator<T> of(T... elements) {
		return Arrays.asList(elements).iterator();
	}

}
