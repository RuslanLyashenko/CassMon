package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.json.JsonUtils;

public class ReadAHeadDto {
    private String ip;
    private boolean readAHead;

    public String getIp() {
        return ip;
    }

    public ReadAHeadDto setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public boolean isReadAHead() {
        return readAHead;
    }

    public ReadAHeadDto setReadAHead(boolean readAHead) {
        this.readAHead = readAHead;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
