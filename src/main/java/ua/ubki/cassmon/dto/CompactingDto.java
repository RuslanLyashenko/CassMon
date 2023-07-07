package ua.ubki.cassmon.dto;

import org.apache.commons.lang3.tuple.Pair;
import ua.ubki.cassmon.utils.StringsUtils;
import ua.ubki.cassmon.utils.json.JsonUtils;

import java.util.List;

public class CompactingDto {
    private String status;
    private String keySpace;
    private String objectName;
    private String currentBytes;
    private String totalBytes;
    private String progress;

    public CompactingDto(String compactingString, List<Pair<Integer, Integer>> headerFieldPos) {
        if (headerFieldPos.size() != 8) {
            status = "ERROR";
            keySpace = "ERROR";
            objectName = "ERROR";
            currentBytes = "ERROR";
            totalBytes = "ERROR";
            progress = "ERROR";
        } else {
            status = compactingString.substring(headerFieldPos.get(1).getLeft(), headerFieldPos.get(1).getRight()).trim();
            keySpace = compactingString.substring(headerFieldPos.get(2).getLeft(), headerFieldPos.get(2).getRight()).trim();
            objectName = compactingString.substring(headerFieldPos.get(3).getLeft(), headerFieldPos.get(3).getRight()).trim();
            currentBytes = compactingString.substring(headerFieldPos.get(4).getLeft(), headerFieldPos.get(4).getRight()).trim();
            totalBytes = compactingString.substring(headerFieldPos.get(5).getLeft(), headerFieldPos.get(5).getRight()).trim();
            progress = compactingString.substring(headerFieldPos.get(7).getLeft()).trim();
        }
    }

    public String getStatus() {
        return status;
    }

    public CompactingDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getKeySpace() {
        return keySpace;
    }

    public CompactingDto setKeySpace(String keySpace) {
        this.keySpace = keySpace;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public CompactingDto setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public String getCurrentBytes() {
        return currentBytes;
    }

    public CompactingDto setCurrentBytes(String currentBytes) {
        this.currentBytes = currentBytes;
        return this;
    }

    public String getTotalBytes() {
        return totalBytes;
    }

    public CompactingDto setTotalBytes(String totalBytes) {
        this.totalBytes = totalBytes;
        return this;
    }

    public String getProgress() {
        return progress;
    }

    public CompactingDto setProgress(String progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
