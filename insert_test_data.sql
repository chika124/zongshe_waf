USE waf_db;

-- 插入测试日志数据
INSERT INTO db_log (
    log_timestamp, log_level, client_ip, http_method, url, 
    status_code, user_agent, rule_id, rule_name, action_taken, 
    threat_score, country_code, bytes_sent, request_time, request_id
) VALUES
-- SQL注入攻击
('2025-01-15 14:32:15', 'attack', '192.168.1.100', 'GET', '/api/users?id=1 UNION SELECT * FROM users', 
 403, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/96.0.4664.110', 'SQL_INJECTION_001', 'SQL Injection Detection Rule', 'block', 
 95, 'CN', 450, 0.123, 'req-20250115-001'),

-- XSS攻击
('2025-01-15 14:28:42', 'attack', '10.0.0.25', 'GET', '/search?q=javascript:alert("xss")', 
 403, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/15.1', 'XSS_002', 'XSS Attack Detection Rule', 'block', 
 85, 'US', 320, 0.095, 'req-20250115-002'),

-- 路径遍历攻击
('2025-01-15 13:45:18', 'attack', '172.16.0.50', 'GET', '/files/../config/password.txt', 
 403, 'Python-urllib/3.9', 'PATH_TRAVERSAL_001', 'Path Traversal Detection Rule', 'block', 
 90, 'JP', 280, 0.078, 'req-20250115-003'),

-- 正常请求
('2025-01-15 14:35:22', 'info', '192.168.1.100', 'GET', '/api/users', 
 200, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/96.0.4664.110', NULL, NULL, 'allow', 
 NULL, 'CN', 1250, 0.187, 'req-20250115-004'),

-- SQL注入攻击 - 低危
('2025-01-15 14:40:55', 'attack', '192.168.1.101', 'POST', '/api/login', 
 403, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Edge/96.0.1054.43', 'SQL_INJECTION_002', 'SQL Injection Detection Rule', 'alert', 
 55, 'CN', 420, 0.156, 'req-20250115-005'),

-- 命令注入攻击
('2025-01-15 14:45:10', 'attack', '203.0.113.75', 'POST', '/api/exec?cmd=ls -la', 
 403, 'curl/7.79.1', 'CMD_INJECTION_001', 'Command Injection Detection Rule', 'block', 
 98, 'GB', 380, 0.112, 'req-20250115-006'),

-- 正常登录
('2025-01-15 14:50:33', 'info', '192.168.1.100', 'POST', '/api/login', 
 200, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/96.0.4664.110', NULL, NULL, 'allow', 
 NULL, 'CN', 650, 0.234, 'req-20250115-007'),

-- 扫描器检测
('2025-01-15 14:55:47', 'attack', '198.51.100.20', 'GET', '/wp-admin/', 
 403, 'Nessus Scanner/8.15.0', 'SCANNER_001', 'Scanner Detection Rule', 'block', 
 75, 'DE', 410, 0.089, 'req-20250115-008');
