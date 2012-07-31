import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session

class DeploymentCommands {

    public static void main(String[] args) {
        copyWarToJboss("localhost", 2222)
    }

    public static void copyWarToJboss(String server, int port) {
        JSch jsch = new JSch()

        Session session = jsch.getSession("jboss", server, port)
        session.setConfig("StrictHostKeyChecking", "no")
        session.password = "bosshy"
        session.getConfig("test")
        session.connect()

        ChannelExec channel = session.openChannel("exec")

        channel.inputStream = null

        channel.errStream = System.err

        channel.command = "/bin/mv /pack/jboss/sdm-sample.war /pack/jboss/standalone/deployments/sdm-sample.war"
        InputStream stream = channel.inputStream
        channel.connect()

        byte[] tmp = new byte[1024];
        while (true) {
            while (stream.available() > 0) {
                if (stream.read(tmp, 0, 1024) < 0) break;
            }
            if (channel.isClosed()) {
                if (channel.exitStatus != 0) {
                    System.out.println("exit-status: " + channel.getExitStatus())
                };
                break;
            }
            Thread.sleep(1000)
        }
        channel.disconnect()
        session.disconnect()
    }
}
