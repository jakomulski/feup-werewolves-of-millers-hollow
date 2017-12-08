package pt.up.fe.werewolves_of_millers_hollow_game.messages;

public enum MessageTypes {
	ANSWER("!"), QUESTION("?"), INFORM("."), OK(". I am ready.");
	private String text;

	public String getText() {
		return text;
	}

	private MessageTypes(String text) {
		this.text = text;
	}
}
