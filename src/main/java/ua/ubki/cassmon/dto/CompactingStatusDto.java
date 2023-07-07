package ua.ubki.cassmon.dto;

import ua.ubki.cassmon.utils.json.JsonUtils;

import java.util.List;

public class CompactingStatusDto {
    private NodeDto node;
    private String timeRemaining;
    private List<String> pendingObjects;
    private List<CompactingDto> compactingObjects;

    public NodeDto getNode() {
        return node;
    }

    public CompactingStatusDto setNode(NodeDto node) {
        this.node = node;
        return this;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public CompactingStatusDto setTimeRemaining(String timeRemaining) {
        this.timeRemaining = timeRemaining;
        return this;
    }

    public List<String> getPendingObjects() {
        return pendingObjects;
    }

    public CompactingStatusDto setPendingObjects(List<String> pendingObjects) {
        this.pendingObjects = pendingObjects;
        return this;
    }

    public List<CompactingDto> getCompactingObjects() {
        return compactingObjects;
    }

    public CompactingStatusDto setCompactingObjects(List<CompactingDto> compactingObjects) {
        this.compactingObjects = compactingObjects;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
