package org.example.waf.controller;

import org.example.waf.domain.Application;
import org.example.waf.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class ApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    private final ApplicationService applicationService;

    // 构造函数注入
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // 获取所有应用
    @GetMapping
    public ResponseEntity<?> getApplications() {
        try {
            List<Application> applications = applicationService.getAllApplications();
            return ResponseEntity.ok(new ApiResponse(200, "success", applications));
        } catch (Exception e) {
            logger.error("获取应用列表失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "获取应用列表失败", null));
        }
    }

    // 创建应用
    @PostMapping
    public ResponseEntity<?> createApplication(@RequestBody Application application) {
        try {
            logger.info("创建应用请求: name={}, domain={}", application.getName(), application.getDomain());
            
            // 参数校验
            if (application.getName() == null || application.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(400, "应用名称不能为空", null));
            }
            if (application.getDomain() == null || application.getDomain().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(400, "域名不能为空", null));
            }

            Application newApp = applicationService.createApplication(application);
            if (newApp == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(400, "该域名已存在", null));
            }
            
            logger.info("创建应用成功: id={}, name={}", newApp.getId(), newApp.getName());
            return ResponseEntity.ok(new ApiResponse(200, "success", newApp));
        } catch (Exception e) {
            logger.error("创建应用失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "创建应用失败", null));
        }
    }

    // 更新应用状态
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            logger.info("更新应用状态请求: id={}, status={}", id, request.getStatus());
            
            boolean success = applicationService.updateApplicationStatus(id, request.getStatus());
            if (success) {
                return ResponseEntity.ok(new ApiResponse(200, "success", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("更新应用状态失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "更新应用状态失败", null));
        }
    }

    // 删除应用
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        try {
            logger.info("删除应用请求: id={}", id);
            
            boolean success = applicationService.deleteApplication(id);
            if (success) {
                return ResponseEntity.ok(new ApiResponse(200, "success", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("删除应用失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "删除应用失败", null));
        }
    }

    // 内部静态类：API响应格式
    static class ApiResponse {
        private int code;
        private String message;
        private Object data;

        public ApiResponse(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        // getter方法
        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }

    // 内部静态类：状态更新请求
    static class StatusUpdateRequest {
        private String status;

        // getter和setter方法
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}