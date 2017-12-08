package pt.up.fe.werewolves_of_millers_hollow_game.example.smart;

import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class SmartPlayer extends Player {
	private static final long serialVersionUID = 1613757712627326987L;

	@Override
	public void playerConfig(Configuration config) {
		new SmartWerewolfStrategy(this).config(gameState -> config.addOperation(gameState, AgentTypes.WEREWOLF));
		new SmartVillagerStrategy(this).config(gameState -> config.addOperation(gameState, AgentTypes.VILLAGER));
		new SmartFortuneTellerStrategy(this)
				.config(gameState -> config.addOperation(gameState, AgentTypes.FORTUNE_TELLER));
	};
}
