package dk.nsi.sdm4.core.persistence;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ImportStatusRepositoryJdbcImplTest {
	@Configuration
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
			return new EmbeddedDatabaseBuilder().build();
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
	}

	@Autowired
	private ImportStatusRepositoryJdbcImpl repository;

	@Test
	public void returnsNoStatusWhenTableIsEmpty() {
		assertNull(repository.getLatestStatus());
	}
}