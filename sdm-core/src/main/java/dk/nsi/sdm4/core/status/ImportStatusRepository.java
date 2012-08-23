package dk.nsi.sdm4.core.status;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Represents the import status and deadline information for a single parser
 */
public interface ImportStatusRepository {
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void importStartedAt(DateTime startTime);

	@Transactional(propagation = Propagation.MANDATORY)
	void importEndedAt(DateTime endTime, ImportStatus.Outcome outcome);

	ImportStatus getLatestStatus();

	boolean isOverdue();
}
