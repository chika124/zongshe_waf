package org.example.waf.dto;

public class LoginResponse {
    private boolean success;   // 是否成功
    private String message;    // 提示信息
    private String token;      // 登录成功时返回的 JWT token
    private Long expiresIn;    // token 有效期（秒）
    private UserInfo user;     // 用户信息

    // 构造函数
    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(String token, Long expiresIn, UserInfo user) {
        this.success = true;
        this.message = "登录成功";
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // getters and setters
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUser() {
        return user;
    }
    public void setUser(UserInfo user) {
        this.user = user;
    }

    // 内部类 UserInfo
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;

        public UserInfo(String id, String username, String role) {}

        public UserInfo(Long id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
    }
}
