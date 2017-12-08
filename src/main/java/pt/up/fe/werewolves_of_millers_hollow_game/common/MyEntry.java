package pt.up.fe.werewolves_of_millers_hollow_game.common;

import java.util.Map;

import jade.util.leap.Serializable;

public final class MyEntry<K, V> implements Map.Entry<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K key;
	private V value;

	public MyEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}

	@Override
	public String toString() {
		return "(" + key + ", " + value + ")";
	}
}