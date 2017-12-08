package pt.up.fe.werewolves_of_millers_hollow_game.common;

public class GenericWrapper<T> {
	private T value = null;

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public void setIfNull(T value) {
		if (this.value == null)
			this.value = value;
	}
}