package dk.nsi.sdm4.sikrede.integrationtest;

import org.junit.Test;

public class SikredeimporterIsUpIT {
	@Test
	public void statusPageReturns200OK() throws Exception {
		new StatuspageChecker().assertThatStatuspageReturns200OK("sikredeimporter");
	}
}
