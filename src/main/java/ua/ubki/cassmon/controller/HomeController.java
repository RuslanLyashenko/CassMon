package ua.ubki.cassmon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.ubki.cassmon.service.CassandraService;
import ua.ubki.cassmon.service.NodeService;

@Controller
@RequestMapping("")
public class HomeController {

    private final CassandraService cassandraService;
    private final NodeService nodeService;

    @Autowired
    public HomeController(CassandraService cassandraService, NodeService nodeService) {
        this.cassandraService = cassandraService;
        this.nodeService = nodeService;
    }

    // главная страница (активен первый кластер в списке)
    @GetMapping("")
    public String home(Model model) {
        var clusterList = nodeService.getClusterList();
        String clusterName = clusterList.get(0);
        model.addAttribute("clusterList", clusterList);
        model.addAttribute("activeCluster", clusterName);
        var nodesStatusInfo = cassandraService.getNodesStatusInfo(clusterName);
        model.addAttribute("nodeStatus", nodesStatusInfo.getLeft());
        model.addAttribute("errorList", nodesStatusInfo.getRight());

        return "home";
    }

    @GetMapping("tableList/{ip}")
    public String getTableList(@PathVariable String ip, Model model) {
        model.addAttribute("ip", ip);
        return "table-list";
    }

    @GetMapping("snapshotsList/{clusterName}")
    public String getSnapshotsList(@PathVariable String clusterName, Model model) {
        model.addAttribute("clusterName", clusterName);
        return "snapshots-list";
    }

    @GetMapping("compactingStat/{clusterName}")
    public String getCompactingStat(@PathVariable String clusterName, Model model) {
        model.addAttribute("clusterName", clusterName);
        model.addAttribute("compactingStat", cassandraService.getCompactionStatByClusterName(clusterName));
        return "compacting-stat";
    }


    // состояние кластера
    @GetMapping("nodeStatus/{clusterName}")
    public String home(@PathVariable String clusterName, Model model) {
        model.addAttribute("clusterList", nodeService.getClusterList());
        model.addAttribute("activeCluster", clusterName);
        model.addAttribute("nodeStatus", cassandraService.getNodesStatusInfo(clusterName).getLeft());
        model.addAttribute("errorList", cassandraService.getNodesStatusInfo(clusterName).getRight());

        return "home";
    }

//    @GetMapping("tables/{ip}")
//    public String getTablesStat(@PathVariable String ip, Model model) throws IOException, InterruptedException {
//        var tableMap = cassandraService.getTableStat(ip);
//        var keySpaces = tableMap.keySet().stream().sorted(String::compareTo).toList();
//        var keySpace = keySpaces.get(0);
//
//        model.addAttribute("ip", ip);
//        model.addAttribute("keySpace", keySpace);
//        model.addAttribute("keySpaces", keySpaces);
//        model.addAttribute("tableList", tableMap.get(keySpace));
//        return "tables";
//    }
//
//    @GetMapping("tables/{ip}/{keySpace}")
//    public String getTablesStat(@PathVariable String ip, @PathVariable String keySpace, Model model) throws IOException, InterruptedException {
//        var tableMap = cassandraService.getTableStat(ip);
//        model.addAttribute("ip", ip);
//        model.addAttribute("keySpace", keySpace);
//        model.addAttribute("keySpaces", tableMap.keySet().stream().sorted(String::compareTo).toList());
//        model.addAttribute("tableList", tableMap.get(keySpace));
//        return "tables";
//    }
//
//    @GetMapping("globalStat/{storageName}/{keySpace}")
//    public String getGlobalStat(@PathVariable String storageName, @PathVariable String keySpace, Model model) throws IOException, InterruptedException {
//        var globalStat = cassandraService.getGlobalStat(storageName, keySpace);
//        model.addAttribute("keySpace", keySpace);
//        model.addAttribute("headerList", globalStat.getKey());
//        model.addAttribute("tableList", globalStat.getValue());
//        return "global-stat";
//    }

}
