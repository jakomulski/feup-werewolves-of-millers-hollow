package pt.up.fe.werewolves_of_millers_hollow_game.behaviours;

import java.util.stream.Collectors;

import jade.domain.DFService;
import jade.domain.FIPAException;
import pt.up.fe.werewolves_of_millers_hollow_game.actions.Actions;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Moderator;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class ModeratorBehaviour extends BaseBehaviour {
	private static final long serialVersionUID = 8492835806340589393L;

	public ModeratorBehaviour(Moderator agent) {
		super(agent, AgentTypes.MODERATOR, () -> {
			return agent.operations.stream().filter(operation -> operation.getAgentType().equals(AgentTypes.MODERATOR))
					.collect(Collectors.toList());
		});

		try {
			DFService.register(agent, agent.createDFAgentDescriptionWithType(AgentTypes.MODERATOR.name()));
			System.out.println(agent.getLocalName() + ": I am moderator");
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Message message = new Message(MessageTypes.INFORM, MessageTopics.INIT).withReceivers(AgentTypes.PLAYER);
		Actions.sendMessage(myAgent, message).accept(null);
	};

}