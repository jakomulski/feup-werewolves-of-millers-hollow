package pt.up.fe.werewolves_of_millers_hollow_game.behaviours;

import java.io.IOException;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
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

	private void sendToReceiver(ACLMessage aclMessage, AID receiver) {
		aclMessage.addReceiver(receiver);
		myAgent.send(aclMessage);
		aclMessage.removeReceiver(receiver);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Message message = new Message(MessageTypes.INFORM,
		// MessageTopics.INIT).withReceivers(AgentTypes.PLAYER);
		// Actions.sendMessage(myAgent, message).accept(null);
		try {
			sendToAllInitInfo();
		} catch (FIPAException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	private Stack<AgentTypes> createPlayesTypesStack() {
		Stack<AgentTypes> players = new Stack<AgentTypes>();
		myAgent.gameSpecyfication.getPlayerTypesNumbers().forEach((playerType, number) -> {
			for (int i = 0; i < number; ++i)
				players.push(playerType);
		});
		Collections.shuffle(players);
		return players;
	}

	private void sendToAllInitInfo() throws FIPAException, IOException {
		Stack<AgentTypes> playerTypesStack = createPlayesTypesStack();

		ACLMessage aclMessage = new Message(MessageTypes.INFORM, MessageTopics.INIT).withReceivers(AgentTypes.PLAYER)
				.toACLMessage();
		aclMessage.addUserDefinedParameter("type", myAgent.myType.name());
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription description = new ServiceDescription();
		description.setType(AgentTypes.PLAYER.name());
		template.addServices(description);
		DFAgentDescription[] result = DFService.search(myAgent, template);
		for (int i = 0; i < result.length; ++i) {
			AgentTypes type = playerTypesStack.pop();
			AID receiver = result[i].getName();
			aclMessage.setContentObject(type);
			sendToReceiver(aclMessage, receiver);

			System.out.println(String.format("%-20s%-20s%-50s%s", "Moderator", "MODERATOR", receiver.getLocalName(),
					"You are " + type));
			myAgent.players.put(receiver.getLocalName(), type);
		}
	}

}