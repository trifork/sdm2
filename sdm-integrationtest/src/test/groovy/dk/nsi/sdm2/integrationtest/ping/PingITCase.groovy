package dk.nsi.sdm2.integrationtest.ping

import org.junit.Test
import static org.junit.Assert.*

class PingITCase {

    @Test
    def void willAnswer200OK() throws Exception {
        HttpURLConnection urlConnection = new URL("http://localhost:8080/status/").openConnection()
        assertEquals 200, urlConnection.responseCode
        assertEquals "OK", urlConnection.inputStream.getText()
    }
}
