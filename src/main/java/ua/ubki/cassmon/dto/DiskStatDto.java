package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.json.JsonUtils;

public class DiskStatDto {
    private String mount = "";
    private Long totalSpace = 0L;
    private Long usedSpace = 0L;
    private Long freeSpace = 0L;
    private Integer percent = 0;

    public String getMount() {
        return mount;
    }

    public DiskStatDto setMount(String mount) {
        this.mount = mount;
        return this;
    }

    public Long getTotalSpace() {
        return totalSpace / 1024 / 1024;
    }

    public DiskStatDto setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
        return this;
    }

    public Long getUsedSpace() {
        return usedSpace / 1024 / 1024;
    }

    public DiskStatDto setUsedSpace(Long usedSpace) {
        this.usedSpace = usedSpace;
        return this;
    }

    public Long getFreeSpace() {
        return freeSpace / 1024 / 1024;
    }

    public DiskStatDto setFreeSpace(Long freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    public Integer getPercent() {
        return percent;
    }

    public DiskStatDto setPercent(Integer percent) {
        this.percent = percent;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
