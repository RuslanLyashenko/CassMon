package ua.ubki.cassmon.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.ubki.cassmon.utils.json.JsonUtils;

public class TableStatDto {
    private String tableName;
    @JsonProperty("space_used_total") // общее занимаемое место
    private Long spaceUsedTotal;
    @JsonProperty("space_used_live")
    private Long spaceUsedLive;
    @JsonProperty("space_used_by_snapshots_total") // занимаемое снепшотами место
    private Long spaceUsedBySnapshotsTotal;

    @JsonProperty("bloom_filter_off_heap_memory_used")
    private String bloomFilterOffHeapMemoryUsed;

    @JsonProperty("memtable_switch_count")
    private Integer memtableSwitchCount;


    @JsonProperty("memtable_cell_count")
    private Integer memtableCellCount;

    @JsonProperty("memtable_data_size")
    private String memtableDataSize;


    @JsonProperty("local_read_latency_ms")
    private String localReadLatencyMs;

    @JsonProperty("local_write_latency_ms")
    private String localWriteLatencyMs;

    @JsonProperty("pending_flushes")
    private Integer pendingFlushes;

    @JsonProperty("compacted_partition_minimum_bytes")
    private Integer compactedPartitionMinimumBytes;

    @JsonProperty("local_read_count")
    private Integer localReadCount;

    @JsonProperty("sstable_compression_ratio")
    private Double sstableCompressionRatio;


    @JsonProperty("bloom_filter_false_positives")
    private Integer bloomFilterFalsePositives;

    @JsonProperty("off_heap_memory_used_total")
    private String offHeapMemoryUsedTotal;

    @JsonProperty("memtable_off_heap_memory_used")
    private String memtableOffHeapMemoryUsed;

    @JsonProperty("index_summary_off_heap_memory_used")
    private String indexSummaryOffHeapMemoryUsed;

    @JsonProperty("bloom_filter_space_used")
    private String bloomFilterSpaceUsed;

    @JsonProperty("sstables_in_each_level")
    private List<Object> sstablesInEachLevel;

    @JsonProperty("compacted_partition_maximum_bytes")
    private Long compactedPartitionMaximumBytes;


    @JsonProperty("local_write_count")
    private Long localWriteCount;

    @JsonProperty("compression_metadata_off_heap_memory_used")
    private String compressionMetadataOffHeapMemoryUsed;

    @JsonProperty("number_of_partitions_estimate")
    private Integer numberOfPartitionsEstimate;

    @JsonProperty("compacted_partition_mean_bytes")
    private Long compactedPartitionMeanBytes;

    @JsonProperty("bloom_filter_false_ratio")
    private String bloomFilterFalseRatio;

    @JsonProperty("percent_repaired")
    private Double percentRepaired;

    @JsonProperty("average_live_cells_per_slice_last_five_minutes")
    private Double averageLiveCellsPerSliceLastFiveMinutes;
    @JsonProperty("maximum_live_cells_per_slice_last_five_minutes")
    private Integer maximumLiveCellsPerSliceLastFiveMinutes;
    @JsonProperty("average_tombstones_per_slice_last_five_minutes")
    private Double averageTombstonesPerSliceLastFiveMinutes;
    @JsonProperty("maximum_tombstones_per_slice_last_five_minutes")
    private Integer maximumTombstonesPerSliceLastFiveMinutes;
    @JsonProperty("dropped_mutations")
    private String droppedMutations;

    public void setAverageTombstonesPerSliceLastFiveMinutes(Double averageTombstonesPerSliceLastFiveMinutes) {
        this.averageTombstonesPerSliceLastFiveMinutes = averageTombstonesPerSliceLastFiveMinutes;
    }

    public Double getAverageTombstonesPerSliceLastFiveMinutes() {
        return averageTombstonesPerSliceLastFiveMinutes;
    }

    public void setBloomFilterOffHeapMemoryUsed(String bloomFilterOffHeapMemoryUsed) {
        this.bloomFilterOffHeapMemoryUsed = bloomFilterOffHeapMemoryUsed;
    }

    public String getBloomFilterOffHeapMemoryUsed() {
        return bloomFilterOffHeapMemoryUsed;
    }

