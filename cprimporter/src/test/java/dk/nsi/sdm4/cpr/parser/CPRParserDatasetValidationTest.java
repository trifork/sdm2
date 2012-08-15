package dk.nsi.sdm4.cpr.parser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CPRParserDatasetValidationTest {
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	private CPRParser parser;

	@Before
	public void instatiateParser() {
		parser = new CPRParser();
	}

	@Test()
	public void shouldComplainIfPassedNull() throws IOException {
		try {
			parser.process(null);
		} catch (NullPointerException expectedException) {
			assertTrue(expectedException.getMessage().contains("dataset"));
			return;
		}

		fail("Expected NullPointerException");
	}

	@Test()
	public void shouldComplainIfPassedFileInsteadOfDirectory() throws IOException {
		File aFile = tmpDir.newFile();
		try {
			parser.process(aFile);
		} catch (IllegalStateException expectedException) {
			assertTrue(expectedException.getMessage().contains("not a directory"));
			assertTrue("Error message contains path to file", expectedException.getMessage().contains(aFile.getAbsolutePath()));
			return;
		}

		fail("Expected IllegalStateException");
	}

	@Test()
	public void shouldComplainIfPassedUnreadableDirectory() throws IOException {
		File aDirectory = tmpDir.newFolder();
		aDirectory.setReadable(false);
		try {
			parser.process(aDirectory);
		} catch (IllegalStateException expectedException) {
			assertTrue(expectedException.getMessage().contains("not readable"));
			assertTrue("Error message contains path to directory", expectedException.getMessage().contains(aDirectory.getAbsolutePath()));
			return;
		}

		fail("Expected IllegalStateException");
	}

}
