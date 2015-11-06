package com.almondtools.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayLists<T> {

	private ArrayList<T> list;

	private ArrayLists() {
		this.list = new ArrayList<T>();
	}

	private ArrayLists(Collection<? extends T> list) {
		this.list = new ArrayList<T>(list);
	}

	@SafeVarargs
	public static <T> ArrayLists<T> list(T... elements) {
		return new ArrayLists<T>(java.util.Arrays.asList(elements));
	}

	public static <T> ArrayLists<T> empty() {
		return new ArrayLists<T>();
	}

	@SafeVarargs
	public static <T> ArrayList<T> of(T... elements) {
		return new ArrayList<T>(java.util.Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> ArrayList<T> of(Predicate<T> cond, T... elements) {
		ArrayList<T> list = new ArrayList<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}
	
	public ArrayLists<T> add(T add) {
		list.add(add);
		return this;
	}

	public ArrayLists<T> addConditional(boolean b, T add) {
		list.add(add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ArrayLists<T> addAll(T... add) {
		list.addAll(java.util.Arrays.asList(add));
		return this;
	}

	public ArrayLists<T> addAll(List<T> add) {
		list.addAll(add);
		return this;
	}

	public ArrayLists<T> remove(T remove) {
		list.remove(remove);
		return this;
	}

	public ArrayLists<T> removeConditional(boolean b, T remove) {
		if (b) {
			list.remove(remove);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public ArrayLists<T> removeAll(T... remove) {
		list.removeAll(java.util.Arrays.asList(remove));
		return this;
	}

	public ArrayLists<T> removeAll(List<T> remove) {
		list.removeAll(remove);
		return this;
	}

	public ArrayLists<T> retain(T retain) {
		Set<T> retainAll = new HashSet<T>();
		retainAll.add(retain);
		list.retainAll(retainAll);
		return this;
	}

	public ArrayLists<T> retainConditional(boolean b, T retain) {
		if (b) {
			Set<T> retainAll = new HashSet<T>();
			retainAll.add(retain);
			list.retainAll(retainAll);
		}
		return this;
	}

	public ArrayLists<T> retainAll(List<T> retain) {
		list.retainAll(retain);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ArrayLists<T> retainAll(T... retain) {
		list.retainAll(java.util.Arrays.asList(retain));
		return this;
	}

	public ArrayList<T> build() {
		return list;
	}

}
