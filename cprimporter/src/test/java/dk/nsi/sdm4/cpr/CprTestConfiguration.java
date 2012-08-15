package dk.nsi.sdm4.cpr;


import com.googlecode.flyway.core.Flyway;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import dk.sdsd.nsp.slalog.api.SLALogger;
import dk.sdsd.nsp.slalog.impl.SLALoggerDummyImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class CprTestConfiguration {
	@Value("${test.mysql.port}")
	private int mysqlPort;

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties(){
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource() throws Exception{
		MysqlDataSource ds = new MysqlDataSource();

		ds.setDatabaseName("sdm_warehouse");
		ds.setCreateDatabaseIfNotExist(true);

		ds.setServerName("127.0.0.1");
		ds.setPortNumber(mysqlPort);

		ds.setUser("root");
		ds.setPassword("papkasse");

		return ds;
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
