package pt.up.fe.werewolves_of_millers_hollow_game.engine;

import java.util.HashMap;
import java.util.Map;

import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class GameSpecyfication {
	private final Map<AgentTypes, Integer> playerTypesNumbers = new HashMap<>();

	public void setNumberOfPlayersOfType(AgentTypes type, int number) {
		playerTypesNumbers.put(type, number);
	}

	public Map<AgentTypes, Integer> getPlayerTypesNumbers() {
		return playerTypesNumbers;
	}

}
