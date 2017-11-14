package pt.up.fe.werewolves_of_millers_hollow_game.example;

import java.util.Stack;

import pt.up.fe.werewolves_of_millers_hollow_game.agents.Moderator;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class DummyModerator extends Moderator {
	private static final long serialVersionUID = 6116674073712848165L;

	@Override
	public void config(Configuration config) {

		Stack<AgentTypes> players = new Stack<AgentTypes>();
		this.gameSpecyfication.getPlayerTypesNumbers().forEach((playerType, number) -> {
			for (int i = 0; i < number; ++i)
				players.push(playerType);
		});
		int playersNumber = players.size();
		int werewolfesNumber = gameSpecyfication.getPlayerTypesNumbers().get(AgentTypes.WEREWOLF);

		config.addOperation(GameStates.GAME_START, AgentTypes.MODERATOR)
				.whenReceive(MessageTypes.QUESTION, MessageTopics.WHO_I_AM).fromTypes(AgentTypes.PLAYER)
				.thenReply(this, MessageTypes.ANSWER, () -> {
					AgentTypes type = players.pop();
					return type;
				});

		config.addOperation(GameStates.GAME_START, AgentTypes.MODERATOR)
				.whenReceive(MessageTypes.OK, MessageTopics.WHO_I_AM).fromTypes(AgentTypes.PLAYERS)
				.afterMessagesNumber(playersNumber).then(x -> changeGameState(GameStates.FIRST_NIGHT))
				.afterMessagesNumber(playersNumber).thenSend(this,
						new Message(MessageTypes.INFORM, MessageTopics.FIRST_NIGHT).withReceivers(AgentTypes.WEREWOLF));

		config.addOperation(GameStates.FIRST_NIGHT, AgentTypes.MODERATOR)
				.whenReceive(MessageTypes.OK, MessageTopics.FIRST_NIGHT).fromTypes(AgentTypes.WEREWOLF)
				.afterMessagesNumber(werewolfesNumber).then(x -> changeGameState(GameStates.NIGHT))
				.afterMessagesNumber(werewolfesNumber).thenSend(this,
						new Message(MessageTypes.INFORM, MessageTopics.NIGHT).withReceivers(AgentTypes.WEREWOLF));

	};

}
