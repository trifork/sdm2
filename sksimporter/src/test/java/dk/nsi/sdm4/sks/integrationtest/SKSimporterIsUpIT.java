package dk.nsi.sdm4.sks.integrationtest;

import dk.nsi.sdm4.testutils.StatuspageChecker;
import org.junit.Test;

public class SKSimporterIsUpIT {
	@Test
	public void statusPageReturns200OK() throws Exception {
		new StatuspageChecker().assertThatStatuspageReturns200OK("sksimporter");
	}
}
