package pt.up.fe.werewolves_of_millers_hollow_game.example;

import java.io.IOException;

import jade.wrapper.StaleProxyException;
import pt.up.fe.werewolves_of_millers_hollow_game.engine.Game;

public class App {

	public static void main(String[] args) throws StaleProxyException, InterruptedException, IOException {
		Game.buldByConsole();
		// GameSpecyfication gameSpecyfication = new GameSpecyfication();
		//
		// gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.WEREWOLF, 2);
		// gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.VILLAGER, 6);
		// gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.FORTUNE_TELLER,
		// 1);
		//
		// new
		// Game(gameSpecyfication).addModerator(SimpleModerator.class).addPlayer(SmartPlayer.class,
		// 4)
		// .addPlayer(DummyPlayer.class, 5);
	}
}
