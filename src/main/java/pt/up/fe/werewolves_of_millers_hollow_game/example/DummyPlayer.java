package pt.up.fe.werewolves_of_millers_hollow_game.example;

import jade.lang.acl.UnreadableException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class DummyPlayer extends Player {
	private static final long serialVersionUID = 1613757712627326987L;

	@Override
	public void config(Configuration config) {
		gameStartConfig(config);
		firstNightConfig(config);
		nightConfig(config);
	};

	private void gameStartConfig(Configuration config) {
		config.addOperation(GameStates.GAME_START, AgentTypes.PLAYER)
				.whenReceive(MessageTypes.INFORM, MessageTopics.INIT).fromTypes(AgentTypes.MODERATOR).thenSend(this,
						new Message(MessageTypes.QUESTION, MessageTopics.WHO_I_AM).withReceivers(AgentTypes.MODERATOR));

		config.addOperation(GameStates.GAME_START, AgentTypes.PLAYER)
				.whenReceive(MessageTypes.ANSWER, MessageTopics.WHO_I_AM).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					try {
						AgentTypes type = (AgentTypes) aclMsg.getContentObject();
						this.removeBehaviour(currentBehaviour);
						this.addBehaviour(type.getBehaviour(this));
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}).thenSend(this,
						new Message(MessageTypes.OK, MessageTopics.WHO_I_AM).withReceivers(AgentTypes.MODERATOR));

	}

	private void firstNightConfig(Configuration config) {
		int otherWerewolfesNumber = this.gameSpecyfication.getPlayerTypesNumbers().get(AgentTypes.WEREWOLF) - 1;

		config.addOperation(GameStates.FIRST_NIGHT, AgentTypes.WEREWOLF)
				.whenReceive(MessageTypes.INFORM, MessageTopics.FIRST_NIGHT).fromTypes(AgentTypes.MODERATOR)
				.thenSend(this, new Message(MessageTypes.QUESTION, MessageTopics.WHO_ARE_YOU)
						.withReceivers(AgentTypes.WEREWOLF));

		config.addOperation(GameStates.FIRST_NIGHT, AgentTypes.WEREWOLF)
				.whenReceive(MessageTypes.QUESTION, MessageTopics.WHO_ARE_YOU).fromTypes(AgentTypes.WEREWOLF)
				.thenReply(this, MessageTypes.ANSWER, this::getLocalName);

		config.addOperation(GameStates.FIRST_NIGHT, AgentTypes.WEREWOLF)
				.whenReceive(MessageTypes.ANSWER, MessageTopics.WHO_ARE_YOU).fromTypes(AgentTypes.WEREWOLF)
				.then(aclMsg -> {
					try {
						String werewolfName = (String) aclMsg.getContentObject();
						this.players.put(werewolfName, AgentTypes.WEREWOLF);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}).afterMessagesNumber(otherWerewolfesNumber).thenSend(this,
						new Message(MessageTypes.OK, MessageTopics.FIRST_NIGHT).withReceivers(AgentTypes.MODERATOR));

	}

	private void nightConfig(Configuration config) {
		config.addOperation(GameStates.NIGHT, AgentTypes.WEREWOLF).whenReceive(MessageTypes.INFORM, MessageTopics.NIGHT)
				.fromTypes(AgentTypes.MODERATOR).then(x -> System.out.println("VOTE!"));

	}
}