    public void setMemtableSwitchCount(Integer memtableSwitchCount) {
        this.memtableSwitchCount = memtableSwitchCount;
    }

    public Integer getMemtableSwitchCount() {
        return memtableSwitchCount;
    }

    public void setMaximumTombstonesPerSliceLastFiveMinutes(Integer maximumTombstonesPerSliceLastFiveMinutes) {
        this.maximumTombstonesPerSliceLastFiveMinutes = maximumTombstonesPerSliceLastFiveMinutes;
    }

    public Integer getMaximumTombstonesPerSliceLastFiveMinutes() {
        return maximumTombstonesPerSliceLastFiveMinutes;
    }

    public void setMemtableCellCount(Integer memtableCellCount) {
        this.memtableCellCount = memtableCellCount;
    }

    public Integer getMemtableCellCount() {
        return memtableCellCount;
    }

    public void setMemtableDataSize(String memtableDataSize) {
        this.memtableDataSize = memtableDataSize;
    }

    public String getMemtableDataSize() {
        return memtableDataSize;
    }

    public void setAverageLiveCellsPerSliceLastFiveMinutes(Double averageLiveCellsPerSliceLastFiveMinutes) {
        this.averageLiveCellsPerSliceLastFiveMinutes = averageLiveCellsPerSliceLastFiveMinutes;
    }

    public Double getAverageLiveCellsPerSliceLastFiveMinutes() {
        return averageLiveCellsPerSliceLastFiveMinutes;
    }

    public void setLocalReadLatencyMs(String localReadLatencyMs) {
        this.localReadLatencyMs = localReadLatencyMs;
    }

    public String getLocalReadLatencyMs() {
        return localReadLatencyMs;
    }

    public void setLocalWriteLatencyMs(String localWriteLatencyMs) {
        this.localWriteLatencyMs = localWriteLatencyMs;
    }

    public String getLocalWriteLatencyMs() {
        return localWriteLatencyMs;
    }

    public void setPendingFlushes(Integer pendingFlushes) {
        this.pendingFlushes = pendingFlushes;
    }

    public Integer getPendingFlushes() {
        return pendingFlushes;
    }

    public void setCompactedPartitionMinimumBytes(Integer compactedPartitionMinimumBytes) {
        this.compactedPartitionMinimumBytes = compactedPartitionMinimumBytes;
    }

    public Integer getCompactedPartitionMinimumBytes() {
        return compactedPartitionMinimumBytes;
    }

    public void setLocalReadCount(Integer localReadCount) {
        this.localReadCount = localReadCount;
    }

    public Integer getLocalReadCount() {
        return localReadCount;
    }

    public void setSstableCompressionRatio(Double sstableCompressionRatio) {
        this.sstableCompressionRatio = sstableCompressionRatio;
    }

    public Double getSstableCompressionRatio() {
        return sstableCompressionRatio;
    }

    public void setDroppedMutations(String droppedMutations) {
        this.droppedMutations = droppedMutations;
    }

    public String getDroppedMutations() {
        return droppedMutations;
    }

    public void setBloomFilterFalsePositives(Integer bloomFilterFalsePositives) {
        this.bloomFilterFalsePositives = bloomFilterFalsePositives;
    }

    public Integer getBloomFilterFalsePositives() {
        return bloomFilterFalsePositives;
    }

    public void setOffHeapMemoryUsedTotal(String offHeapMemoryUsedTotal) {
        this.offHeapMemoryUsedTotal = offHeapMemoryUsedTotal;
    }

    public String getOffHeapMemoryUsedTotal() {
        return offHeapMemoryUsedTotal;
    }

    public void setMemtableOffHeapMemoryUsed(String memtableOffHeapMemoryUsed) {
        this.memtableOffHeapMemoryUsed = memtableOffHeapMemoryUsed;
    }

    public String getMemtableOffHeapMemoryUsed() {
        return memtableOffHeapMemoryUsed;
    }

    public void setIndexSummaryOffHeapMemoryUsed(String indexSummaryOffHeapMemoryUsed) {
        this.indexSummaryOffHeapMemoryUsed = indexSummaryOffHeapMemoryUsed;
    }

