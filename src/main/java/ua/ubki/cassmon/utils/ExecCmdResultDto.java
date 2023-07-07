package ua.ubki.cassmon.utils;

import ua.ubki.cassmon.utils.json.JsonUtils;

public class ExecCmdResultDto {
    private String commandOutput = "";
    private String errorOutput = "";
    private String errorMessage = "";
    private long duration = 0;

    public boolean isOK() {
        return !commandOutput.isEmpty() && errorOutput.isEmpty() && errorMessage.isEmpty();
    }

    public String log() {
        return String.format("OutputMessage: '%s'. ErrorOutputMessage: '%s', ErrorMessage: '%s'",
                commandOutput, errorOutput, errorMessage);
    }

    public String getCommandOutput() {
        return commandOutput;
    }

    public ExecCmdResultDto setCommandOutput(String commandOutput) {
        this.commandOutput = commandOutput;
        return this;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public ExecCmdResultDto setErrorOutput(String errorOutput) {
        this.errorOutput = errorOutput;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public ExecCmdResultDto setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ExecCmdResultDto setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
