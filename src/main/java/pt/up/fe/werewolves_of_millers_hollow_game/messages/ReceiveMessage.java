package pt.up.fe.werewolves_of_millers_hollow_game.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;

public class ReceiveMessage {
	private final MessageTypes messageType;
	private final MessageTopics messageTopic;
	private final List<AgentTypes> playerTypes = new ArrayList<>();

	public ReceiveMessage(MessageTypes messageType, MessageTopics messageTopic) {
		this.messageType = messageType;
		this.messageTopic = messageTopic;
	}

	public ReceiveMessage from(AgentTypes... playerType) {
		this.playerTypes.addAll(Arrays.asList(playerType));
		return this;
	}

	public MessageTypes getMessageType() {
		return messageType;
	}

	public MessageTopics getMessageTopic() {
		return messageTopic;
	}

	public List<AgentTypes> getPlayerTypes() {
		return playerTypes;
	}

}
