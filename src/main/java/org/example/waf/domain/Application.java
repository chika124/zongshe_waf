package org.example.waf.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Application {
    private Long id;
    private String name;
    private String domain;
    private String status; // enabled 或 disabled
    private Integer totalRequests;
    private Integer blockedRequests;
    private Integer attackCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // 无参构造函数
    public Application() {
    }

    // 有参构造函数
    public Application(Long id, String name, String domain, String status, Integer totalRequests, Integer blockedRequests, Integer attackCount, Date createTime) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.status = status;
        this.totalRequests = totalRequests;
        this.blockedRequests = blockedRequests;
        this.attackCount = attackCount;
        this.createTime = createTime;
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getBlockedRequests() {
        return blockedRequests;
    }

    public void setBlockedRequests(Integer blockedRequests) {
        this.blockedRequests = blockedRequests;
    }

    public Integer getAttackCount() {
        return attackCount;
    }

    public void setAttackCount(Integer attackCount) {
        this.attackCount = attackCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}