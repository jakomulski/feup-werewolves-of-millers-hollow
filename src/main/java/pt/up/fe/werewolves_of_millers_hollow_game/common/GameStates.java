package pt.up.fe.werewolves_of_millers_hollow_game.common;

public enum GameStates {
	GAME_START, FIRST_NIGHT, NIGHT, DAY;
	public static GameStates currentState = GAME_START;

	public static void setCurrentState(GameStates currentState) {
		GameStates.currentState = currentState;
	}

}
