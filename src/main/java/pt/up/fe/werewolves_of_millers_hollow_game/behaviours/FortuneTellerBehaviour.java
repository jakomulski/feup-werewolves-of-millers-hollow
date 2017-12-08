package pt.up.fe.werewolves_of_millers_hollow_game.behaviours;

import java.util.stream.Collectors;

import jade.domain.DFService;
import jade.domain.FIPAException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class FortuneTellerBehaviour extends BaseBehaviour {
	public FortuneTellerBehaviour(Player player) {
		super(player, AgentTypes.FORTUNE_TELLER,
				() -> player.operations.stream()
						.filter(operation -> operation.getAgentType().equals(AgentTypes.FORTUNE_TELLER))
						.collect(Collectors.toList()));

		try {
			DFService.deregister(player);
			DFService.register(player, player.createDFAgentDescriptionWithType(AgentTypes.FORTUNE_TELLER.name()));
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}