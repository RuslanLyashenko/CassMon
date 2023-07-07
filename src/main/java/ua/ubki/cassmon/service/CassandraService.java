package ua.ubki.cassmon.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.ubki.cassmon.dto.*;
import ua.ubki.cassmon.utils.ExecCmd;
import ua.ubki.cassmon.utils.ExecCmdResultDto;
import ua.ubki.cassmon.utils.ParallelExecutor;
import ua.ubki.cassmon.utils.StringsUtils;
import ua.ubki.cassmon.utils.json.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CassandraService {
    private static final Logger logger = LoggerFactory.getLogger(CassandraService.class);

    @Value("${cassandra.bin.path}")
    private String cassandraBinPath;
    @Value("${node.login}")
    private String nodeLogin;
    @Value("${cassandra.storage.path}")
    private String storagePath;
    @Value("${base.ip}")
    private String baseIp;

    private final NodeService nodeService;

    @Autowired
    public CassandraService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    // состояние нод кластера
    public Pair<List<NodeStateDto>, String> getNodeStatusByClusterName(String clusterName) {
        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);
        ExecCmdResultDto execResult = new ExecCmdResultDto();
        String ip = "";

        for (var node : nodeList) {
            ip = node.getIp();

            logger.info("Get nodes status by cluster name: {}. Get status for node: {}", clusterName, ip);
            String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy status",
                    nodeLogin, ip, cassandraBinPath);

            execResult = ExecCmd.exec(cmd);
            if (execResult.isOK()) {
                break;
            } else {
                logger.warn("Get nodes status error. Try another node");
            }
        }

        if (execResult.isOK()) {
            return Pair.of(Arrays.stream(execResult.getCommandOutput().split("\n"))
                    .filter(e -> e.contains(baseIp))
                    .map(NodeStateDto::new)
                    .toList(), "");

        } else {
            return Pair.of(Collections.emptyList(),
                    String.format("Get nodes status by cluster name error. Node: %s. %s", ip, execResult.log()));
        }
    }

    // чтение состояния диска для ноды по IP
    public ExecCmdResultDto getDiskStat(String ip) {
        logger.info("Request disk stats. Node: {}", ip);

        String dfCmd = """
                export LANGUAGE=English; df | awk 'BEGIN {printf"["}{if($1=="Filesystem")next;if(a)printf",";printf"{\\"mount\\":\\""$6"\\",\\"totalSpace\\":\\""$2"\\",\\"usedSpace\\":\\""$3"\\",\\"freeSpace\\":\\""$4"\\",\\"percent\\":\\""$5"\\"}";a++;}END{print"]";}'
                """;
        String cmd = String.format("ssh %s@%s %s", nodeLogin, ip, dfCmd);

        return ExecCmd.exec(cmd);
    }

    // чтение состояния диска для нод кластера
    public Pair<List<Pair<String, DiskStatDto>>, List<String>> getDiskStatByClusterName(String clusterName) {
        logger.info("Get disk stat by cluster name: {}", clusterName);

        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);
        List<Pair<String, ExecCmdResultDto>> result = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> nodeList.parallelStream()
                        .forEach(node -> result.add(Pair.of(node.getIp(), getDiskStat(node.getIp())))),
                20, true);

        return Pair.of(
                result.stream()
                        .map(e -> {
                            if (e.getRight().isOK()) {
                                String json = e.getRight().getCommandOutput();

                                return Pair.of(e.getLeft(), JsonUtils.buildListFromJsonString(json, JsonNode.class)
                                        .stream()
                                        .filter(v -> v.get("mount").textValue().startsWith("/"))
                                        .map(v -> ((ObjectNode) v).put("percent", v.get("percent").textValue().replace("%", "")))
                                        .map(v -> JsonUtils.buildObjectFromJsonString(v.toString(), DiskStatDto.class))
                                        .filter(v -> v.getMount().equals(storagePath))
                                        .findFirst().orElse(new DiskStatDto()));
                            } else {
                                return Pair.of(e.getLeft(), new DiskStatDto());
                            }
                        })
                        .toList(),
                result.stream()
                        .filter(e -> !e.getRight().isOK())
                        .map(e -> String.format("Get disk stat error. Node: %s. %s", e.getLeft(), e.getRight().log()))
                        .toList()
        );
    }

    // чтение состояния ReadAHead для ноды по IP
    public ExecCmdResultDto getReadAHeadValue(String ip) {
        logger.info("Request ReadAHead value. Node: {}", ip);

        String lsblkCmd = """
                export LANGUAGE=English; lsblk -l --output NAME,KNAME,TYPE,FSTYPE,RA,MOUNTPOINT | awk 'BEGIN {printf"["}{if($1=="NAME")next;if(a)printf",";printf"{\\"name\\":\\""$1"\\",\\"kname\\":\\""$2"\\",\\"type\\":\\""$3"\\",\\"fsType\\":\\""$4"\\",\\"readAHead\\":\\""$5"\\",\\"mountPoint\\":\\""$6"\\"}";a++;}END{print"]";}'
                """;
        String cmd = String.format("ssh %s@%s %s", nodeLogin, ip, lsblkCmd);

        return ExecCmd.exec(cmd);
    }

    // чтение состояния ReadAHead для ноды по названию кластера
    public Pair<List<Pair<String, Integer>>, List<String>> getReadAHeadValueByClusterName(String clusterName) {
        logger.info("Get node ReadAHead value by cluster name: {}", clusterName);

        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);
        List<Pair<String, ExecCmdResultDto>> result = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> nodeList.parallelStream()
                        .forEach(node -> result.add(Pair.of(node.getIp(), getReadAHeadValue(node.getIp())))),
                20, true);

        return Pair.of(
                result.stream()
                        .map(e -> {
                            if (e.getRight().isOK()) {
                                String json = e.getRight().getCommandOutput();

                                List<ReadAHeadStatDto> readAHeadStatList = JsonUtils.buildListFromJsonString(json, JsonNode.class)
                                        .stream()
                                        .filter(v -> v.get("mountPoint").textValue().startsWith("/"))
                                        .map(v -> JsonUtils.buildObjectFromJsonString(v.toString(), ReadAHeadStatDto.class))
                                        .filter(v -> v.getMountPoint().equals(storagePath))
                                        .toList();

                                if (readAHeadStatList.isEmpty()) {
                                    return Pair.of(e.getLeft(), -1);
                                } else if (readAHeadStatList.stream().anyMatch(v -> v.getReadAHead() != 0)) {
                                    return Pair.of(e.getLeft(), 0);
                                } else {
                                    return Pair.of(e.getLeft(), 1);
                                }
                            } else {
                                return Pair.of(e.getLeft(), -1);
                            }
                        })
                        .toList(),
                result.stream()
                        .filter(e -> !e.getRight().isOK())
                        .map(e -> String.format("Get node ReadAHead value error. Node: %s. %s",
                                e.getLeft(), e.getRight().log()))
                        .toList()
        );
    }

    // получение производительности компактинга для ноды по IP
    public ExecCmdResultDto getPerformanceIO(String ip) {
        logger.info("Request compacting performance IO. Node: {}", ip);

        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy getcompactionthroughput",
                nodeLogin, ip, cassandraBinPath);
        return ExecCmd.exec(cmd);
    }

    // получение производительности компактинга по названию кластера
    public Pair<List<Pair<String, String>>, List<String>> getPerformanceIOByClusterName(String clusterName) {
        logger.info("Get Cassandra compacting performance by cluster name: {}", clusterName);

        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);
        List<Pair<String, ExecCmdResultDto>> result = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> nodeList.parallelStream()
                        .forEach(node -> result.add(Pair.of(node.getIp(), getPerformanceIO(node.getIp())))),
                20, true);

        return Pair.of(
                result.stream()
                        .map(e -> {
                            if (e.getRight().isOK()) {
                                String output = e.getRight().getCommandOutput();
                                String[] part = StringsUtils.truncateSpace(output).split(":");
                                return Pair.of(e.getLeft(), part[part.length - 1].trim());
                            } else {
                                return Pair.of(e.getLeft(), "ERROR");
                            }
                        })
                        .toList(),
                result.stream()
                        .filter(e -> !e.getRight().isOK())
                        .map(e -> String.format("Get Cassandra compacting performance error. Node: %s. %s",
                                e.getLeft(), e.getRight().log()))
                        .toList()
        );
    }

    // получение количества слотов компактинга для ноды по IP
    public ExecCmdResultDto getCompactingSlots(String ip) {
        logger.info("Request compacting slots. Node: {}", ip);

        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy getconcurrentcompactors",
                nodeLogin, ip, cassandraBinPath);
        return ExecCmd.exec(cmd);
    }

    // получение количества слотов компактинга по названию кластера
    public Pair<List<Pair<String, Integer>>, List<String>> getCompactingSlotsByClusterName(String clusterName) {
        logger.info("Get Cassandra compacting slots by cluster name: {}", clusterName);

        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);
        List<Pair<String, ExecCmdResultDto>> result = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> nodeList.parallelStream()
                        .forEach(node -> result.add(Pair.of(node.getIp(), getCompactingSlots(node.getIp())))),
                20, true);

        return Pair.of(
                result.stream()
                        .map(e -> {
                            if (e.getRight().isOK()) {
                                String output = e.getRight().getCommandOutput();
                                String[] part = StringsUtils.truncateSpace(output).split("\n");
                                return Pair.of(e.getLeft(), Integer.parseInt(part[part.length - 1]));
                            } else {
                                return Pair.of(e.getLeft(), -1);
                            }
                        })
                        .toList(),
                result.stream()
                        .filter(e -> !e.getRight().isOK())
                        .map(e -> String.format("Get Cassandra compacting slots error. Node: %s. %s",
                                e.getLeft(), e.getRight().log()))
                        .toList()
        );
    }

    // получение версии Кассандры для ноды по IP
    public ExecCmdResultDto getVersion(String ip) {
        logger.info("Request node version. Node: {}", ip);

        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy version",
                nodeLogin, ip, cassandraBinPath);
        return ExecCmd.exec(cmd);
    }

    // получение версии Кассандры всех нод по названию кластера
    public Pair<List<Pair<String, String>>, List<String>> getVersionByClusterName(String clusterName) {
        logger.info("Get Cassandra version by cluster name: {}", clusterName);

        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);
        List<Pair<String, ExecCmdResultDto>> result = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> nodeList.parallelStream()
                        .forEach(node -> result.add(Pair.of(node.getIp(), getVersion(node.getIp())))),
                20, true);

        return Pair.of(
                result.stream()
                        .map(e -> {
                            if (e.getRight().isOK()) {
                                String output = e.getRight().getCommandOutput();
                                String[] part = StringsUtils.truncateSpace(output).split(" ");
                                return Pair.of(e.getLeft(), part[part.length - 1]);
                            } else {
                                return Pair.of(e.getLeft(), "ERROR");
                            }
                        })
                        .toList(),
                result.stream()
                        .filter(e -> !e.getRight().isOK())
                        .map(e -> String.format("Get Cassandra version error. Node: %s. %s",
                                e.getLeft(), e.getRight().log()))
                        .toList()
        );
    }

    // комплексный отчет по состоянию нод кластера
    public Pair<List<NodeStatusDto>, List<String>> getNodesStatusInfo(String clusterName) {
        long tt = System.currentTimeMillis();

        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());
        List<NodeStatusDto> nodeStatusList = Collections.synchronizedList(new ArrayList<>());
        List<Pair<String, Long>> workTime = Collections.synchronizedList(new ArrayList<>());
        nodeService.getIpByClusterList(clusterName).forEach(e -> nodeStatusList.add(new NodeStatusDto().setIp(e.getIp())));

        List<Runnable> tasks = List.of(

                // состояние нод
                () -> {
                    long t = System.currentTimeMillis();
                    var nodeStatus = getNodeStatusByClusterName(clusterName);
                    nodeStatus.getLeft().forEach(e -> nodeStatusList.stream()
                            .filter(f -> f.getIp().equals(e.getIp()))
                            .findFirst()
                            .ifPresent(nodeStatusDto -> nodeStatusDto
                                    .setStatus(e.getStatus())
                                    .setDiskSpace(e.getDiskSpace())));
                    if (!nodeStatus.getRight().isEmpty()) {
                        errorMessages.add(nodeStatus.getRight());
                    }
                    workTime.add(Pair.of("getNodeStatus", System.currentTimeMillis() - t));
                },

                // свободное место на диске
                () -> {
                    long t = System.currentTimeMillis();
                    var diskStat = getDiskStatByClusterName(clusterName);
                    diskStat.getLeft().forEach(e -> nodeStatusList.stream()
                            .filter(f -> f.getIp().equals(e.getLeft()))
                            .findFirst()
                            .get()
                            .setDiskStat(e.getRight()));
                    errorMessages.addAll(diskStat.getRight());
                    workTime.add(Pair.of("getDiskStat", System.currentTimeMillis() - t));
                },

                // слотов компактинга
                () -> {
                    long t = System.currentTimeMillis();
                    var compactingSlots = getCompactingSlotsByClusterName(clusterName);
                    compactingSlots.getLeft().forEach(e -> nodeStatusList.stream()
                            .filter(f -> f.getIp().equals(e.getLeft()))
                            .findFirst()
                            .get()
                            .setCompactingSlots(e.getRight()));
                    errorMessages.addAll(compactingSlots.getRight());
                    workTime.add(Pair.of("getCompactingSlots", System.currentTimeMillis() - t));
                },

                // скорость ввода-вывода при компактинге
                () -> {
                    long t = System.currentTimeMillis();
                    var perf = getPerformanceIOByClusterName(clusterName);
                    perf.getLeft().forEach(e -> nodeStatusList.stream()
                            .filter(f -> f.getIp().equals(e.getLeft()))
                            .findFirst()
                            .get()
                            .setIoPerf(e.getRight()));
                    errorMessages.addAll(perf.getRight());
                    workTime.add(Pair.of("getPerformanceIO", System.currentTimeMillis() - t));
                },

                // состояние Read AHead!
                () -> {
                    long t = System.currentTimeMillis();
                    var readAHead = getReadAHeadValueByClusterName(clusterName);
                    readAHead.getLeft().forEach(e -> nodeStatusList.stream()
                            .filter(f -> f.getIp().equals(e.getLeft()))
                            .findFirst()
                            .get()
                            .setReadAHead(e.getRight()));
                    errorMessages.addAll(readAHead.getRight());
                    workTime.add(Pair.of("getReadAHead", System.currentTimeMillis() - t));
                },

                // версия Cassandra
                () -> {
                    long t = System.currentTimeMillis();
                    var version = getVersionByClusterName(clusterName);
                    version.getLeft().forEach(e -> nodeStatusList.stream()
                            .filter(f -> f.getIp().equals(e.getLeft()))
                            .findFirst()
                            .get()
                            .setVersion(e.getRight()));
                    errorMessages.addAll(version.getRight());
                    workTime.add(Pair.of("getVersion", System.currentTimeMillis() - t));
                }
        );

        ParallelExecutor.runOnThreads(() -> tasks.parallelStream()
                .forEach(Runnable::run), 20, true);

        workTime.add(Pair.of("Total time", System.currentTimeMillis() - tt));

        StringBuilder timeReport = new StringBuilder("\nGet node status info time stat, ms:\n");
        workTime.forEach(e -> timeReport.append(e.getLeft()).append(": ").append(e.getRight()).append("\n"));

        logger.info("{}", timeReport);

        return Pair.of(nodeStatusList, errorMessages);
    }

    // список таблиц ноды OLD
    public Pair<Map<String, List<TableStatDto>>, String> getTableStat(String ip) {
        logger.info("Request table stat. Node: {}", ip);

        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy cfstats -F json",
                nodeLogin, ip, cassandraBinPath);

        var execResult = ExecCmd.exec(cmd);

        Map<String, List<TableStatDto>> tableMap = new HashMap<>();

        if (execResult.isOK()) {
            Map<String, JsonNode> jsonNodeKeySpace = JsonUtils
                    .buildMapFromJsonString(execResult.getCommandOutput(), String.class, JsonNode.class);

            var excludedKeySpace = Set.of("system_traces", "system", "system_distributed", "system_schema", "system_auth");

            for (var keySpace : jsonNodeKeySpace.entrySet()) {

                if (!keySpace.getKey().equals("total_number_of_tables") && !excludedKeySpace.contains(keySpace.getKey())) {
                    String tablesStatJson = keySpace.getValue().get("tables").toString();

                    Map<String, TableStatDto> tableIndexMap = JsonUtils.buildMapFromJsonString(tablesStatJson,
                            String.class, TableStatDto.class, Map.of(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));


                    var tableList = tableIndexMap.entrySet().stream()
                            .map(e -> {

                                TableStatDto tableStat = e.getValue();
                                tableStat.setTableName(e.getKey());

                                tableStat.setSpaceUsedTotal(tableStat.getSpaceUsedTotal() / 1024 / 1024);
                                tableStat.setSpaceUsedLive(tableStat.getSpaceUsedLive() / 1024 / 1024);
                                tableStat.setSpaceUsedBySnapshotsTotal(tableStat.getSpaceUsedBySnapshotsTotal() / 1024 / 1024);

                                return tableStat;
                            })
                            .sorted((a, b) -> b.getSpaceUsedTotal().compareTo(a.getSpaceUsedTotal()))
                            .toList();
                    tableMap.put(keySpace.getKey(), tableList);
                }
            }

            return Pair.of(tableMap, "");
        } else {
            return Pair.of(tableMap,
                    String.format("Get table stat error. Node: %s. %s", ip, execResult.log()));
        }
    }

    // получение списка снепшотов
    public Map<String, List<SnapshotStatDto>> getSnapshotsList(String clusterName) {
        logger.info("Get snapshots list from {}", clusterName);

        long tt = System.currentTimeMillis();

        var iPs = nodeService.getIpByClusterList(clusterName);
        List<Map<String, List<TableStatDto>>> result = Collections.synchronizedList(new ArrayList<>());
        List<Pair<String, Long>> workTime = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> iPs.parallelStream().forEach(node -> {
            long t = System.currentTimeMillis();
            result.add(getTableStat(node.getIp()).getLeft());
            workTime.add(Pair.of(node.getIp(), System.currentTimeMillis() - t));
        }), 20, true);

        // все keyspace датацентра
        List<String> keySpaceList = result.stream()
                .map(e -> e.keySet().stream().toList())
                .flatMap(List::stream)
                .distinct()
                .toList();

        Map<String, List<SnapshotStatDto>> snapshotsList = new HashMap<>();

        keySpaceList.forEach(keySpace -> {
            List<List<SnapshotStatDto>> tableSnapshotsList = new ArrayList<>();
            result.forEach(node -> tableSnapshotsList.add(node.get(keySpace).stream()
                    .filter(table -> table.getSpaceUsedBySnapshotsTotal() > 0)
                    .map(table -> new SnapshotStatDto()
                            .setTableName(table.getTableName())
                            .setSpaceTotal(table.getSpaceUsedTotal())
                            .setSnapshotsSpaceTotal(table.getSpaceUsedBySnapshotsTotal()))
                    .toList()));

            var currentSnapshotsList = tableSnapshotsList.stream()
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(SnapshotStatDto::getTableName))
                    .entrySet()
                    .stream()
                    .map(e -> {
                        var snapshotsStat = new SnapshotStatDto().setTableName(e.getKey());
                        var tables = e.getValue();
                        snapshotsStat.setSpaceTotal(tables.stream()
                                .map(SnapshotStatDto::getSpaceTotal)
                                .reduce(0L, Long::sum));
                        snapshotsStat.setSnapshotsSpaceTotal(tables.stream()
                                .map(SnapshotStatDto::getSnapshotsSpaceTotal)
                                .reduce(0L, Long::sum));
                        return snapshotsStat;
                    })
                    .toList();

            if (!currentSnapshotsList.isEmpty()) {
                snapshotsList.put(keySpace, currentSnapshotsList);
            }
        });

        workTime.add(Pair.of("Total time", System.currentTimeMillis() - tt));

        StringBuilder timeReport = new StringBuilder("\nGet snapshots total space time stat, ms:\n");
        workTime.forEach(e -> timeReport.append(e.getLeft()).append(": ").append(e.getRight()).append("\n"));

        logger.info("Total time: {}", timeReport);

        return snapshotsList;
    }

    // чистка всех снепшотов
    public List<String> clearSnapshots(String clusterName, String keySpace) {
        logger.info("Clear snapshots in {} keySpace {}", clusterName, keySpace);

        long tt = System.currentTimeMillis();

        var nodes = nodeService.getIpByClusterList(clusterName);
        List<Pair<String, Long>> workTime = Collections.synchronizedList(new ArrayList<>());

        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

        ParallelExecutor.runOnThreads(() -> nodes.parallelStream().forEach(node -> {
            long t = System.currentTimeMillis();
            String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy clearsnapshot %s --all",
                    nodeLogin, node.getIp(), cassandraBinPath, keySpace);

            var execResult = ExecCmd.exec(cmd);
            if (!execResult.isOK()) {
                errorMessages.add(execResult.log());
            }

            workTime.add(Pair.of(node.getIp(), System.currentTimeMillis() - t));
        }), 20, true);

        workTime.add(Pair.of("Total time", System.currentTimeMillis() - tt));

        StringBuilder timeReport = new StringBuilder("\nClear snapshots in {}, ms:\n");
        workTime.forEach(e -> timeReport.append(e.getLeft()).append(": ").append(e.getRight()).append("\n"));

        logger.info("Total time: {}", timeReport);

        return errorMessages;
    }

    // состояние компактинка отдельной ноды (сырой вид)
    public ExecCmdResultDto getCompactionStatTxt(String ip) {
        logger.info("Request compaction stats. Node: {}", ip);

        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy compactionstats -H",
                nodeLogin, ip, cassandraBinPath);

        return ExecCmd.exec(cmd);
    }

    // состояние компактинга ноды
    public Pair<CompactingStatusDto, String> getCompactionStat(String ip) {
        var execResult = getCompactionStatTxt(ip);

        var compactingStatus = new CompactingStatusDto()
                .setNode(nodeService.getNodeByIP(ip));

        if (execResult.isOK()) {
            String compactionStr = execResult.getCommandOutput();

            var compactionStrList = Arrays.asList(compactionStr.split("\n"));

            var pendingObjects = compactionStrList.stream()
                    .filter(e -> e.startsWith("- ") && e.contains(": "))
                    .map(e -> e.replace("- ", "").split(": ")[0])
                    .toList();

            List<CompactingDto> compactingObjects = new ArrayList<>();

            if (compactionStr.contains("compaction type")) {

                var headerFieldPos = getCompactionStatFieldPos(
                        compactionStrList.stream()
                                .filter(e -> e.contains("compaction type"))
                                .findFirst()
                                .orElse(""));

                compactingObjects = compactionStrList.stream()
                        .filter(e -> e.contains(" bytes "))
                        .map(e -> new CompactingDto(e, headerFieldPos))
                        .toList();
            }

            var timeRemainingStr = StringsUtils.truncateSpace(compactionStrList.stream()
                    .filter(e -> e.startsWith("Active compaction remaining time :"))
                    .findFirst()
                    .orElse(""));

            var timeRemaining = timeRemainingStr.isEmpty() ? "" :
                    StringsUtils.truncateSpace(timeRemainingStr.split(" :")[1]
                            .replace("h", "h ")
                            .replace("m", "m "));

            return Pair.of(compactingStatus
                    .setTimeRemaining(timeRemaining)
                    .setPendingObjects(pendingObjects)
                    .setCompactingObjects(compactingObjects), "");
        } else {
            return Pair.of(compactingStatus, execResult.log());
        }
    }

    // состояние компактинга кластера
    public Pair<List<CompactingStatusDto>, List<String>> getCompactionStatByClusterName(String clusterName) {
        logger.info("Request compaction stats all nodes");

        List<NodeDto> nodeList = nodeService.getIpByClusterList(clusterName);

        List<CompactingStatusDto> result = Collections.synchronizedList(new ArrayList<>());
        List<String> resultError = Collections.synchronizedList(new ArrayList<>());

        nodeList.parallelStream().forEach(node -> {
            var compactionStat = getCompactionStat(node.getIp());
            result.add(compactionStat.getLeft());
            if (!compactionStat.getRight().isEmpty()) {
                resultError.add(compactionStat.getRight());
            }
        });

        return Pair.of(result, resultError);
    }

    public void startCompacting(String ip, String keySpace, String tableName, boolean compactAllNodes) {
        logger.info("Start compacting: {}.{}", keySpace, tableName);

        if (compactAllNodes) {
            nodeService.getIpByClusterList(nodeService.getNodeByIP(ip).getClusterName())
                    .forEach(e -> {
                        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy compact %s %s",
                                nodeLogin, e.getIp(), cassandraBinPath, keySpace, tableName);
                        ExecCmd.exec(cmd, true);
                    });
        } else {
            String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy compact %s %s",
                    nodeLogin, ip, cassandraBinPath, keySpace, tableName);
            ExecCmd.exec(cmd, true);
        }
    }

    public ExecCmdResultDto stopCompacting(String ip) {
        logger.info("Stop compacting: {}", ip);

        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy stop COMPACTION",
                nodeLogin, ip, cassandraBinPath);

        return ExecCmd.exec(cmd, true);
    }

    public void setIOPerfValue(String clusterName, String ip, int ioPerfValue) {
        logger.info("Set I/O performance");

        if (ip.equals("null")) {
            nodeService.getIpByClusterList(clusterName)
                    .forEach(e -> {
                        String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy setcompactionthroughput %d",
                                nodeLogin, e.getIp(), cassandraBinPath, ioPerfValue);
                        ExecCmd.exec(cmd, true);
                    });
        } else {
            String cmd = String.format("ssh %s@%s %snodetool -Dcom.sun.jndi.rmiURLParsing=legacy setcompactionthroughput %d",
                    nodeLogin, ip, cassandraBinPath, ioPerfValue);
            ExecCmd.exec(cmd, true);
        }

    }

    private List<Pair<Integer, Integer>> getCompactionStatFieldPos(String headerStr) {
        if (headerStr.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> fieldName = List.of("id", "compaction type", "keyspace", "table",
                    "completed", "total", "unit", "progress");

            List<Pair<Integer, Integer>> fieldLength = new ArrayList<>();

            for (int i = 0; i < fieldName.size(); i++) {
                String currField = fieldName.get(i);
                String nextField = (i == fieldName.size() - 1) ? null : fieldName.get(i + 1);

                Integer startPos = headerStr.indexOf(currField);
                Integer endPos = nextField == null ? null : headerStr.indexOf(nextField);

                fieldLength.add(Pair.of(startPos, endPos));
            }

            return fieldLength;
        }
    }
}