    public String getIndexSummaryOffHeapMemoryUsed() {
        return indexSummaryOffHeapMemoryUsed;
    }

    public void setBloomFilterSpaceUsed(String bloomFilterSpaceUsed) {
        this.bloomFilterSpaceUsed = bloomFilterSpaceUsed;
    }

    public String getBloomFilterSpaceUsed() {
        return bloomFilterSpaceUsed;
    }

    public void setSstablesInEachLevel(List<Object> sstablesInEachLevel) {
        this.sstablesInEachLevel = sstablesInEachLevel;
    }

    public List<Object> getSstablesInEachLevel() {
        return sstablesInEachLevel;
    }

    public void setCompactedPartitionMaximumBytes(Long compactedPartitionMaximumBytes) {
        this.compactedPartitionMaximumBytes = compactedPartitionMaximumBytes;
    }

    public Long getCompactedPartitionMaximumBytes() {
        return compactedPartitionMaximumBytes;
    }

    public void setSpaceUsedTotal(Long spaceUsedTotal) {
        this.spaceUsedTotal = spaceUsedTotal;
    }

    public Long getSpaceUsedTotal() {
        return spaceUsedTotal;
    }

    public void setLocalWriteCount(Long localWriteCount) {
        this.localWriteCount = localWriteCount;
    }

    public Long getLocalWriteCount() {
        return localWriteCount;
    }

    public void setCompressionMetadataOffHeapMemoryUsed(String compressionMetadataOffHeapMemoryUsed) {
        this.compressionMetadataOffHeapMemoryUsed = compressionMetadataOffHeapMemoryUsed;
    }

    public String getCompressionMetadataOffHeapMemoryUsed() {
        return compressionMetadataOffHeapMemoryUsed;
    }

    public void setNumberOfPartitionsEstimate(Integer numberOfPartitionsEstimate) {
        this.numberOfPartitionsEstimate = numberOfPartitionsEstimate;
    }

    public Integer getNumberOfPartitionsEstimate() {
        return numberOfPartitionsEstimate;
    }

    public void setMaximumLiveCellsPerSliceLastFiveMinutes(Integer maximumLiveCellsPerSliceLastFiveMinutes) {
        this.maximumLiveCellsPerSliceLastFiveMinutes = maximumLiveCellsPerSliceLastFiveMinutes;
    }

    public Integer getMaximumLiveCellsPerSliceLastFiveMinutes() {
        return maximumLiveCellsPerSliceLastFiveMinutes;
    }

    public void setSpaceUsedLive(Long spaceUsedLive) {
        this.spaceUsedLive = spaceUsedLive;
    }

    public Long getSpaceUsedLive() {
        return spaceUsedLive;
    }

    public void setCompactedPartitionMeanBytes(Long compactedPartitionMeanBytes) {
        this.compactedPartitionMeanBytes = compactedPartitionMeanBytes;
    }

    public Long getCompactedPartitionMeanBytes() {
        return compactedPartitionMeanBytes;
    }

    public void setBloomFilterFalseRatio(String bloomFilterFalseRatio) {
        this.bloomFilterFalseRatio = bloomFilterFalseRatio;
    }

    public String getBloomFilterFalseRatio() {
        return bloomFilterFalseRatio;
    }

    public void setPercentRepaired(Double percentRepaired) {
        this.percentRepaired = percentRepaired;
    }

    public Double getPercentRepaired() {
        return percentRepaired;
    }

    public void setSpaceUsedBySnapshotsTotal(Long spaceUsedBySnapshotsTotal) {
        this.spaceUsedBySnapshotsTotal = spaceUsedBySnapshotsTotal;
    }

    public Long getSpaceUsedBySnapshotsTotal() {
        return spaceUsedBySnapshotsTotal;
    }

    public String getTableName() {
        return tableName;
    }

    public TableStatDto setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String formatter(Integer value) {
        if (value == null) {
            return "";
        } else {
            return String.format("%,d", value);
        }
    }

    @Override
    public String toString() {
        return JsonUtils.buildJsonString(this);
    }
}