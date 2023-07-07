package ua.ubki.cassmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.ubki.cassmon.dto.*;
import ua.ubki.cassmon.service.CassandraService;
import ua.ubki.cassmon.utils.ExecCmdResultDto;

import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final CassandraService cassandraService;

    @Autowired
    public RestController(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    @GetMapping("stopCompacting/{ip}")
    public ExecCmdResultDto stopCompacting(@PathVariable String ip) {
        return cassandraService.stopCompacting(ip);
    }

    @GetMapping("tableStats/{ip}")
    public Map<String, List<TableStatDto>> getTableStat(@PathVariable String ip) {
        return cassandraService.getTableStat(ip).getLeft();
    }

    @GetMapping("clearSnapshots/{clusterName}/{keySpace}")
    public List<String> clearSnapshots(@PathVariable String clusterName, @PathVariable String keySpace) {
        return cassandraService.clearSnapshots(clusterName, keySpace);
    }

    @GetMapping("snapshotsStats/{clusterName}")
    public Map<String, List<SnapshotStatDto>> getSnapshotsStat(@PathVariable String clusterName) {
        return cassandraService.getSnapshotsList(clusterName);
    }

    @GetMapping("compactionStatByClusterName/{clusterName}")
    public List<CompactingStatusDto> getCompactionStatAll(@PathVariable String clusterName) {
        return cassandraService.getCompactionStatByClusterName(clusterName).getLeft();
    }

    @GetMapping("startCompacting/{ip}/{keySpace}/{tableName}/{compactAllNodes}")
    public String startCompacting(@PathVariable String ip, @PathVariable String keySpace,
                                  @PathVariable String tableName, @PathVariable boolean compactAllNodes) {
        cassandraService.startCompacting(ip, keySpace, tableName, compactAllNodes);
        return "ok";
    }

    @GetMapping("setIOPerfValue/{clusterName}/{ip}/{ioPerfValue}")
    public String startCompacting(@PathVariable String clusterName, @PathVariable String ip, @PathVariable int ioPerfValue) {
        cassandraService.setIOPerfValue(clusterName, ip, ioPerfValue);
        return "ok";
    }

}
