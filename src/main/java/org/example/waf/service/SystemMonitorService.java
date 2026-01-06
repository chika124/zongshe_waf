package org.example.waf.service;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

@Service
public class SystemMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorService.class);
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    public SystemMonitorService() {
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
    }

    /**
     * 获取系统CPU使用率
     * @return CPU使用率百分比
     */
    public double getSystemCpuUsage() {
        return osBean.getSystemCpuLoad() * 100;
    }

    /**
     * 获取进程CPU使用率
     * @return 进程CPU使用率百分比
     */
    public double getProcessCpuUsage() {
        return osBean.getProcessCpuLoad() * 100;
    }

    /**
     * 获取物理内存使用率
     * @return 物理内存使用率百分比
     */
    public double getPhysicalMemoryUsage() {
        long totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
        long freePhysicalMemory = osBean.getFreePhysicalMemorySize();
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
        return (double) usedPhysicalMemory * 100 / totalPhysicalMemory;
    }

    /**
     * 获取JVM堆内存使用率
     * @return JVM堆内存使用率百分比
     */
    public double getHeapMemoryUsage() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return (double) heapUsage.getUsed() * 100 / heapUsage.getMax();
    }

    /**
     * 获取磁盘使用率
     * @return 磁盘使用率百分比
     */
    public double getDiskUsage() {
        File[] roots = File.listRoots();
        long totalSpace = 0;
        long usedSpace = 0;

        for (File root : roots) {
            totalSpace += root.getTotalSpace();
            usedSpace += (root.getTotalSpace() - root.getFreeSpace());
        }

        return totalSpace > 0 ? (double) usedSpace * 100 / totalSpace : 0.0;
    }

    /**
     * 获取活跃连接数
     * @return 活跃连接数
     */
    public int getActiveConnections() {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                return getWindowsActiveConnections();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                return getUnixActiveConnections();
            }
        } catch (Exception e) {
            logger.error("获取活跃连接数失败: {}", e.getMessage());
        }
        
        return 0;
    }

    /**
     * 获取Windows系统活跃连接数
     * @return 活跃连接数
     * @throws IOException
     */
    private int getWindowsActiveConnections() throws IOException {
        Process process = Runtime.getRuntime().exec("netstat -an | find /c /v \"\"");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        reader.close();
        
        if (line != null) {
            return Integer.parseInt(line.trim());
        }
        return 0;
    }

    /**
     * 获取Unix/Linux/macOS系统活跃连接数
     * @return 活跃连接数
     * @throws IOException
     */
    private int getUnixActiveConnections() throws IOException {
        Process process = Runtime.getRuntime().exec("netstat -an | wc -l");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        reader.close();
        
        if (line != null) {
            return Integer.parseInt(line.trim());
        }
        return 0;
    }

    /**
     * 获取完整的系统资源信息
     * @return 系统资源信息Map
     */
    public Map<String, Object> getSystemResources() {
        Map<String, Object> resources = new HashMap<>();
        
        resources.put("systemCpuUsage", getSystemCpuUsage());
        resources.put("processCpuUsage", getProcessCpuUsage());
        resources.put("physicalMemoryUsage", getPhysicalMemoryUsage());
        resources.put("heapMemoryUsage", getHeapMemoryUsage());
        resources.put("diskUsage", getDiskUsage());
        resources.put("activeConnections", getActiveConnections());
        resources.put("timestamp", System.currentTimeMillis());
        
        return resources;
    }

    /**
     * 获取网络接口信息
     * @return 网络接口信息列表
     */
    public List<Map<String, Object>> getNetworkInterfaces() {
        List<Map<String, Object>> networkInterfaces = new ArrayList<>();
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();
                if (!netInterface.isUp() || netInterface.isLoopback()) {
                    continue;
                }
                
                Map<String, Object> interfaceInfo = new HashMap<>();
                interfaceInfo.put("name", netInterface.getName());
                interfaceInfo.put("displayName", netInterface.getDisplayName());
                
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                List<String> ipAddresses = new ArrayList<>();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    ipAddresses.add(address.getHostAddress());
                }
                interfaceInfo.put("ipAddresses", ipAddresses);
                
                networkInterfaces.add(interfaceInfo);
            }
        } catch (Exception e) {
            logger.error("获取网络接口信息失败: {}", e.getMessage());
        }
        
        return networkInterfaces;
    }

    /**
     * 获取系统信息
     * @return 系统信息Map
     */
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("osArch", System.getProperty("os.arch"));
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("javaVendor", System.getProperty("java.vendor"));
        systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        return systemInfo;
    }
}