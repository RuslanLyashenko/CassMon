package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.json.JsonUtils;

public class NodeDto {
    private String clusterName;
    private String dataCenter;
    private String ip;

    public NodeDto(String nodeString) {
        var parts = nodeString.split(":");
        clusterName = parts[0];
        dataCenter = parts[1];
        ip = parts[2];
    }

    public String getClusterName() {
        return clusterName;
    }

    public NodeDto setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public NodeDto setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public NodeDto setIp(String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
