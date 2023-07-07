package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.StringsUtils;
import ua.ubki.cassmon.utils.json.JsonUtils;

public class NodeStateDto {
    private String ip;
    private String status;
    private String diskSpace;

    public NodeStateDto(String statusString) {
        String[] statusStr = StringsUtils.truncateSpace(statusString).split(" ");
        status = statusStr[0];
        ip = statusStr[1];
        diskSpace = statusStr[2] + " " + statusStr[3];
    }

    public String getIp() {
        return ip;
    }

    public NodeStateDto setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public NodeStateDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getDiskSpace() {
        return diskSpace;
    }

    public NodeStateDto setDiskSpace(String diskSpace) {
        this.diskSpace = diskSpace;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
