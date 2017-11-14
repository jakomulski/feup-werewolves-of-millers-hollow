package pt.up.fe.werewolves_of_millers_hollow_game.behaviours;

import java.util.List;
import java.util.function.Supplier;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import pt.up.fe.werewolves_of_millers_hollow_game.agents.BaseAgent;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Operation;
import pt.up.fe.werewolves_of_millers_hollow_game.messages.ReceiveMessage;

public abstract class BaseBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = 5136651404771610780L;

	private List<Operation> operations;
	protected BaseAgent myAgent;

	public BaseBehaviour(BaseAgent agent, AgentTypes agentType, Supplier<List<Operation>> operations) {
		super(agent);
		myAgent = agent;
		agent.myType = agentType;
		this.operations = operations.get();
	}

	@Override
	public void action() {
		ACLMessage receivedAclMsg = this.myAgent.receive();
		if (receivedAclMsg != null) {
			operations.forEach(operation -> {
				if (messageReceiveConditions(operation, receivedAclMsg))
					operation.getAction().accept(receivedAclMsg);
			});
		} else
			block();
	}

	private boolean messageReceiveConditions(Operation operation, ACLMessage received) {
		ReceiveMessage expected = operation.getExpectedMessage();

		boolean stateIsCorrect = GameStates.currentState.equals(operation.getGameState());
		boolean messageTypeIsCorrect = received.getPerformative() == expected.getMessageType().ordinal();
		boolean senderIsNotReceiver = !received.getSender().equals(operation.getAgent().getAID());
		boolean senderHasSpecefiedType = expected.getPlayerTypes()
				.contains(AgentTypes.valueOf(received.getUserDefinedParameter("type")));
		boolean topicIsCorrect = expected.getMessageTopic().name().equals(received.getUserDefinedParameter("topic"));
		return senderIsNotReceiver && stateIsCorrect && messageTypeIsCorrect && topicIsCorrect
				&& senderHasSpecefiedType;
	}

	@Override
	public boolean done() {
		return false;
	}
}
