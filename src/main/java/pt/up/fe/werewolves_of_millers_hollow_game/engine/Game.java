package pt.up.fe.werewolves_of_millers_hollow_game.engine;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Moderator;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;

public class Game {
	private final GameSpecyfication gameSpecyfication;
	private final Profile profile = new ProfileImpl(true);
	private final ContainerController containerController = jade.core.Runtime.instance().createMainContainer(profile);

	public Game(GameSpecyfication gameSpecyfication) {
		this.gameSpecyfication = gameSpecyfication;
	}

	public Game addModerator(Class<? extends Moderator> moderatorClass) throws StaleProxyException {
		containerController.createNewAgent("moderator", moderatorClass.getName(), new Object[] { gameSpecyfication })
				.start();
		return this;
	}

	private int playersNumber = 0;

	public Game addPlayer(Class<? extends Player> playerClass) throws StaleProxyException {
		containerController
				.createNewAgent("p" + playersNumber, playerClass.getName(), new Object[] { gameSpecyfication }).start();
		playersNumber++;
		return this;
	}

	// public void startGame() throws StaleProxyException {
	// int playersNumber =
	// gameSpecyfication.getPlayerTypesNumbers().values().stream().mapToInt(Integer::intValue)
	// .sum();
	// for (; playersNumber > 0; --playersNumber) {
	// containerController
	// .createNewAgent("p" + playersNumber, playerClass.getName(), new Object[]
	// { gameSpecyfication })
	// .start();
	// }
	//
	// containerController.createNewAgent("moderator", moderatorClass.getName(),
	// new Object[] { gameSpecyfication })
	// .start();
	// }

}
