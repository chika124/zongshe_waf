USE waf_db;

CREATE TABLE IF NOT EXISTS db_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_timestamp DATETIME NOT NULL,
    log_level VARCHAR(20) NOT NULL,
    client_ip VARCHAR(50) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    url VARCHAR(255) NOT NULL,
    status_code INT NOT NULL,
    user_agent TEXT,
    rule_id VARCHAR(50),
    rule_name VARCHAR(100),
    action_taken VARCHAR(20) NOT NULL,
    threat_score INT,
    country_code VARCHAR(10),
    bytes_sent BIGINT,
    request_time DOUBLE,
    request_id VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 可选：添加索引以提高查询性能
CREATE INDEX idx_db_log_timestamp ON db_log(log_timestamp);
CREATE INDEX idx_db_log_client_ip ON db_log(client_ip);
CREATE INDEX idx_db_log_action_taken ON db_log(action_taken);
CREATE INDEX idx_db_log_rule_id ON db_log(rule_id);
