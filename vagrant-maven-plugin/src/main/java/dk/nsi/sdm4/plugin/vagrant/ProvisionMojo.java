package dk.nsi.sdm4.plugin.vagrant;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;

/**
 * @goal provision
 * @phase pre-integration-test
 */
public class ProvisionMojo extends AbstractMojo {
    /**
     * @parameter
     *
     * @required
     */
    private File vagrantfile;

    /**
     * @parameter
     *
     * @required
     */
    private String[] nodes;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (nodes == null || nodes.length == 0) {
            nodes = new String[] { "" };
        }
        final File vagrantRoot = vagrantfile.getParentFile();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(vagrantRoot);

        getLog().info("vagrantfile: " + vagrantfile);
        for (String node : nodes) {
            doProvision(executor, node);
        }
    }

    private void doProvision(Executor executor, String node) throws MojoExecutionException {
        CommandLine cmd = CommandLine.parse("vagrant provision " + node);

        try {

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            executor.setStreamHandler(new PumpStreamHandler(outputStream));
            if (executor.execute(cmd) != 0) {
                throw new MojoExecutionException("Failed to provision node=" + node);
            }
            getLog().info(outputStream.toString());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to execute '" + cmd + "'", e);
        }
    }
}
