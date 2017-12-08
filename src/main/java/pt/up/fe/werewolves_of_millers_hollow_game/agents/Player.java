package pt.up.fe.werewolves_of_millers_hollow_game.agents;

import java.util.Arrays;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.UnreadableException;
import pt.up.fe.werewolves_of_millers_hollow_game.actions.Actions;
import pt.up.fe.werewolves_of_millers_hollow_game.behaviours.PlayerBehaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public abstract class Player extends BaseAgent {
	@Override
	protected final Behaviour getBahaviour() {
		return new PlayerBehaviour(this);
	}

	protected abstract void playerConfig(Configuration config);

	AgentTypes myType = AgentTypes.PLAYER;

	protected void config(Configuration config) {
		playerConfig(config);

		int otherPlayerNumber = this.gameSpecyfication.getPlayerTypesNumbers().values().stream().mapToInt(n -> n).sum()
				- 1;

		config.addOperation(GameStates.GAME_START, AgentTypes.PLAYER)
				.whenReceive(MessageTypes.INFORM, MessageTopics.INIT).fromTypes(AgentTypes.MODERATOR).then(aclMsg -> {
					try {
						myType = (AgentTypes) aclMsg.getContentObject();
						Actions.sendMessage(this,
								new Message(MessageTypes.INFORM, MessageTopics.HELLO, this::getLocalName)
										.withReceivers(AgentTypes.PLAYER))
								.accept(null);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				});

		config.addOperation(GameStates.GAME_START, AgentTypes.PLAYER)
				.whenReceive(MessageTypes.INFORM, MessageTopics.HELLO).fromTypes(AgentTypes.PLAYER).then(aclMsg -> {
					String name = aclMsg.getSender().getLocalName();
					this.players.put(name, AgentTypes.PLAYER);
				}).afterMessagesNumber(() -> otherPlayerNumber).then(x -> {
					this.removeBehaviour(currentBehaviour);
					this.addBehaviour(myType.getBehaviour(this));
				}).afterMessagesNumber(() -> otherPlayerNumber)
				.thenSend(this, new Message(MessageTypes.OK, MessageTopics.INIT).withReceivers(AgentTypes.MODERATOR));

		Arrays.asList(AgentTypes.PLAYERS).forEach(playerType -> {
			config.addOperation(GameStates.NIGHT, playerType)
					.whenReceive(MessageTypes.INFORM, MessageTopics.NIGHT_START).fromTypes(AgentTypes.MODERATOR)
					.then(aclMsg -> {
						try {
							String killedPlayer = (String) aclMsg.getContentObject();
							if (this.getLocalName().equals(killedPlayer))
								this.doDelete();
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
					});
			config.addOperation(GameStates.DAY, playerType).whenReceive(MessageTypes.INFORM, MessageTopics.DAY_START)
					.fromTypes(AgentTypes.MODERATOR).then(aclMsg -> {
						try {
							String killedPlayer = (String) aclMsg.getContentObject();
							if (this.getLocalName().equals(killedPlayer))
								this.doDelete();
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
					});
		});

	};

}
