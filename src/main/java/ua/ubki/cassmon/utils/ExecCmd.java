package ua.ubki.cassmon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecCmd {
    private static final Logger logger = LoggerFactory.getLogger(ExecCmd.class);

    private ExecCmd() {
        //
    }

    public static ExecCmdResultDto exec(String cmd) {
        return exec(cmd, false);
    }

    public static ExecCmdResultDto exec(String cmd, boolean woWaitResult) {
        logger.info("Exec command: {}", cmd);

        long t = System.currentTimeMillis();

        ExecCmdResultDto result = new ExecCmdResultDto();

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);

            if (!woWaitResult) {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line;
                StringBuilder commandOutput = new StringBuilder();
                StringBuilder errorOutput = new StringBuilder();

                while ((line = stdInput.readLine()) != null) {
                    commandOutput.append(line).append("\n");
                }
                result.setCommandOutput(commandOutput.toString());

                while ((line = stdError.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                result.setErrorOutput(errorOutput.toString());

                stdInput.close();
                stdError.close();
            }
        } catch (IOException e) {
            result.setErrorMessage(e.getMessage());
            logger.error("Execute command error", e);
        }

        result.setDuration(System.currentTimeMillis() - t);

        return result;
    }

}
