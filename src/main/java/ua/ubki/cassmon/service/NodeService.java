package ua.ubki.cassmon.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ua.ubki.cassmon.dto.NodeDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Order(100)
public class NodeService {
    @Value("#{'${nodes}'.split(',')}")
    private List<String> nodeListParams;

    private List<NodeDto> nodeList;

    @PostConstruct
    public void setup() {
        nodeList = nodeListParams.stream().map(NodeDto::new).toList();
    }

    public List<NodeDto> getNodeList() {
        return nodeList;
    }

    public Map<String, List<String>> getDCNodeList(String storageName) {
        return nodeList.stream()
                .filter(e -> e.getClusterName().equals(storageName))
                .collect(Collectors.groupingBy(NodeDto::getDataCenter))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(NodeDto::getIp).toList()));
    }

    public List<String> getStorageList() {
        return nodeList.stream().map(NodeDto::getClusterName).distinct().toList();
    }

    public List<String> getIpList() {
        return nodeList.stream()
                .map(NodeDto::getIp)
                .distinct()
                .sorted(String::compareTo)
                .toList();
    }

    public List<String> getClusterList() {
        return nodeList.stream()
                .map(NodeDto::getClusterName)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getDataCenterList(String clusterName) {
        return nodeList.stream()
                .filter(e -> e.getClusterName().equals(clusterName))
                .map(NodeDto::getDataCenter)
                .distinct()
                .sorted()
                .toList();
    }

    public List<NodeDto> getIpByClusterList(String clusterName) {
        return nodeList.stream()
                .filter(e -> e.getClusterName().equals(clusterName))
                .distinct()
                .sorted(Comparator.comparing(NodeDto::getIp))
                .toList();
    }

    public List<NodeDto> getIpByClusterAndDataCenterList(String clusterName, String dataCenterName) {
        return nodeList.stream()
                .filter(e -> e.getClusterName().equals(clusterName) && e.getDataCenter().equals(dataCenterName))
                .distinct()
                .sorted(Comparator.comparing(NodeDto::getIp))
                .toList();
    }

    public NodeDto getNodeByIP(String ip) {
        return nodeList.stream()
                .filter(e -> e.getIp().equals(ip))
                .findFirst()
                .orElse(null);
    }
}
