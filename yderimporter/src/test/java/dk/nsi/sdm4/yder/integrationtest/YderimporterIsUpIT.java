package dk.nsi.sdm4.yder.integrationtest;

import org.junit.Test;

public class YderimporterIsUpIT {
	@Test
	public void statusPageReturns200OK() throws Exception {
		new StatuspageChecker().assertThatStatuspageReturns200OK("yderimporter");
	}
}
