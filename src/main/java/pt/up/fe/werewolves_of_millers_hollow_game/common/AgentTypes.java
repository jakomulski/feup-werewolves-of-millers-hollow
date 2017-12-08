package pt.up.fe.werewolves_of_millers_hollow_game.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import jade.core.behaviours.Behaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.behaviours.FortuneTellerBehaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.behaviours.VillagerBehaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.behaviours.WerewolfBehaviour;

public enum AgentTypes {
	PLAYER, MODERATOR, VILLAGER, WEREWOLF, FORTUNE_TELLER;
	public static final AgentTypes[] PLAYERS = { VILLAGER, WEREWOLF, FORTUNE_TELLER, PLAYER };

	public static final AgentTypes[] ALL = { FORTUNE_TELLER, VILLAGER, WEREWOLF, MODERATOR };

	private final static Map<AgentTypes, Function<Player, Behaviour>> typesToBehaviours = new HashMap<>();
	static {
		typesToBehaviours.put(VILLAGER, agent -> new VillagerBehaviour(agent));
		typesToBehaviours.put(WEREWOLF, agent -> new WerewolfBehaviour(agent));
		typesToBehaviours.put(FORTUNE_TELLER, agent -> new FortuneTellerBehaviour(agent));
	}

	// lazy initialization
	public Behaviour getBehaviour(Player player) {
		return typesToBehaviours.get(this).apply(player);
	}

}
