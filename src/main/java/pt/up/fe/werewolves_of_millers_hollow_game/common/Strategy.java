package pt.up.fe.werewolves_of_millers_hollow_game.common;

import java.util.function.Function;

public interface Strategy {
	void config(Function<GameStates, Operation> config);
}
