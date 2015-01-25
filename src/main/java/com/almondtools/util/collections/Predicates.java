package com.almondtools.util.collections;


public class Predicates {

	public static <T> Predicate<T> all(final Class<T> clazz) {
		return new Predicate<T>() {

			@Override
			public boolean evaluate(T object) {
				if (clazz.isInstance(object)) {
					return true;
				} else {
					return false;
				}
			}
			
		};
	}

	public static <T> Predicate<T> notNull() {
		return new Predicate<T>() {

			@Override
			public boolean evaluate(T object) {
				return object != null;
			}
			
		};
	}

}
