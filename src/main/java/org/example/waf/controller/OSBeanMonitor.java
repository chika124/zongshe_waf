package org.example.waf.controller;

import org.example.waf.service.SystemMonitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class OSBeanMonitor {

    private final SystemMonitorService systemMonitorService;

    public OSBeanMonitor(SystemMonitorService systemMonitorService) {
        this.systemMonitorService = systemMonitorService;
    }

    /**
     * 获取CPU使用率信息
     * @return CPU使用率信息
     */
    @GetMapping("/cpu")
    public ResponseEntity<Map<String, Object>> getCpuUsage() {
        Map<String, Object> cpuInfo = Map.of(
            "systemCpuUsage", systemMonitorService.getSystemCpuUsage(),
            "processCpuUsage", systemMonitorService.getProcessCpuUsage(),
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(cpuInfo);
    }

    /**
     * 获取内存使用率信息
     * @return 内存使用率信息
     */
    @GetMapping("/memory")
    public ResponseEntity<Map<String, Object>> getMemoryUsage() {
        Map<String, Object> memoryInfo = Map.of(
            "physicalMemoryUsage", systemMonitorService.getPhysicalMemoryUsage(),
            "heapMemoryUsage", systemMonitorService.getHeapMemoryUsage(),
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(memoryInfo);
    }

    /**
     * 获取磁盘使用率信息
     * @return 磁盘使用率信息
     */
    @GetMapping("/disk")
    public ResponseEntity<Map<String, Object>> getDiskUsage() {
        Map<String, Object> diskInfo = Map.of(
            "diskUsage", systemMonitorService.getDiskUsage(),
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(diskInfo);
    }

    /**
     * 获取活跃连接数
     * @return 活跃连接数信息
     */
    @GetMapping("/connections")
    public ResponseEntity<Map<String, Object>> getActiveConnections() {
        Map<String, Object> connectionsInfo = Map.of(
            "activeConnections", systemMonitorService.getActiveConnections(),
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(connectionsInfo);
    }

    /**
     * 获取完整的系统资源信息
     * @return 系统资源信息
     */
    @GetMapping("/resources")
    public ResponseEntity<Map<String, Object>> getSystemResources() {
        return ResponseEntity.ok(systemMonitorService.getSystemResources());
    }

    /**
     * 获取系统基本信息
     * @return 系统基本信息
     */
    @GetMapping("/system-info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        return ResponseEntity.ok(systemMonitorService.getSystemInfo());
    }

    /**
     * 获取网络接口信息
     * @return 网络接口信息
     */
    @GetMapping("/network-interfaces")
    public ResponseEntity<?> getNetworkInterfaces() {
        return ResponseEntity.ok(systemMonitorService.getNetworkInterfaces());
    }
}