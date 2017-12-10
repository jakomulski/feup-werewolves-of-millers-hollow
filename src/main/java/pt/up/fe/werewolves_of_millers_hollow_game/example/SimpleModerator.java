package pt.up.fe.werewolves_of_millers_hollow_game.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import pt.up.fe.werewolves_of_millers_hollow_game.actions.Actions;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.Moderator;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Operation;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;

public class SimpleModerator extends Moderator {
	private static final long serialVersionUID = 6116674073712848165L;

	@Override
	public void config(Configuration config) {
		gameStartConfig(() -> config.addOperation(GameStates.GAME_START, AgentTypes.MODERATOR));
		firstNightConfig(() -> config.addOperation(GameStates.FIRST_NIGHT, AgentTypes.MODERATOR));
		dayConfig(() -> config.addOperation(GameStates.DAY, AgentTypes.MODERATOR));
		nightConfig(() -> config.addOperation(GameStates.NIGHT, AgentTypes.MODERATOR));
	};

	Integer getWerewolfesAndFortuneTellerNumber() {
		return (int) this.players.values().stream()
				.filter(v -> v.equals(AgentTypes.WEREWOLF) || v.equals(AgentTypes.FORTUNE_TELLER)).count();
	}

	Integer getWerewolfesNumber() {
		return (int) this.players.values().stream().filter(v -> v.equals(AgentTypes.WEREWOLF)).count();
	}

	Integer getNotWerewolfesNumber() {
		return (int) this.players.values().stream().filter(v -> !v.equals(AgentTypes.WEREWOLF)).count();
	}

	private void gameStartConfig(Supplier<Operation> config) {
		int playersNumber = this.gameSpecyfication.getPlayerTypesNumbers().values().stream().mapToInt(i -> i).sum();

		config.get().whenReceive(MessageTypes.OK, MessageTopics.INIT).fromTypes(AgentTypes.PLAYERS)
				.afterMessagesNumber(() -> playersNumber).then(aclMsg -> {
					changeGameState(GameStates.FIRST_NIGHT);
					Actions.sendMessage(this, new Message(MessageTypes.INFORM, MessageTopics.FIRST_NIGHT)
							.withReceivers(AgentTypes.PLAYERS)).accept(aclMsg);
				});
	};

	private void firstNightConfig(Supplier<Operation> config) {
		config.get().whenReceive(MessageTypes.OK, MessageTopics.FIRST_NIGHT).fromTypes(AgentTypes.WEREWOLF)
				.afterMessagesNumber(this::getWerewolfesNumber).then(aclMsg -> {
					nightAction(aclMsg, null);
				});
	};

	private void dayConfig(Supplier<Operation> config) {
		Map<String, AtomicInteger> votes = new HashMap<>();
		Supplier<Optional<String>> highestVote = () -> {
			try {
				Optional<Entry<String, AtomicInteger>> maxEnity = votes.entrySet().stream()
						.max((a, b) -> new Integer(a.getValue().get()).compareTo(b.getValue().get()));

				Optional<AtomicInteger> maxValue = maxEnity.map(opt -> opt.getValue());
				Optional<String> maxVote = maxEnity.map(opt -> opt.getKey());

				long numOfMax = votes.entrySet().stream().filter(e -> e.getValue().get() == maxValue.get().get())
						.count();

				if (numOfMax == 1)
					return maxVote;
				else
					return Optional.empty();
			} finally {
				votes.clear();
			}
		};

		AtomicInteger counter = new AtomicInteger(0);
		config.get().whenReceive(MessageTypes.OK, MessageTopics.DAY_END).fromTypes(AgentTypes.PLAYERS).then(aclMsg -> {
			try {
				String vote = (String) aclMsg.getContentObject();
				if (vote != null) {
					if (!votes.containsKey(vote))
						votes.put(vote, new AtomicInteger(0));
					votes.get(vote).incrementAndGet();
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}).afterMessagesNumber(players::size).then(aclMsg -> {
			counter.set((counter.get() + 1) % 4);

			Optional<String> highestVoteValue = highestVote.get();
			String vote = highestVoteValue.orElse(null);

			this.doWait(500);
			if (vote == null && counter.get() != 3) {
				dayAction(aclMsg, null);
			} else {
				this.players.remove(vote);
				nightAction(aclMsg, vote);
			}
		});

	}

	private void nightConfig(Supplier<Operation> config) {
		Map<String, AtomicInteger> votes = new HashMap<>();
		Supplier<Optional<String>> highestVote = () -> {
			try {
				return votes.entrySet().stream()
						.max((a, b) -> new Integer(a.getValue().get()).compareTo(b.getValue().get()))
						.map(opt -> opt.getKey());
			} finally {
				votes.clear();
			}
		};
		config.get().whenReceive(MessageTypes.OK, MessageTopics.NIGHT_END)
				.fromTypes(AgentTypes.WEREWOLF, AgentTypes.FORTUNE_TELLER).then(aclMsg -> {
					try {
						String vote = (String) aclMsg.getContentObject();
						if (vote != null) {
							if (!votes.containsKey(vote))
								votes.put(vote, new AtomicInteger(0));
							votes.get(vote).incrementAndGet();
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}).afterMessagesNumber(this::getWerewolfesAndFortuneTellerNumber).then(aclMsg -> {
					Optional<String> highestVoteValue = highestVote.get();
					String vote = highestVoteValue.orElse(null);
					this.players.remove(vote);

					dayAction(aclMsg, vote);

				});
		config.get().whenReceive(MessageTypes.QUESTION, MessageTopics.WHO_IS_THAT).fromTypes(AgentTypes.FORTUNE_TELLER)
				.then(aclMsg -> {
					try {
						String name = (String) aclMsg.getContentObject();
						Actions.replyToMessage(this, MessageTypes.ANSWER, () -> this.players.get(name)).accept(aclMsg);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}

				});
	};

	private void dayAction(ACLMessage aclMsg, String playerToKill) {
		gameEndCheck();
		changeGameState(GameStates.DAY);
		Actions.sendMessage(this, new Message(MessageTypes.INFORM, MessageTopics.DAY_START, () -> playerToKill)
				.withReceivers(AgentTypes.PLAYERS)).accept(aclMsg);

		this.doWait(1000);
		Actions.sendMessage(this,
				new Message(MessageTypes.INFORM, MessageTopics.DAY_END).withReceivers(AgentTypes.PLAYERS))
				.accept(aclMsg);

	}

	private void nightAction(ACLMessage aclMsg, String playerToKill) {
		gameEndCheck();
		changeGameState(GameStates.NIGHT);
		Actions.sendMessage(this, new Message(MessageTypes.INFORM, MessageTopics.NIGHT_START, () -> playerToKill)
				.withReceivers(AgentTypes.PLAYERS)).accept(aclMsg);
		// this.doWait(50);
		Actions.sendMessage(this,
				new Message(MessageTypes.INFORM, MessageTopics.NIGHT_END).withReceivers(AgentTypes.PLAYERS))
				.accept(aclMsg);

	}

	private void gameEndCheck() {
		int werewolfesNumber = getWerewolfesNumber();

		if (werewolfesNumber == 0) {
			System.out.println("---- Werewolfes lost! ----");
			this.doDelete();
			System.exit(0);

		}

		if (getNotWerewolfesNumber() == 0) {
			System.out.println(("---- Werewolfes win! ----"));
			this.doDelete();
			System.exit(0);
		}

	}

}
