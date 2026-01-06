package org.example.waf.controller;

import org.example.waf.domain.User;
import org.example.waf.service.UserService;
import org.example.waf.util.JwtUtil;
import org.example.waf.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;
    
    // 用户名正则：3-20个字符，只能包含字母、数字和下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    // 密码正则：8-20个字符，至少包含字母、数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,20}$");

    // 注册接口 - 适配前端请求格式
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            // 参数非空验证
            if (username == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LoginResponse(false, "缺少必要参数"));
            }
            
            // 用户名格式验证
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LoginResponse(false, "用户名格式错误：3-20个字符，只能包含字母、数字和下划线"));
            }
            
            // 密码强度验证
            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LoginResponse(false, "密码强度不足：8-20个字符，至少包含字母和数字"));
            }

            // 生成安全的默认邮箱：用户名+随机字符串@example.com
            String email = username + "_" + System.currentTimeMillis() + "@example.com";

            // 调用注册服务
            User user = userService.registerUser(username, email, password);

            // 生成JWT token
            String token = JwtUtil.generateToken(String.valueOf(user.getId()), user.getUsername());

            // 构建与登录接口相同格式的响应
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
            LoginResponse response = new LoginResponse(token, 3600L, userInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 避免泄露具体错误信息给用户
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse(false, "注册失败，请检查输入信息"));
        }
    }
}

