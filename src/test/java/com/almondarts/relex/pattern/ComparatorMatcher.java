package com.almondarts.relex.pattern;

import static java.lang.Integer.signum;

import java.util.Comparator;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ComparatorMatcher<T> extends TypeSafeMatcher<T> {

	private Comparator<T> comparator;
	private int sign;
	private T comparable;

	public ComparatorMatcher(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	public ComparatorMatcher<T> lessThan(T comparable) {
		return compare(-1, comparable);
	}

	public ComparatorMatcher<T> greaterThan(T comparable) {
		return compare(1, comparable);
	}
	
	public ComparatorMatcher<T> equalTo(T comparable) {
		return compare(0, comparable);
	}
	
	public ComparatorMatcher<T> compare(int sign, T comparable) {
		this.sign = sign;
		this.comparable = comparable;
		return this;
	}

	@Override
	protected boolean matchesSafely(T item) {
		if (comparator != null && comparable != null && comparisonFails(item)) {
			return false;
		}
		return true;
	}

	private boolean comparisonFails(T item) {
		return signum(comparator.compare(item, comparable)) != sign;
	}

	@Override
	public void describeTo(Description description) {
		if (comparator != null && comparable != null) {
			if (sign == 0) {
				description.appendText(" equal to ").appendValue(comparable);
			} else if (sign > 0) {
				description.appendText(" greater than ").appendValue(comparable);
			} else if (sign < 0) {
				description.appendText(" less than ").appendValue(comparable);
			}
		}
	}

	public static <T> ComparatorMatcher<T> compareWith(Comparator<T> comparator) {
		return new ComparatorMatcher<T>(comparator);
	}

}
