package dk.nsi.sdm4.sor.integrationtest;

import dk.nsi.sdm4.testutils.StatuspageChecker;
import org.junit.Test;

public class SorrelationimporterIsUpIT {
	@Test
	public void statusPageReturns200OK() throws Exception {
		new StatuspageChecker().assertThatStatuspageReturns200OK("sorrelationimporter");
	}
}
