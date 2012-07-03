package dk.nsi.sdm2.integrationtest.ping

import org.junit.Test
import static org.junit.Assert.*
import static dk.nsi.integrationtest.util.ServerUtils.*

class PingITCase {

    @Test
    def void willAnswer200OK() throws Exception {
        HttpURLConnection urlConnection = new URL("${urlPrefix()}/status/").openConnection()
        assertEquals 200, urlConnection.responseCode
        assertEquals "text/plain", urlConnection.contentType
        assertEquals "OK", urlConnection.inputStream.getText()
    }
}
