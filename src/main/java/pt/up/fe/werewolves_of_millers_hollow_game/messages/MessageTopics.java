package pt.up.fe.werewolves_of_millers_hollow_game.messages;

public enum MessageTopics {
	INIT("The game has started"), HELLO("Hello. I am"), WHO_I_AM("I am"), FIRST_NIGHT("first night"), NIGHT_START(
			"It's night. Killed was"), NIGHT_END("Vote"), DAY_START("It's day. Killed was"), DAY_END(
					"Vote"), WHO_IS_THAT("Type of"), I_AM_FORTUNE_TELLER("I_AM_FORTUNE_TELLER");

	private String text;

	public String getText() {
		return text;
	}

	private MessageTopics(String text) {
		this.text = text;
	}
}
