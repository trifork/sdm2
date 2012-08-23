package dk.nsi.sdm4.core.status;

import org.joda.time.DateTime;

public class TimeSourceRealTimeImpl implements TimeSource {
	@Override
	public DateTime now() {
		return new DateTime();
	}
}
