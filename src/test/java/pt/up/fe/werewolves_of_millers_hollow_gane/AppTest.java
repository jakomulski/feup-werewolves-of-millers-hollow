package pt.up.fe.werewolves_of_millers_hollow_gane;

import java.util.function.Function;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */

	public void testApp() {
		Function<String, Runnable> run = dupa -> {
			final Wrapper counter = new Wrapper();
			return () -> {
				counter.increment();
			};
		};

		assertTrue(true);
	}

	public Function<String, String> parse1(Function<String, String> function) {
		return function;
	}

	public Function<String, String> parse2(Function<String, String> function) {
		return s -> {
			return function.apply(s);
		};
	}

	class Wrapper {
		int value = 0;

		void increment() {
			value++;
		}

		int getValue() {
			return value;
		}
	}
}
