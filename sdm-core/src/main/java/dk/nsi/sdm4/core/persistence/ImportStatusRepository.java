package dk.nsi.sdm4.core.persistence;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface ImportStatusRepository {
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void importStartedAt(Date startTime);

	@Transactional(propagation = Propagation.MANDATORY)
	void importEndedAt(Date endTime);

	ImportStatus getLatestStatus();
}
