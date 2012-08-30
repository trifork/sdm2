package dk.nsi.sdm4.testutils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.fail;

/**
 * Class meant to be used from junit-based tests.
 * Checks that a statuspage returned from the StatusReporter class in sdm4-core is running on
 * localhost 8080.
 * This class assumes the module is already deployed to an app server.
 */
public class StatuspageChecker {
	private static final int MAX_RETRIES = 10;

	public void assertThatStatuspageReturns200OK(String modulename) throws IOException, InterruptedException {
		int status = 0;
		String url = "http://localhost:8080/" + modulename + "/status";
		final URL u = new URL(url);
		for (int i = 0; i < MAX_RETRIES; i++) {
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.connect();
			status = connection.getResponseCode();
			connection.disconnect();

			if (status == 200) {
				return;
			}

			if (status != 404 && status != 503) {
				fail("Status page did not respond with HTTP code 200, status was " + status);
			}

			Thread.sleep(1000);
		}

		fail("Status page on " + url + " did not respond with HTTP code 200 after " + MAX_RETRIES + " retries, last status was " + status);
	}
}
