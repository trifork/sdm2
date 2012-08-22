package dk.nsi.sdm4.core.persistence;

import dk.nsi.sdm4.core.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;

@Repository
public class ImportStatusRepositoryJdbcImpl implements ImportStatusRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Parser parser;
	private String statusTableName;

	@PostConstruct
	private void constructTableNameFromParser() {
		statusTableName = parser.getHome() + "ImportStatus";
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void importStartedAt(Date startTime) {

	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void importEndedAt(Date endTime) {

	}

	@Override
	public ImportStatus getLatestStatus() {
		return null;
	}
}
