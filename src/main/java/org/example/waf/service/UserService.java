package org.example.waf.service;

import org.example.waf.domain.User;
import org.example.waf.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    // 使用强密码加密策略
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // 更高的工作因子增强安全性

    // 注册逻辑
    public User registerUser(String username, String email, String password) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已被注册
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // 🔐 使用 BCrypt 加密密码
        user.setPassword(encoder.encode(password));

        // 默认角色在实体类里已是 "user"
        return userRepository.save(user);
    }
}
