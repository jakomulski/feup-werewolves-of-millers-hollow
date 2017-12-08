package pt.up.fe.werewolves_of_millers_hollow_game.example;

import jade.wrapper.StaleProxyException;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.engine.Game;
import pt.up.fe.werewolves_of_millers_hollow_game.engine.GameSpecyfication;
import pt.up.fe.werewolves_of_millers_hollow_game.example.dummy.DummyPlayer;
import pt.up.fe.werewolves_of_millers_hollow_game.example.smart.SmartPlayer;

public class App {

	public static void main(String[] args) throws StaleProxyException, InterruptedException {

		GameSpecyfication gameSpecyfication = new GameSpecyfication();

		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.WEREWOLF, 2);
		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.VILLAGER, 6);
		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.FORTUNE_TELLER, 1);
		Game game = new Game(gameSpecyfication).addModerator(DummyModerator.class).addPlayer(SmartPlayer.class)
				.addPlayer(SmartPlayer.class).addPlayer(DummyPlayer.class).addPlayer(SmartPlayer.class)
				.addPlayer(SmartPlayer.class).addPlayer(SmartPlayer.class).addPlayer(SmartPlayer.class)
				.addPlayer(SmartPlayer.class).addPlayer(DummyPlayer.class);
	}
}
