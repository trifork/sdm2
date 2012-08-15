package dk.nsi.sdm4.sample.parser;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.sample.config.SampleApplicationConfig;
import dk.nsi.sdm4.sample.dao.SampleDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class SampleParserTest {
	@Configuration
	static class ContextConfiguration  {
		private SampleApplicationConfig productionConfig = new SampleApplicationConfig();

		@Bean
		SampleDao dao() {
			return new SampleDao() {
				@Override
				public void createSamples(List<SampleRecord> samples) {
					// ignore, we just test that the infrastructure up to the parser calling this method works
				}
			};
		}

		@Bean
		public Unmarshaller jaxbMarshaller() {
			return productionConfig.jaxbMarshaller();
		}

		@Bean
		public Parser parser() {
			return productionConfig.parser();
		}
	}

	@Value("${templatePath:classpath:samples.xml}")
	Resource samplesXmlResource;

	@Inject
	SampleParser parser;

	@Test
	public void canParseSampleXml() throws IOException {
		parser.process(samplesXmlResource.getFile());
	}
}
