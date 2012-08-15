package dk.nsi.sdm4.core.config;

import com.googlecode.flyway.core.Flyway;
import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.parser.ParserExecutor;
import dk.nsi.sdm4.core.util.Preconditions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
//@EnableTransactionManagement
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

    @SuppressWarnings("unchecked")
    public static String getHome(Class clazz) {
        Preconditions.checkNotNull(clazz, "Class");
        Preconditions.checkArgument(clazz.isAnnotationPresent(EnableStamdata.class), "Parsers must be annotated with @EnableStamdata.");

        EnableStamdata es = (EnableStamdata) clazz.getAnnotation(EnableStamdata.class);
        return es.home();
    }

    // this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
