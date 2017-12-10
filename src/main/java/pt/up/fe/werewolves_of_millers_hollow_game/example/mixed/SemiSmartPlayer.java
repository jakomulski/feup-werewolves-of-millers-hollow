package pt.up.fe.werewolves_of_millers_hollow_game.example.mixed;

import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.example.dummy.DummyFortuneTellerStrategy;
import pt.up.fe.werewolves_of_millers_hollow_game.example.dummy.DummyVillagerStrategy;
import pt.up.fe.werewolves_of_millers_hollow_game.example.smart.SmartWerewolfStrategy;

public class SemiSmartPlayer extends Player {
	private static final long serialVersionUID = 1613757712627326987L;

	@Override
	public void playerConfig(Configuration config) {
		new SmartWerewolfStrategy(this).config(gameState -> config.addOperation(gameState, AgentTypes.WEREWOLF));
		new DummyVillagerStrategy(this).config(gameState -> config.addOperation(gameState, AgentTypes.VILLAGER));
		new DummyFortuneTellerStrategy(this)
				.config(gameState -> config.addOperation(gameState, AgentTypes.FORTUNE_TELLER));
	};
}
