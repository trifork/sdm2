package dk.nsi.sdm4.core.status;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.status.ImportStatus;
import dk.nsi.sdm4.core.status.ImportStatusRepositoryJdbcImpl;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.File;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ImportStatusRepositoryJdbcImplTest {
	@Configuration
	@PropertySource("classpath:test.properties")
	static class ContextConfiguration {
		@Bean
		public ImportStatusRepositoryJdbcImpl repository() {
			return new ImportStatusRepositoryJdbcImpl();
		}

		@Bean
		public JdbcTemplate template() {
			return new JdbcTemplate(datasource());
		}

		@Bean
		public DataSource datasource() {
			return new EmbeddedDatabaseBuilder()
					.addScript("ImportStatusRepositoryJdbcImplTest-schema.sql")
					.build();
		}

		@Bean
		public Parser fakeParser() {
			return new Parser() {
				@Override
				public void process(File dataSet) throws ParserException {
					throw new UnsupportedOperationException("process");
				}

				@Override
				public String getHome() {
					return "fakeParser";
				}
			};
		}

		@Bean
		public PlatformTransactionManager transactionManager(DataSource ds) {
			return new DataSourceTransactionManager(ds);
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer properties(){
			return new PropertySourcesPlaceholderConfigurer();
		}
	}

	@Autowired
	private ImportStatusRepositoryJdbcImpl repository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void returnsNoStatusWhenTableIsEmpty() {
		assertNull(repository.getLatestStatus());
	}

	@Test
	public void returnsOpenStatusWhenOnlyOneOpenStatusInDb() {
		DateTime startTime = new DateTime();
		repository.importStartedAt(startTime);

		ImportStatus latestStatus = repository.getLatestStatus();
		assertNotNull(latestStatus);
		assertEquals(startTime, latestStatus.getStartTime());
	}

	@Test
	public void callingEndedAtWithAnEmptyDatabaseDoesNothing() {
		// this can happen in the ParserExecutor, if some exception occurs before we reach the call to importStartedAt
		repository.importEndedAt(new DateTime(), ImportStatus.Outcome.FAILURE);
		assertNull(repository.getLatestStatus());
	}

	@Test
	public void returnsClosedStatusWhenOnlyOneStatusInDb() {
		ImportStatus expectedStatus = insertStatusInDb(ImportStatus.Outcome.SUCCESS);

		assertEquals(expectedStatus, repository.getLatestStatus());
	}

	@Test
	public void returnsSuccesStatusFromDb() {
		insertStatusInDb(ImportStatus.Outcome.SUCCESS);
		assertEquals(ImportStatus.Outcome.SUCCESS, repository.getLatestStatus().getOutcome());
	}

	@Test
	public void returnsErrorStatusFromDb() {
		insertStatusInDb(ImportStatus.Outcome.FAILURE);
		assertEquals(ImportStatus.Outcome.FAILURE, repository.getLatestStatus().getOutcome());
	}

	@Test
	public void openStatusHasNoOutcome() {
		DateTime startTime = new DateTime();
		repository.importStartedAt(startTime);
		assertNull(repository.getLatestStatus().getOutcome());
	}

	@Test
	public void returnsLatestStatusWhenTwoClosedStatusesExistsInDb() throws InterruptedException {
		insertStatusInDb(ImportStatus.Outcome.SUCCESS);
		Thread.sleep(1); // to avoid the next status having the exact same startTime as the one just inserted
		ImportStatus expectedStatus = insertStatusInDb(ImportStatus.Outcome.FAILURE);

		ImportStatus latestStatus = repository.getLatestStatus();
		assertEquals(expectedStatus, latestStatus);
	}

	@Test
	public void whenTwoOpenStatusesExistsInDbEndingOnlyUpdatesTheLatest() {
		DateTime startTimeOldest = new DateTime();
		repository.importStartedAt(startTimeOldest);
		// The reason for this not being closed would be some kind of program error or outage

		DateTime startTimeNewest = new DateTime();
		repository.importStartedAt(startTimeNewest);

		repository.importEndedAt(new DateTime(), ImportStatus.Outcome.FAILURE);

		// check that the newest was closed
		ImportStatus dbStatus = repository.getLatestStatus();
		assertEquals(startTimeNewest, dbStatus.getStartTime());
		assertNotNull(dbStatus.getEndTime());

		// check that some open status exists (which we can then conclude must be the oldest of the two test statuses)
		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) from fakeParserImportStatus WHERE EndTime IS NULL"));
	}

	@Test
	public void jobIsNotOverdueWhenItHasNotRun() {
		assertFalse(repository.isOverdue());
	}

	@Test
	public void jobIsNotOverdueWhenItHasJustRunWithSucces() {
		insertStatusInDb(ImportStatus.Outcome.SUCCESS);
		assertFalse(repository.isOverdue());
	}

	@Test
	public void jobIsNotOverdueWhenItHasJustRunWithError() {
		insertStatusInDb(ImportStatus.Outcome.FAILURE);
		assertFalse(repository.isOverdue());
	}

	private ImportStatus insertStatusInDb(ImportStatus.Outcome outcome) {
		DateTime startTime = new DateTime();
		repository.importStartedAt(startTime);
		DateTime endTime = new DateTime();
		repository.importEndedAt(endTime, outcome);
		ImportStatus expectedStatus = new ImportStatus();
		expectedStatus.setStartTime(startTime);
		expectedStatus.setEndTime(endTime);
		expectedStatus.setOutcome(outcome);
		return expectedStatus;
	}
}