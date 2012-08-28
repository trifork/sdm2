package dk.nsi.sdm4.plugin.vagrant;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;

/**
 * @goal generate-jboss-descriptors
 * @phase prepare-package
 */
public class JbossMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}/WEB-INF"
	 */
	private File outputDir;

	/**
	 * @parameter default-value="${project.artifactId}"
	 */
	private String modulename;

	/**
	 * @parameter default-value="${project.packaging}"
	 */
	private String packaging;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Detected packaging: " + packaging + " for module " + modulename);
		if ("war".equals(packaging)) {
			writeTemplateToFile("jboss-web.xml");
			writeTemplateToFile("jboss-deployment-structure.xml");
		} else {
			getLog().debug("Packaging is not war, skip module");
		}
	}

	private void writeTemplateToFile(String name) throws MojoExecutionException {
		String templatedContent = getTemplate(name);
		writeToOutputFile(templatedContent, name);
		getLog().info("Wrote " + new File(outputDir, name).getAbsolutePath());
	}

	private void writeToOutputFile(String content, String filename) throws MojoExecutionException {
		File outfile = null;
		try {
			outfile = new File(outputDir, filename);
			FileUtils.write(outfile, content);
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot write jboss-web.xml to " + outfile, e);
		}
	}

	private String getTemplate(String source) throws MojoExecutionException {
		try {
			StringWriter outstream = new StringWriter();
			IOUtils.copy(this.getClass().getResourceAsStream("/" + source), outstream);
			return outstream.getBuffer().toString().replace("${modulename}", modulename);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to read jboss-web.xml from classpath", e);
		}
	}
}