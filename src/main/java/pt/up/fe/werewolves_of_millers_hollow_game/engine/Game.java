package pt.up.fe.werewolves_of_millers_hollow_game.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Moderator;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.example.SimpleModerator;
import pt.up.fe.werewolves_of_millers_hollow_game.example.dummy.DummyPlayer;
import pt.up.fe.werewolves_of_millers_hollow_game.example.mixed.SemiSmartPlayer;
import pt.up.fe.werewolves_of_millers_hollow_game.example.smart.SmartPlayer;

public class Game {
	private final GameSpecyfication gameSpecyfication;
	private final Profile profile = new ProfileImpl(true);
	private final ContainerController containerController = jade.core.Runtime.instance().createMainContainer(profile);

	public Game(GameSpecyfication gameSpecyfication) {
		this.gameSpecyfication = gameSpecyfication;
	}

	public static void buldByConsole() throws StaleProxyException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		GameSpecyfication gameSpecyfication = new GameSpecyfication();

		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.FORTUNE_TELLER, 1);
		int werewolfes = amountByConsole(br, "Amount of werewolfes:", n -> n > 0);
		int villagers = amountByConsole(br, "Amount of villagers:", n -> n > 0);
		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.WEREWOLF, werewolfes);
		gameSpecyfication.setNumberOfPlayersOfType(AgentTypes.VILLAGER, villagers);

		Game game = new Game(gameSpecyfication);

		int totalPlayers = werewolfes + villagers + 1;
		int smartPlayers = amountByConsole(br, "Amount of smart players (total:" + totalPlayers + "):",
				n -> n > 0 && n <= totalPlayers);
		game.addPlayer(SmartPlayer.class, smartPlayers);
		int semiPlayers = amountByConsole(br, "Amount of semi-smart players (total:" + totalPlayers + "):",
				n -> n > 0 && n <= totalPlayers - smartPlayers);
		game.addPlayer(SemiSmartPlayer.class, smartPlayers);
		game.addPlayer(DummyPlayer.class, amountByConsole(br, "Amount of dummy players (total:" + totalPlayers + "):",
				n -> n == totalPlayers - smartPlayers - semiPlayers));
		game.addModerator(SimpleModerator.class);
	}

	private static int amountByConsole(BufferedReader br, String info, Function<Integer, Boolean> condition)
			throws StaleProxyException {
		System.out.print(info);
		try {
			int i = Integer.parseInt(br.readLine());
			if (!condition.apply(i))
				throw new NumberFormatException();
			return i;

		} catch (NumberFormatException | IOException e) {
			System.out.println("incorrect number");
			return Game.amountByConsole(br, info, condition);
		}
	}

	public Game addModerator(Class<? extends Moderator> moderatorClass) throws StaleProxyException {
		containerController.createNewAgent("moderator", moderatorClass.getName(), new Object[] { gameSpecyfication })
				.start();
		return this;
	}

	private int playersNumber = 0;

	public Game addPlayer(Class<? extends Player> playerClass, int number) throws StaleProxyException {
		for (int i = 0; i < number; ++i) {
			containerController
					.createNewAgent("p" + playersNumber, playerClass.getName(), new Object[] { gameSpecyfication })
					.start();
			playersNumber++;
		}
		return this;
	}
}
