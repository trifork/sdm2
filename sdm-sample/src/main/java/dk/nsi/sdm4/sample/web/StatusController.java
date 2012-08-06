package dk.nsi.sdm4.sample.web;

import dk.nsi.sdm4.core.web.StatusControllerBase;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

public class StatusController extends StatusControllerBase {
    final File failFile = new File("/tmp/FailSimulator");

    @Override
    protected void doHealthCheck() {
        if (failFile.exists()) {
            throw new RuntimeException("File " + failFile.getAbsolutePath() + " exists, and therefore throwing exception");
        }
    }

    @RequestMapping("/failMode")
    @ResponseBody
    public String failMode(@RequestParam("enable") boolean enable) throws Exception {
        if (enable) {
            FileUtils.touch(failFile);
            return "Enabled";
        }
        else {
            System.out.println("failFile.delete() = " + failFile.delete());
            return "Disabled";
        }
    }
}
