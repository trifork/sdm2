package dk.nsi.sdm4.core.status;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface ImportStatusRepository {
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void importStartedAt(DateTime startTime);

	@Transactional(propagation = Propagation.MANDATORY)
	void importEndedAt(DateTime endTime, ImportStatus.Outcome outcome);

	ImportStatus getLatestStatus();
}
