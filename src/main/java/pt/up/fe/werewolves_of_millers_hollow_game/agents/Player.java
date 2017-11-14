package pt.up.fe.werewolves_of_millers_hollow_game.agents;

import jade.core.behaviours.Behaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.behaviours.PlayerBehaviour;

public abstract class Player extends BaseAgent {
	@Override
	protected final Behaviour getBahaviour() {
		return new PlayerBehaviour(this);
	}
}
