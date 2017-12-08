package pt.up.fe.werewolves_of_millers_hollow_game.behaviours;

import java.util.stream.Collectors;

import jade.domain.DFService;
import jade.domain.FIPAException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class VillagerBehaviour extends BaseBehaviour {
	private static final long serialVersionUID = 5136651404771610780L;

	public VillagerBehaviour(Player player) {
		super(player, AgentTypes.VILLAGER, () -> {
			return player.operations.stream().filter(operation -> operation.getAgentType().equals(AgentTypes.VILLAGER))
					.collect(Collectors.toList());
		});
		try {
			DFService.deregister(player);
			DFService.register(player, player.createDFAgentDescriptionWithType(AgentTypes.VILLAGER.name()));

		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}
