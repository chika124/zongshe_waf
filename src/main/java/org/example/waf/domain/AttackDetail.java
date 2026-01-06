package org.example.waf.domain;



public class AttackDetail {
    private Long id;
    private String time;
    private String ip;
    private String type;
    private String location;
    private String status;
    private String severity;
    private String rule;
    private String method;
    private String url;
    private String userAgent;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getRule() { return rule; }
    public void setRule(String rule) { this.rule = rule; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
