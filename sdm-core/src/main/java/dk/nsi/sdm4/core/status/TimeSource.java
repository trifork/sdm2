package dk.nsi.sdm4.core.status;

import org.joda.time.DateTime;

import javax.xml.crypto.Data;

public interface TimeSource {
	DateTime now();
}
