package org.example.waf.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.waf.domain.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    private final List<Application> applications = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final ObjectMapper objectMapper;
    private final Path dataFilePath;

    public ApplicationService(@Value("${app.applications.file:./data/applications.json}") String filePath) {
        this.objectMapper = new ObjectMapper();
        this.dataFilePath = Paths.get(filePath).toAbsolutePath();
        loadApplications();
    }

    // 从文件加载应用数据
    private void loadApplications() {
        try {
            // 1. 优先从外部文件加载
            if (Files.exists(dataFilePath)) {
                List<Application> loadedApps = objectMapper.readValue(dataFilePath.toFile(), new TypeReference<List<Application>>() {});
                applications.clear();
                applications.addAll(loadedApps);
                // 更新ID生成器
                if (!applications.isEmpty()) {
                    long maxId = applications.stream().mapToLong(Application::getId).max().orElse(0);
                    idGenerator.set(maxId + 1);
                }
                logger.info("从外部文件加载应用数据成功，共{}个应用", applications.size());
            } else {
                // 2. 尝试从类路径加载默认数据
                try {
                    ClassPathResource resource = new ClassPathResource("applications.json");
                    if (resource.exists()) {
                        List<Application> loadedApps = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Application>>() {});
                        applications.clear();
                        applications.addAll(loadedApps);
                        // 更新ID生成器
                        if (!applications.isEmpty()) {
                            long maxId = applications.stream().mapToLong(Application::getId).max().orElse(0);
                            idGenerator.set(maxId + 1);
                        }
                        logger.info("从类路径加载应用数据成功，共{}个应用", applications.size());
                    } else {
                        // 3. 初始化默认数据
                        initDefaultApplications();
                        saveApplications();
                    }
                } catch (Exception e) {
                    // 类路径加载失败，初始化默认数据
                    initDefaultApplications();
                    saveApplications();
                }
            }
        } catch (IOException e) {
            logger.error("加载应用数据失败: {}", e.getMessage());
            // 初始化默认数据
            initDefaultApplications();
        }
    }

    // 保存应用数据到文件
    private void saveApplications() {
        try {
            // 确保父目录存在
            Path parentDir = dataFilePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            objectMapper.writeValue(dataFilePath.toFile(), applications);
            logger.info("保存应用数据到文件成功，文件路径: {}", dataFilePath);
        } catch (IOException e) {
            logger.error("保存应用数据失败: {}", e.getMessage());
        }
    }

    // 初始化默认应用数据
    private void initDefaultApplications() {
        applications.clear();
        
        Application app1 = new Application();
        app1.setId(idGenerator.incrementAndGet());
        app1.setName("API服务");
        app1.setDomain("api.demo.com");
        app1.setStatus("enabled");
        app1.setTotalRequests(12560);
        app1.setBlockedRequests(1234);
        app1.setAttackCount(89);
        app1.setCreateTime(new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000)); // 30天前
        applications.add(app1);

        Application app2 = new Application();
        app2.setId(idGenerator.incrementAndGet());
        app2.setName("管理后台");
        app2.setDomain("admin.demo.com");
        app2.setStatus("enabled");
        app2.setTotalRequests(8560);
        app2.setBlockedRequests(456);
        app2.setAttackCount(23);
        app2.setCreateTime(new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)); // 10天前
        applications.add(app2);

        Application app3 = new Application();
        app3.setId(idGenerator.incrementAndGet());
        app3.setName("Web前端");
        app3.setDomain("www.demo.com");
        app3.setStatus("disabled");
        app3.setTotalRequests(25600);
        app3.setBlockedRequests(1890);
        app3.setAttackCount(156);
        app3.setCreateTime(new Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000)); // 15天前
        applications.add(app3);

        Application app4 = new Application();
        app4.setId(idGenerator.incrementAndGet());
        app4.setName("移动端API");
        app4.setDomain("mobile-api.demo.com");
        app4.setStatus("enabled");
        app4.setTotalRequests(18900);
        app4.setBlockedRequests(890);
        app4.setAttackCount(67);
        app4.setCreateTime(new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000)); // 5天前
        applications.add(app4);

        logger.info("初始化默认应用数据成功，共{}个应用", applications.size());
    }

    // 获取所有应用
    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }

    // 根据ID获取应用
    public Application getApplicationById(Long id) {
        return applications.stream()
                .filter(app -> app.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 创建应用
    public Application createApplication(Application application) {
        // 检查域名是否已存在
        boolean domainExists = applications.stream()
                .anyMatch(app -> app.getDomain().equalsIgnoreCase(application.getDomain()));
        if (domainExists) {
            return null; // 域名已存在
        }

        Application newApp = new Application();
        newApp.setId(idGenerator.incrementAndGet());
        newApp.setName(application.getName());
        newApp.setDomain(application.getDomain());
        newApp.setStatus("enabled"); // 默认启用
        newApp.setTotalRequests(0);
        newApp.setBlockedRequests(0);
        newApp.setAttackCount(0);
        newApp.setCreateTime(new Date());
        
        applications.add(newApp);
        saveApplications();
        return newApp;
    }

    // 更新应用状态
    public boolean updateApplicationStatus(Long id, String status) {
        Application app = getApplicationById(id);
        if (app != null) {
            app.setStatus(status);
            saveApplications();
            return true;
        }
        return false;
    }

    // 删除应用
    public boolean deleteApplication(Long id) {
        boolean removed = applications.removeIf(app -> app.getId().equals(id));
        if (removed) {
            saveApplications();
        }
        return removed;
    }
}