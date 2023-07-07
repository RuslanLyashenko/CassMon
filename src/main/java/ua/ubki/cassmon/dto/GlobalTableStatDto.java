package ua.ubki.cassmon.dto;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ua.ubki.cassmon.utils.json.JsonUtils;

import java.util.List;

public class GlobalTableStatDto {
    private String tableName;
    private Integer dc1Total;
    private Integer dc2Total;
    private Integer dcDelta;
    private Pair<Double, Double> dcPercent;
    private List<Triple<Integer, Double, Double>> tableSize;

    public String getTableName() {
        return tableName;
    }

    public GlobalTableStatDto setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Integer getDc1Total() {
        return dc1Total;
    }

    public GlobalTableStatDto setDc1Total(Integer dc1Total) {
        this.dc1Total = dc1Total;
        return this;
    }

    public Integer getDc2Total() {
        return dc2Total;
    }

    public GlobalTableStatDto setDc2Total(Integer dc2Total) {
        this.dc2Total = dc2Total;
        return this;
    }

    public Integer getDcDelta() {
        return dcDelta;
    }

    public GlobalTableStatDto setDcDelta(Integer dcDelta) {
        this.dcDelta = dcDelta;
        return this;
    }

    public Pair<Double, Double> getDcPercent() {
        return dcPercent;
    }

    public GlobalTableStatDto setDcPercent(Pair<Double, Double> dcPercent) {
        this.dcPercent = dcPercent;
        return this;
    }

    public List<Triple<Integer, Double, Double>> getTableSize() {
        return tableSize;
    }

    public GlobalTableStatDto setTableSize(List<Triple<Integer, Double, Double>> tableSize) {
        this.tableSize = tableSize;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}
