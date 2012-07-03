package dk.nsi.integrationtest.util;

public class ServerUtils {
    public static String urlPrefix() {
        final String urlPrefix = System.getProperty("integrationtest.urlprefix");
        return urlPrefix != null ? urlPrefix : "http://localhost:8080";
    }
}
