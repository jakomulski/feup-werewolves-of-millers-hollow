package pt.up.fe.werewolves_of_millers_hollow_game.example.smart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jade.lang.acl.UnreadableException;
import pt.up.fe.werewolves_of_millers_hollow_game.actions.Actions;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Player;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Operation;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Strategy;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class SmartVillagerStrategy implements Strategy {
	Player player;

	private List<String> playersToKillFirst = new ArrayList<>();

	public SmartVillagerStrategy(Player player) {
		this.player = player;
	}

	Random random = new Random();

	public void config(Function<GameStates, Operation> config) {
		dayConfig(() -> config.apply(GameStates.DAY));
		nightConfig(() -> config.apply(GameStates.NIGHT));
	}

	private void dayConfig(Supplier<Operation> config) {

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.I_AM_FORTUNE_TELLER)
				.fromTypes(AgentTypes.FORTUNE_TELLER).then(aclMsg -> {
					try {
						player.players.put(aclMsg.getSender().getLocalName(), AgentTypes.FORTUNE_TELLER);
						Entry<String, AgentTypes> info = (Entry<String, AgentTypes>) aclMsg.getContentObject();
						if (info.getKey() != player.getLocalName())
							player.players.put(info.getKey(), info.getValue());
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.DAY_START).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					try {
						String killedPlayer = (String) aclMsg.getContentObject();
						if (killedPlayer != null) {
							player.players.remove(killedPlayer);
							playersToKillFirst.remove(killedPlayer);
						}

					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				});

		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.DAY_END).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					String vote = getVote();
					Actions.sendMessage(player, new Message(MessageTypes.OK, MessageTopics.DAY_END, () -> vote)
							.withReceivers(AgentTypes.ALL)).accept(aclMsg);
				});

		config.get().whenReceive(MessageTypes.OK, MessageTopics.DAY_END).fromTypes(AgentTypes.PLAYERS).then(aclMsg -> {
			try {
				String vote = (String) aclMsg.getContentObject();
				if (vote.equals(this.player.getLocalName())) {
					if (null == player.players.get(vote)) {
						String playerToKill = (String) aclMsg.getSender().getLocalName();
						playersToKillFirst.add(playerToKill);
					}
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		});

	}

	private String getVote() {
		List<String> werewolfes = player.players.entrySet().stream()
				.filter(entry -> AgentTypes.WEREWOLF.equals(entry.getValue())).map(e -> e.getKey())
				.collect(Collectors.toList());

		List<String> notKnowPlayers = player.players.entrySet().stream()
				.filter(e -> AgentTypes.PLAYER.equals(e.getValue())).map(e -> e.getKey()).collect(Collectors.toList());

		if (!werewolfes.isEmpty())
			return werewolfes.get(random.nextInt(werewolfes.size()));
		else if (!playersToKillFirst.isEmpty())
			return playersToKillFirst.get(random.nextInt(playersToKillFirst.size()));
		else if (!notKnowPlayers.isEmpty())
			return notKnowPlayers.get(random.nextInt(notKnowPlayers.size()));
		return "";
	}

	private void nightConfig(Supplier<Operation> config) {
		config.get().whenReceive(MessageTypes.INFORM, MessageTopics.NIGHT_START).fromTypes(AgentTypes.MODERATOR)
				.then(aclMsg -> {
					try {
						String killedPlayer = (String) aclMsg.getContentObject();
						if (killedPlayer != null) {
							player.players.remove(killedPlayer);
							playersToKillFirst.remove(killedPlayer);
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				});

	}
}
