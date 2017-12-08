package pt.up.fe.werewolves_of_millers_hollow_game.behaviours;

import java.util.stream.Collectors;

import jade.domain.DFService;
import jade.domain.FIPAException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class PlayerBehaviour extends BaseBehaviour {

	public PlayerBehaviour(Player player) {
		super(player, AgentTypes.PLAYER, () -> {
			return player.operations.stream().filter(operation -> operation.getAgentType().equals(AgentTypes.PLAYER))
					.collect(Collectors.toList());
		});

		try {
			DFService.register(player, player.createDFAgentDescriptionWithType(AgentTypes.PLAYER.name()));
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}
