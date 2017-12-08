package pt.up.fe.werewolves_of_millers_hollow_game.example.dummy;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jade.lang.acl.UnreadableException;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Operation;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Strategy;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class DummyVillagerStrategy implements Strategy {
	Player player;

	public DummyVillagerStrategy(Player player) {
		this.player = player;
	}

	public void config(Function<GameStates, Operation> config) {
		dayConfig(() -> config.apply(GameStates.DAY));
		nightConfig(() -> config.apply(GameStates.DAY));
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
					List<String> votes = player.players.entrySet().stream().map(e -> e.getKey())
							.collect(Collectors.toList());
					if (votes.isEmpty())
						return "";
					return votes.get(random.nextInt(votes.size() - 1));
				}).withReceivers(AgentTypes.ALL));
		// .thenReply(player, MessageTypes.OK, () -> {
		//
		// });
	}

	private void nightConfig(Supplier<Operation> config) {
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

	}
}
