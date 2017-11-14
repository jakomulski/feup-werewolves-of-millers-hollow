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

	private final Class<? extends Moderator> moderatorClass;
	private final Class<? extends Player> playerClass;

	public Game(GameSpecyfication gameSpecyfication, Class<? extends Moderator> moderatorClass,
			Class<? extends Player> playerClass) {
		this.gameSpecyfication = gameSpecyfication;
		this.moderatorClass = moderatorClass;
		this.playerClass = playerClass;
	}

	public void startGame() throws StaleProxyException {
		int playersNumber = gameSpecyfication.getPlayerTypesNumbers().values().stream().mapToInt(Integer::intValue)
				.sum();
		for (; playersNumber > 0; --playersNumber) {
			containerController
					.createNewAgent("p" + playersNumber, playerClass.getName(), new Object[] { gameSpecyfication })
					.start();
		}

		containerController.createNewAgent("moderator", moderatorClass.getName(), new Object[] { gameSpecyfication })
				.start();
	}
}
