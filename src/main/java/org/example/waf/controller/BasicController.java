package org.example.waf.controller;

import org.example.waf.dto.LoginRequest;
import org.example.waf.dto.LoginResponse;
import org.example.waf.domain.User;
import org.example.waf.UserRepository;
import org.example.waf.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class BasicController { // 建议改为 AuthController

    private static final Logger logger = LoggerFactory.getLogger(BasicController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("登录请求: username={}, password length={}", 
                loginRequest.getUsername(), 
                loginRequest.getPassword() != null ? loginRequest.getPassword().length() : 0);

            // 参数校验
            ResponseEntity<?> validationError = validateLoginRequest(loginRequest);
            if (validationError != null) {
                return validationError;
            }

            // 查找用户（只通过用户名）
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

            if (!userOpt.isPresent()) {
                logger.warn("用户不存在: username={}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "用户不存在"));
            }

            User user = userOpt.get();

            // 检查状态（如有 status 字段）
            if (!isUserActive(user)) {
                logger.warn("用户被禁用: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "账户已被禁用"));
            }

            // 验证密码
            // 检查密码是否已加密（以$2a$开头表示BCrypt加密）
            boolean passwordValid;
            if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
                // 密码已加密，使用BCrypt验证
                try {
                    passwordValid = BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());
                } catch (Exception e) {
                    logger.warn("BCrypt验证失败: {}", e.getMessage());
                    passwordValid = false;
                }
            } else {
                // 密码是明文，直接比较
                passwordValid = loginRequest.getPassword().equals(user.getPassword());
            }
            
            if (!passwordValid) {
                logger.warn("密码错误: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "密码错误"));
            }

            // 生成 token
            String token = JwtUtil.generateToken(String.valueOf(user.getId()), user.getUsername());

            // 构建成功响应（用新的构造器）
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
            LoginResponse response = new LoginResponse(token, 3600L, userInfo);

            logger.info("用户登录成功: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("登录异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(false, "系统错误，请稍后重试"));
        }
    }

    // 参数校验
    private ResponseEntity<?> validateLoginRequest(LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "用户名不能为空"));
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "密码不能为空"));
        }

        if (loginRequest.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(false, "密码长度不能少于6位"));
        }

        return null;
    }

    // 用户状态检查
    private boolean isUserActive(User user) {
        return true; // 没有 status 字段就默认 true
    }

    // Token 校验
    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "Token无效"));
            }

            String token = authHeader.substring(7);
            if (JwtUtil.validateToken(token)) {
                return ResponseEntity.ok(new LoginResponse(true, "Token有效"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "Token已过期"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Token验证失败"));
        }
    }
}
