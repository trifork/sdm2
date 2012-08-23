package dk.nsi.sdm4.core.status;

import org.joda.time.DateTime;

public class ProgrammableTimeSource implements TimeSource {
	DateTime now = new DateTime();

	@Override
	public DateTime now() {
		return now;
	}
}
