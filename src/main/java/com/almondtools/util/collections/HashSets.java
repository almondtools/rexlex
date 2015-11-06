package com.almondtools.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class HashSets<T> {

	private HashSet<T> set;

	private HashSets(boolean linked) {
		if (linked) {
			this.set = new LinkedHashSet<T>();
		} else {
			this.set = new HashSet<T>();
		}
	}

	private HashSets(Collection<? extends T> set, boolean linked) {
		if (linked) {
			this.set = new LinkedHashSet<T>(set);
		} else {
			this.set = new HashSet<T>(set);
		}
	}

	public static <T> HashSets<T> linked() {
		return new HashSets<T>(true);
	}

	public static <T> HashSets<T> linked(Collection<T> set) {
		return new HashSets<T>(set, true);
	}

	public static <T> HashSets<T> hashed() {
		return new HashSets<T>(false);
	}

	public static <T> HashSets<T> hashed(Collection<T> set) {
		return new HashSets<T>(set, false);
	}

	public static <T> HashSets<T> empty() {
		return new HashSets<T>(false);
	}

	@SafeVarargs
	public static <T> HashSet<T> of(T... elements) {
		return new HashSet<T>(Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> HashSet<T> of(Predicate<T> cond, T... elements) {
		HashSet<T> list = new HashSet<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}
	
	@SafeVarargs
	public static <T> LinkedHashSet<T> ofLinked(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> LinkedHashSet<T> ofLinked(Predicate<T> cond, T... elements) {
		LinkedHashSet<T> list = new LinkedHashSet<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}
	
	public static <T> HashSet<T> intersectionOf(Set<T> set, Set<T> other) {
		return new HashSets<T>(set, false).intersect(other).build();
	}

	public static <T> HashSet<T> unionOf(Set<T> set, Set<T> other) {
		return new HashSets<T>(set, false).union(other).build();
	}

	public static <T> HashSet<T> complementOf(Set<T> set, Set<T> minus) {
		return new HashSets<T>(set, false).minus(minus).build();
	}

	public HashSets<T> union(Set<T> add) {
		return addAll(add);
	}

	public HashSets<T> add(T add) {
		set.add(add);
		return this;
	}

	public HashSets<T> addConditional(boolean b, T add) {
		if (b) {
			set.add(add);
		}
		return this;
	}

	public HashSets<T> addAll(Set<T> add) {
		set.addAll(add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public HashSets<T> addAll(T... add) {
		set.addAll(Arrays.asList(add));
		return this;
	}

	public HashSets<T> minus(Set<T> remove) {
		return removeAll(remove);
	}

	public HashSets<T> remove(T remove) {
		set.remove(remove);
		return this;
	}

	public HashSets<T> removeConditional(boolean b, T remove) {
		if (b) {
			set.remove(remove);
		}
		return this;
	}

	public HashSets<T> removeAll(Set<T> remove) {
		set.removeAll(remove);
		return this;
	}

	@SuppressWarnings("unchecked")
	public HashSets<T> removeAll(T... remove) {
		set.removeAll(Arrays.asList(remove));
		return this;
	}

	public HashSets<T> intersect(Set<T> retain) {
		return retainAll(retain);
	}

	public HashSets<T> retain(T retain) {
		Set<T> retainAll = new HashSet<T>();
		retainAll.add(retain);
		set.retainAll(retainAll);
		return this;
	}

	public HashSets<T> retainConditional(boolean b, T retain) {
		if (b) {
			Set<T> retainAll = new HashSet<T>();
			retainAll.add(retain);
			set.retainAll(retainAll);
		}
		return this;
	}

	public HashSets<T> retainAll(Set<T> retain) {
		set.retainAll(retain);
		return this;
	}

	@SuppressWarnings("unchecked")
	public HashSets<T> retainAll(T... retain) {
		set.retainAll(Arrays.asList(retain));
		return this;
	}

	public HashSet<T> build() {
		return set;
	}

}
