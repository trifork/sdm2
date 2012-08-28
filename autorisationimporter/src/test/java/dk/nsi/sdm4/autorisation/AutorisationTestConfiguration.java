package dk.nsi.sdm4.autorisation;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.googlecode.flyway.core.Flyway;
import com.mysql.jdbc.Driver;

import dk.sdsd.nsp.slalog.api.SLALogger;
import dk.sdsd.nsp.slalog.impl.SLALoggerDummyImpl;

@Configuration
public class AutorisationTestConfiguration {
	@Value("${test.mysql.port}")
	private int mysqlPort;
	private String testDbName = "sdm_warehouse_cpr_test";
	private String db_username = "root";
	private String db_password = "papkasse";

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties(){
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource() throws Exception{
		String jdbcUrlPrefix = "jdbc:mysql://127.0.0.1:" + mysqlPort + "/";

		// we need to make sure we have an empty database. We do it by dropping and creating it, but we can't do that
		// from a datasource connected to the database we want to drop/create, so lets's connect to the mysql db first
		long startMillis = System.currentTimeMillis();
		DataSource adminDs = new SimpleDriverDataSource(new Driver(), jdbcUrlPrefix + "mysql", db_username, db_password);
		JdbcTemplate adminJdbc = new JdbcTemplate(adminDs);
		adminJdbc.update("DROP DATABASE IF EXISTS " + testDbName); // will be created automatically by the "normal" datasource
		System.out.println("Drop db took " + (System.currentTimeMillis()-startMillis) + " ms");

		return new SimpleDriverDataSource(new Driver(), jdbcUrlPrefix + testDbName + "?createDatabaseIfNotExist=true", db_username, db_password);
	}

	@Bean(initMethod = "migrate")
	public Flyway flyway(DataSource dataSource) {
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		return flyway;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource ds) {
		return new DataSourceTransactionManager(ds);
	}

	@Bean
	public SLALogger slaLogger() {
		return new SLALoggerDummyImpl();
	}
}
