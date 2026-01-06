package org.example.waf.waf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class RuleFileStore {

    private final ObjectMapper mapper;
    private final Path filePath;

    public static class PersistedConfig {
        public boolean enabled = true;
        public List<Rule> rules = new ArrayList<>();
    }

    public RuleFileStore(@Value("${app.waf.rules-file:./data/waf-rules.json}") String file) {
        // 处理路径，支持相对路径和绝对路径
        this.filePath = Paths.get(file).toAbsolutePath();
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public PersistedConfig load() throws IOException {
        // 1. 优先从外部文件加载
        if (Files.exists(filePath)) {
            byte[] bytes = Files.readAllBytes(filePath);
            if (bytes.length > 0) {
                return mapper.readValue(bytes, new TypeReference<PersistedConfig>(){});
            }
        }
        
        // 2. 如果外部文件不存在或为空，尝试从类路径加载默认规则
        try {
            ClassPathResource resource = new ClassPathResource("waf-rules.json");
            if (resource.exists()) {
                byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
                if (bytes.length > 0) {
                    return mapper.readValue(bytes, new TypeReference<PersistedConfig>(){});
                }
            }
        } catch (Exception e) {
            // 类路径加载失败，返回默认配置
        }
        
        // 3. 返回默认配置
        return new PersistedConfig();
    }

    public void save(boolean enabled, List<Rule> rules) throws IOException {
        PersistedConfig cfg = new PersistedConfig();
        cfg.enabled = enabled;
        cfg.rules = new ArrayList<>(rules);
        
        // 确保目录存在
        Path dir = filePath.getParent();
        if (dir != null && !Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        
        // 写入文件
        byte[] bytes = mapper.writeValueAsBytes(cfg);
        Files.write(filePath, bytes);
    }

    public Path getFilePath() {
        return filePath;
    }
}



