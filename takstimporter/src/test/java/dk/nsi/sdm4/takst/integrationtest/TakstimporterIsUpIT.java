package dk.nsi.sdm4.takst.integrationtest;

import dk.nsi.sdm4.testutils.StatuspageChecker;
import org.junit.Test;

public class TakstimporterIsUpIT {
	@Test
	public void statusPageReturns200OK() throws Exception {
		new StatuspageChecker().assertThatStatuspageReturns200OK("takstimporter");
	}
}
