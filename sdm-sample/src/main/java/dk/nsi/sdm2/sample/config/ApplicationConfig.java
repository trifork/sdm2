package dk.nsi.sdm2.sample.config;

import com.googlecode.flyway.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {

    @Bean
    public DataSource dataSource() throws Exception{
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        factory.setJndiName("java:jdbc/MySQLDS");
        factory.afterPropertiesSet();
        return (DataSource) factory.getObject();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }
}
