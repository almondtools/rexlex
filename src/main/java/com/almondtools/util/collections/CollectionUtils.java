package com.almondtools.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

	public static char[] toCharArray(Collection<Character> charlist) {
		char[] chars = new char[charlist.size()];
		int i = 0;
		for (Character c : charlist) {
			chars[i] = c.charValue();
			i++;
		}
		return chars;
	}

	public static List<Integer> toIntegerList(int[] array) {
		List<Integer> list = new ArrayList<Integer>(array.length);
		for (int i : array) {
			list.add(i);
		}
		return list;
	}

	public static <F,T> List<T> map(List<F> list, ListMapping<F, T> mapping) {
		List<T> result = new LinkedList<T>();
		for (F item : list) {
			T mapped = mapping.map(item);
			if (mapped != null) {
				result.add(mapped);
			}
		}
		return result;
	}

	public static <FK,FV,TK,TV> Map<TK,TV> map(Map<FK,FV> map, MapMapping<FK,FV,TK,TV> mapping) {
		Map<TK,TV> result = new LinkedHashMap<TK,TV>();
		for (Map.Entry<FK,FV> item : map.entrySet()) {
			TK mappedkey = mapping.key(item.getKey(), item.getValue());
			TV mappedvalue = mapping.value(item.getKey(), item.getValue());
			if (mappedkey != null && mappedvalue != null) {
				result.put(mappedkey, mappedvalue);
			}
		}
		return result;
	}
}
