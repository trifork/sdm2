package dk.nsi.sdm4.sor.integrationtest;

import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.fail;

/**
 * This tests assumes a deployed war file running on a jboss on localhost:8080
 */
public class SorrelationimporterIsUpIT {
	private static final int MAX_RETRIES = 10;

	@Test
	public void statusPageReturns200OK() throws Exception {
		int status = 0;
		String url = "http://localhost:8080/sorrelationimporter/status";
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
				fail("Status page on " + url + " did not respond with HTTP code 200, status was " + status);
			}

			Thread.sleep(1000);
		}

		fail("Status page on " + url + " did not respond with HTTP code 200 after " + MAX_RETRIES + " retries, last status was " + status);
	}
}
