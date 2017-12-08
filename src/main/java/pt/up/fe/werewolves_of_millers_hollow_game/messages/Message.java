package pt.up.fe.werewolves_of_millers_hollow_game.messages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import jade.lang.acl.ACLMessage;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class Message {
	private List<AgentTypes> playerTypes = new ArrayList<>();
	private List<String> playerNames = new ArrayList<>();
	private MessageTypes messageType;
	private MessageTopics messageTopic;
	private Supplier<Serializable> messageContent;

	public ACLMessage toACLMessage() {
		ACLMessage aclMsg = new ACLMessage(getMessageType().ordinal());
		aclMsg.addUserDefinedParameter("topic", getMessageTopic().name());
		try {
			aclMsg.setContentObject(getMessageContent().get());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return aclMsg;
	}

	public MessageTopics getMessageTopic() {
		return messageTopic;
	}

	public Message(MessageTypes messageType, MessageTopics messageTopic, Supplier<Serializable> messageContent) {
		this.messageType = messageType;
		this.messageTopic = messageTopic;
		this.messageContent = messageContent;
	}

	public Message(MessageTypes messageType, MessageTopics messageTopic) {
		this.messageType = messageType;
		this.messageTopic = messageTopic;
		this.messageContent = () -> null;
	}

	public List<AgentTypes> getReceivers() {
		return playerTypes;
	}

	public Message withReceivers(AgentTypes... playerType) {
		this.playerTypes = Arrays.asList(playerType);
		return this;
	}

	public List<String> getPlayerNames() {
		return playerNames;
	}

	public Message withPlayerNames(List<String> playerNames) {
		this.playerNames = playerNames;
		return this;
	}

	public MessageTypes getMessageType() {
		return messageType;
	}

	public Supplier<Serializable> getMessageContent() {
		return messageContent;
	}

	@Override
	public String toString() {
		return messageTopic.getText() + (messageContent.get() == null ? "" : (" " + messageContent.get()))
				+ messageType.getText();
	}

}
