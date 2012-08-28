package dk.nsi.sdm4.cpr.integrationtest;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class CprimporterIsUpIT {
	@Test
	public void statusPageReturns200OK() throws IOException {
		URL u = new URL("http://localhost:8080/cprimporter/status");
		HttpURLConnection connection = (HttpURLConnection) u.openConnection();
		connection.connect();

		int status = connection.getResponseCode();
		assertEquals(200, status);
	}
}
