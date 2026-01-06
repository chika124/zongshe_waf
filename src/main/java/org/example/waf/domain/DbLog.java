package org.example.waf.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "waf_security_logs")
public class DbLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_timestamp")
    private Date logTimestamp;

    @Column(name = "log_level")
    private String logLevel;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "url")
    private String url;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "rule_id")
    private String ruleId;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "threat_score")
    private Integer threatScore;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "bytes_sent")
    private Integer bytesSent;

    @Column(name = "request_time")
    private Double requestTime;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "created_at")
    private Date createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getLogTimestamp() { return logTimestamp; }
    public void setLogTimestamp(Date logTimestamp) { this.logTimestamp = logTimestamp; }
    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    public Integer getThreatScore() { return threatScore; }
    public void setThreatScore(Integer threatScore) { this.threatScore = threatScore; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public Integer getBytesSent() { return bytesSent; }
    public void setBytesSent(Integer bytesSent) { this.bytesSent = bytesSent; }
    public Double getRequestTime() { return requestTime; }
    public void setRequestTime(Double requestTime) { this.requestTime = requestTime; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}