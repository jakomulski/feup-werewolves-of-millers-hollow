package pt.up.fe.werewolves_of_millers_hollow_game.example;

import jade.wrapper.StaleProxyException;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.engine.Game;
import pt.up.fe.werewolves_of_millers_hollow_game.engine.GameSpecyfication;

public class App {

	public static void main(String[] args) throws StaleProxyException, InterruptedException {
		GameSpecyfication gameSpecyfication = new GameSpecyfication();

		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.WEREWOLF, 3);
		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.VILLAGER, 5);
		Game game = new Game(gameSpecyfication, DummyModerator.class, DummyPlayer.class);
		game.startGame();
	}
}
