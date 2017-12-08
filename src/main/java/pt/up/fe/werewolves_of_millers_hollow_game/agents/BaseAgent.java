package pt.up.fe.werewolves_of_millers_hollow_game.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import pt.up.fe.werewolves_of_millers_hollow_game.common.AgentTypes;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;
import pt.up.fe.werewolves_of_millers_hollow_game.common.Operation;
import pt.up.fe.werewolves_of_millers_hollow_game.engine.GameSpecyfication;

public abstract class BaseAgent extends Agent {
	protected Behaviour currentBehaviour;
	public AgentTypes myType = AgentTypes.PLAYER;
	public final List<Operation> operations = new ArrayList<Operation>();
	public GameSpecyfication gameSpecyfication;
	public final Map<String, AgentTypes> players = new HashMap<>();

	public DFAgentDescription createDFAgentDescriptionWithType(String type) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(this.getLocalName());
		sd.setType(type);
		dfd.addServices(sd);

		return dfd;
	}

	protected abstract Behaviour getBahaviour();

	protected abstract void config(Configuration config);

	protected void setup() {
		gameSpecyfication = (GameSpecyfication) this.getArguments()[0];
		this.config(this::addOperation);
		currentBehaviour = getBahaviour();
		this.addBehaviour(currentBehaviour);

	}

	private Operation addOperation(GameStates gameState, AgentTypes agentType) {
		Operation operation = new Operation(gameState, this, agentType);
		operations.add(operation);
		return operation;
	}

	protected interface Configuration {
		Operation addOperation(GameStates gameState, AgentTypes agentType);
	}
}
