package dk.nsi.sdm4.core.config;

import com.googlecode.flyway.core.Flyway;
import dk.nsi.sdm4.core.parser.ParserExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class StamdataConfiguration {
    @Bean
    public ParserExecutor parserExecutor() {
        return new ParserExecutor();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

    // this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(true);
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false);

        propertySourcesPlaceholderConfigurer.setLocations(new Resource[]{new ClassPathResource("default-config.properties"),new ClassPathResource("config.properties")});
        
        return propertySourcesPlaceholderConfigurer;
    }
}
