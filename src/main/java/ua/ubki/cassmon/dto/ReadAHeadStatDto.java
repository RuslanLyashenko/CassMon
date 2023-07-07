package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.json.JsonUtils;

public class ReadAHeadStatDto {
    private String name;
    private String kname;
    private String type;
    private String fsType;
    private Integer readAHead;
    private String mountPoint;

    public String getName() {
        return name;
    }

    public ReadAHeadStatDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getKname() {
        return kname;
    }

    public ReadAHeadStatDto setKname(String kname) {
        this.kname = kname;
        return this;
    }

    public String getType() {
        return type;
    }

    public ReadAHeadStatDto setType(String type) {
        this.type = type;
        return this;
    }

    public String getFsType() {
        return fsType;
    }

    public ReadAHeadStatDto setFsType(String fsType) {
        this.fsType = fsType;
        return this;
    }

    public Integer getReadAHead() {
        return readAHead;
    }

    public ReadAHeadStatDto setReadAHead(Integer readAHead) {
        this.readAHead = readAHead;
        return this;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public ReadAHeadStatDto setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
