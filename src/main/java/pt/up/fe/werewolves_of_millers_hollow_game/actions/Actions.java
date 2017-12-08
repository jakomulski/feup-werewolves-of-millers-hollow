package pt.up.fe.werewolves_of_millers_hollow_game.actions;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.BaseAgent;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class Actions {

	public static Consumer<ACLMessage> sendMessage(BaseAgent agent, Message message) {
		return aclMessage -> {

			String receivers = message.getReceivers().stream().map(rec -> rec.name() + " ")
					.collect(Collectors.joining());
			System.out.println(
					String.format("%-20s%-20s%-50s%s", agent.getLocalName(), agent.myType, receivers, message));
			message.getReceivers().forEach(type -> {
				try {
					ACLMessage aclMsg = message.toACLMessage();
					aclMsg.addUserDefinedParameter("type", agent.myType.name());
					sendMessage(agent, aclMsg, type);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			});
		};
	}

	public static void sendMessageTo(BaseAgent agent, Message message, String targetName) {
		String receivers = message.getReceivers().stream().map(rec -> rec.name() + " ").collect(Collectors.joining());
		System.out.println(String.format("%-20s%-20s%-50s%s", agent.getLocalName(), agent.myType, receivers, message));
		message.getReceivers().forEach(type -> {
			try {
				ACLMessage aclMsg = message.toACLMessage();
				aclMsg.addUserDefinedParameter("type", agent.myType.name());
				sendMessageTo(agent, aclMsg, type, targetName);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		});
	};

	private static void sendMessage(Agent agent, ACLMessage aclMessage, AgentTypes type) throws FIPAException {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription description = new ServiceDescription();
		description.setType(type.name());
		template.addServices(description);
		DFAgentDescription[] result = DFService.search(agent, template);
		for (int i = 0; i < result.length; ++i)
			aclMessage.addReceiver(result[i].getName());
		agent.send(aclMessage);
	}

	private static void sendMessageTo(Agent agent, ACLMessage aclMessage, AgentTypes type, String targetName)
			throws FIPAException {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription description = new ServiceDescription();
		description.setType(type.name());
		template.addServices(description);
		DFAgentDescription[] result = DFService.search(agent, template);
		for (int i = 0; i < result.length; ++i) {
			if (result[i].getName().getLocalName().equals(targetName))
				aclMessage.addReceiver(result[i].getName());
		}

		agent.send(aclMessage);
	}

	public static Consumer<ACLMessage> replyToMessage(BaseAgent agent, MessageTypes messageType) {
		return replyToMessage(agent, messageType, () -> null);
	}

	public static Consumer<ACLMessage> replyToMessage(BaseAgent agent, MessageTypes messageType,
			Supplier<Serializable> messageContent) {

		return alcMessage -> {

			Serializable content = messageContent.get();
			ACLMessage reply = alcMessage.createReply();

			String topic = alcMessage.getUserDefinedParameter("topic");
			reply.addUserDefinedParameter("type", agent.myType.name());
			reply.addUserDefinedParameter("topic", topic);
			reply.setPerformative(messageType.ordinal());
			try {
				reply.setContentObject(content);
			} catch (IOException e) {
				e.printStackTrace();
			}

			AID aid = (AID) reply.getAllReceiver().next();
			System.out.println(String.format("%-20s%-20s%-50s%s %s%s", agent.getLocalName(), agent.myType.name(),
					aid.getLocalName(), MessageTopics.valueOf(topic).getText(), content, messageType.getText()));
			agent.send(reply);
		};
	}
}
