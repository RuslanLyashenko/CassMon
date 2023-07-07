package ua.ubki.cassmon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.ubki.cassmon.utils.json.JsonUtils;

import java.util.List;

public class SnapshotStatDto {
    private String tableName;
    private Long spaceTotal;
    private Long snapshotsSpaceTotal;

    public String getTableName() {
        return tableName;
    }

    public SnapshotStatDto setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Long getSpaceTotal() {
        return spaceTotal;
    }

    public SnapshotStatDto setSpaceTotal(Long spaceTotal) {
        this.spaceTotal = spaceTotal;
        return this;
    }

    public Long getSnapshotsSpaceTotal() {
        return snapshotsSpaceTotal;
    }

    public SnapshotStatDto setSnapshotsSpaceTotal(Long snapshotsSpaceTotal) {
        this.snapshotsSpaceTotal = snapshotsSpaceTotal;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SnapshotStatDto that)) return false;

        return tableName.equals(that.tableName);
    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}