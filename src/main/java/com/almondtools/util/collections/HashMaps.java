package com.almondtools.util.collections;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HashMaps<K,V> {

	private HashMap<K,V> map;

	private HashMaps(boolean linked) {
		if (linked) {
			this.map = new LinkedHashMap<K, V>();
		} else {
			this.map = new HashMap<K, V>();
		}
	}

	public HashMaps<K,V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public static <K,V> HashMaps<K, V> linked() {
		return new HashMaps<K,V>(true);
	}

	public static <K,V> HashMaps<K, V> hashed() {
		return new HashMaps<K,V>(false);
	}

	public static <K,V> HashMaps<K, V> invert(Map<V, K> toinvert) {
		HashMaps<K, V> maps = new HashMaps<K,V>(false);
		for (Map.Entry<V,K> entry: toinvert.entrySet()) {
			maps.put(entry.getValue(), entry.getKey());
		}
		return maps;
	}

	public HashMap<K,V> build() {
		return map;
	}

}
