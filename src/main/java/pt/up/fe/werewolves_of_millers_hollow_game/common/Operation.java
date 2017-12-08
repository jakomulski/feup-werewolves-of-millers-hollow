package pt.up.fe.werewolves_of_millers_hollow_game.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jade.lang.acl.ACLMessage;
import pt.up.fe.werewolves_of_millers_hollow_game.actions.Actions;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.BaseAgent;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.Message;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTopics;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.MessageTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.ReceiveMessage;

public class Operation {
	private final GameStates gameState;
	private final AgentTypes agentType;

	private ReceiveMessage onMessage;
	private Consumer<ACLMessage> action;
	private BaseAgent agent;

	public Operation(GameStates gameState, BaseAgent agent, AgentTypes agentType) {
		this.gameState = gameState;
		this.agentType = agentType;
		this.agent = agent;
	}

	public BaseAgent getAgent() {
		return agent;
	}

	public AgentTypes getAgentType() {
		return agentType;
	}

	public ReceiveMessage getExpectedMessage() {
		return onMessage;
	}

	public FromTypes whenReceive(MessageTypes messageType, MessageTopics messageTopic) {
		this.onMessage = new ReceiveMessage(messageType, messageTopic);
		return this::fromTypes;
	}

	public GameStates getGameState() {
		return gameState;
	}

	public Consumer<ACLMessage> getAction() {
		return action;
	}

	private ActionConsumer setAction(Consumer<ACLMessage> action) {
		this.action = action;
		return (consumer) -> {
			return setAction((x) -> {
				action.accept(x);
				consumer.accept(x);
			});
		};
	}

	private ActionConsumer fromTypes(AgentTypes... agentTypes) {
		this.onMessage.from(agentTypes);
		return this::setAction;
	}

	public interface FromTypes {
		ActionConsumer fromTypes(AgentTypes... agentTypes);
	}

	public interface ActionConsumer {
		default ActionConsumer fromAllPlayers(String... players) {
			Set<String> set = new HashSet<>(Arrays.asList(players));
			return (consumer) -> {
				then((aclMsg) -> {
					set.remove(aclMsg.getSender().getLocalName());
					if (set.isEmpty()) {
						consumer.accept(aclMsg);
					}
				});
				return this;
			};
		}

		default ActionConsumer afterMessagesNumber(Supplier<Integer> numberSupplier) {
			GenericWrapper<Integer> counter = new GenericWrapper<>();
			return consumer -> {
				return then((msg) -> {
					counter.setIfNull(numberSupplier.get());
					counter.set(counter.get() - 1);
					if (counter.get() == 0) {
						consumer.accept(msg);
						counter.set(null);
					}
				});
			};
		}

		default ActionConsumer thenSend(BaseAgent agent, Message message) {
			return then(Actions.sendMessage(agent, message));
		}

		default void thenReply(BaseAgent agent, MessageTypes messageType, Supplier<Serializable> messageContent) {
			then(Actions.replyToMessage(agent, messageType, messageContent));
		}

		ActionConsumer then(Consumer<ACLMessage> consumer);
	}
}
