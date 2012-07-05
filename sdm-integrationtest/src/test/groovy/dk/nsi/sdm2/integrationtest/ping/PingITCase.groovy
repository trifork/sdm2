package dk.nsi.sdm2.integrationtest.ping

import org.junit.Test
import static org.junit.Assert.*
import static dk.nsi.integrationtest.util.ServerUtils.*
import org.apache.commons.io.FileUtils
import org.junit.Ignore
import org.junit.Before

class PingITCase {

    @Test
    def void willAnswer200OK() throws Exception {
        HttpURLConnection urlConnection = new URL("${urlPrefix()}/status").openConnection() as HttpURLConnection
        println urlConnection.getURL()
        assertEquals 200, urlConnection.responseCode
        assertEquals "text/plain", urlConnection.contentType
        assertEquals "OK", urlConnection.inputStream.getText()
    }

    @Test
    def void willAnswer500ServerErrorOnFailSimulator() throws Exception {
        assertEquals "Enabled", new URL("${urlPrefix()}/failMode?enable=true").openStream().getText()

        HttpURLConnection urlConnection = new URL("${urlPrefix()}/status").openConnection() as HttpURLConnection
        assertEquals 500, urlConnection.responseCode
        assertEquals "text/plain", urlConnection.contentType
        assertEquals "File /tmp/FailSimulator exists, and therefore throwing exception", urlConnection.errorStream.getText()

        assertEquals "Disabled", new URL("${urlPrefix()}/failMode?enable=false").openStream().getText()
    }
}
