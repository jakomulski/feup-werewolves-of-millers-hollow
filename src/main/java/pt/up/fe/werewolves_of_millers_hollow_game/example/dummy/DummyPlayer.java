package pt.up.fe.werewolves_of_millers_hollow_game.example.dummy;

import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class DummyPlayer extends Player {
	private static final long serialVersionUID = 1613757712627326987L;

	@Override
	public void playerConfig(Configuration config) {
		new DummyWerewolfStrategy(this)
		.config(gameState -> config.addOperation(gameState, AgentTypes.WEREWOLF));
		new DummyVillagerStrategy(this)
		.config(gameState -> config.addOperation(gameState, AgentTypes.VILLAGER));
		new DummyFortuneTellerStrategy(this)
				.config(gameState -> config
						.addOperation(gameState, AgentTypes.FORTUNE_TELLER));
	};
}
