package pt.up.fe.werewolves_of_millers_hollow_game.agents;

import jade.core.behaviours.Behaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.behaviours.ModeratorBehaviour;
import pt.up.fe.werewolves_of_millers_hollow_game.common.GameStates;

public abstract class Moderator extends BaseAgent {
	@Override
	protected final Behaviour getBahaviour() {
		return new ModeratorBehaviour(this);
	}

	protected void changeGameState(GameStates gameState) {
		System.out.println("\n  ---- " + gameState + " ----\n" + this.players + "\n");
		GameStates.setCurrentState(gameState);
	}
}
