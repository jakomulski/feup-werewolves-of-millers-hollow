package pt.up.fe.werewolves_of_millers_hollow_game.example.dummy;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jade.lang.acl.UnreadableException;
import pt.up.fe.werewolves_of_millers_hollow_game.actions.Actions;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GenericWrapper;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Operation;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Strategy;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class DummyFortuneTellerStrategy implements Strategy {
	Player player;

	public DummyFortuneTellerStrategy(Player player) {
		this.player = player;
	}

	public void config(Function<GameStates, Operation> config) {
		dayConfig(() -> config.apply(GameStates.DAY));
		nightConfig(() -> config.apply(GameStates.NIGHT));
	}

	private void dayConfig(Supplier<Operation> config) {
		Random random = new Random();

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.DAY_START).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					try {
						String killedPlayer = (String) aclMsg.getContentObject();
						if (killedPlayer != null)
							player.players.remove(killedPlayer);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				});

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.DAY_END).fromTypes(AgentTypes.MODERATOR)
				.thenSend(player, new Message(MessageTypes.OK, MessageTopics.DAY_END, () -> {
					List<String> votes = player.players.entrySet().stream()
							.filter(e -> AgentTypes.WEREWOLF.equals(e.getValue())).map(e -> e.getKey())
							.collect(Collectors.toList());
					if (votes.isEmpty())
						votes = player.players.entrySet().stream().filter(e -> AgentTypes.PLAYER.equals(e.getValue()))
								.map(e -> e.getKey()).collect(Collectors.toList());
					if (votes.isEmpty())
						return "";
					return votes.get(random.nextInt(votes.size()));
				}).withReceivers(AgentTypes.ALL));
	}

	private void nightConfig(Supplier<Operation> config) {
		Random random = new Random();

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.NIGHT_START).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					try {
						String killedPlayer = (String) aclMsg.getContentObject();
						if (killedPlayer != null)
							player.players.remove(killedPlayer);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				});

		GenericWrapper<String> requestedName = new GenericWrapper<>();

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.NIGHT_END).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					List<String> notKnownPlayers = player.players.entrySet().stream()
							.filter(e -> e.getValue().equals(AgentTypes.PLAYER)).map(e -> e.getKey())
							.collect(Collectors.toList());
					if (notKnownPlayers.isEmpty()) {
						Actions.sendMessage(player, new Message(MessageTypes.OK, MessageTopics.NIGHT_END)
								.withReceivers(AgentTypes.MODERATOR)).accept(aclMsg);
					} else {
						String requestedNameValue = notKnownPlayers.get(random.nextInt(notKnownPlayers.size()));
						requestedName.set(requestedNameValue);
						Actions.sendMessage(player,
								new Message(MessageTypes.QUESTION, MessageTopics.WHO_IS_THAT, () -> requestedNameValue)
										.withReceivers(AgentTypes.MODERATOR))
								.accept(aclMsg);
					}
				});

		config.get().whenReceive(MessageTypes.ANSWER, MessageTopics.WHO_IS_THAT).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					try {
						AgentTypes type = (AgentTypes) aclMsg.getContentObject();
						player.players.put(requestedName.get(), type);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}).thenSend(player,
						new Message(MessageTypes.OK, MessageTopics.NIGHT_END).withReceivers(AgentTypes.MODERATOR));
	}
}
