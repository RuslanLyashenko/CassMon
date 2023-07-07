package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.StringsUtils;
import ua.ubki.cassmon.utils.json.JsonUtils;

public class NodeStatusDto {
    private String ip;
    private String status;
    private String diskSpace;
    private DiskStatDto diskStat;
    private Integer compactingSlots;
    private String ioPerf;
    private Integer readAHead;
    private String version;

    public NodeStatusDto() {
    }

    public NodeStatusDto(String statusString) {
        String[] statusStr = StringsUtils.truncateSpace(statusString).split(" ");
        status = statusStr[0];
        ip = statusStr[1];
        diskSpace = statusStr[2] + " " + statusStr[3];
    }

    public int getIOPerfValue() {
        return ioPerf.equals("ERROR") ? 0 : Integer.parseInt(ioPerf.split(" ")[0]);
    }

    public String getIoPerf() {
        return ioPerf;
    }

    public NodeStatusDto setIoPerf(String ioPerf) {
        this.ioPerf = ioPerf;
        return this;
    }

    public Integer getCompactingSlots() {
        return compactingSlots;
    }

    public NodeStatusDto setCompactingSlots(Integer compactingSlots) {
        this.compactingSlots = compactingSlots;
        return this;
    }

    public DiskStatDto getDiskStat() {
        return diskStat;
    }

    public NodeStatusDto setDiskStat(DiskStatDto diskStat) {
        this.diskStat = diskStat;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public NodeStatusDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public NodeStatusDto setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getDiskSpace() {
        return diskSpace;
    }

    public NodeStatusDto setDiskSpace(String diskSpace) {
        this.diskSpace = diskSpace;
        return this;
    }

    public Integer getReadAHead() {
        return readAHead;
    }

    public NodeStatusDto setReadAHead(Integer readAHead) {
        this.readAHead = readAHead;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public NodeStatusDto setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
