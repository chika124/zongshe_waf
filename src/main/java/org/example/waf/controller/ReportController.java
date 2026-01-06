package org.example.waf.controller;

import org.example.waf.DbLogRepository;
import org.example.waf.domain.AttackDetail;
import org.example.waf.domain.DbLog;
import org.example.waf.domain.ReportSummary;
import org.example.waf.dto.ReportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private DbLogRepository dbLogRepository;

    @GetMapping("/report")
    public ReportResponse getReportData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws ParseException {
        logger.info("获取报告数据，参数：startDate={}, endDate={}, page={}, size={}", startDate, endDate, page, size);
        
        // 查询数据库日志数据
        List<DbLog> dbLogs = new ArrayList<>();
        
        try {
            if (startDate != null && endDate != null) {
                // 解析日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                
                // 设置结束日期为当天的23:59:59
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                end = calendar.getTime();
                
                logger.info("按日期范围查询日志：start={}, end={}", start, end);
                dbLogs = dbLogRepository.findByLogTimestampBetween(start, end);
            } else {
                // 如果没有提供日期范围，查询所有日志
                logger.info("查询所有日志");
                dbLogs = dbLogRepository.findAll();
            }
            
            logger.info("查询到的日志数量：{}", dbLogs.size());
            
            // 计算汇总数据
            ReportSummary summary = new ReportSummary();
            summary.setTotalAttacks(dbLogs.size());
            
            // 统计被拦截的攻击
            long blockedAttacks = dbLogs.stream()
                    .filter(log -> log != null && "block".equals(log.getActionTaken()))
                    .count();
            summary.setBlockedAttacks((int) blockedAttacks);
            
            // 计算拦截率
            double blockRate = dbLogs.size() > 0 ? (blockedAttacks * 100.0) / dbLogs.size() : 0;
            summary.setBlockRate(Math.round(blockRate * 10) / 10.0); // 保留一位小数
            
            // 统计高危攻击
            long highRiskAttacks = dbLogs.stream()
                    .filter(log -> log != null && log.getThreatScore() != null && log.getThreatScore() >= 80)
                    .count();
            summary.setHighRiskAttacks((int) highRiskAttacks);

            // 将DbLog转换为AttackDetail
            List<AttackDetail> attackList = new ArrayList<>();
            for (DbLog log : dbLogs) {
                if (log == null) {
                    logger.warn("发现空的DbLog对象");
                    continue;
                }
                
                AttackDetail attack = new AttackDetail();
                attack.setId(log.getId());
                
                // 处理时间戳（添加空值检查）
                if (log.getLogTimestamp() != null) {
                    attack.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(log.getLogTimestamp()));
                } else {
                    attack.setTime(""); // 或设置默认值
                }
                
                attack.setIp(log.getClientIp());
                
                // 从ruleName中提取攻击类型
                String attackType = "未知攻击";
                if (log.getRuleName() != null) {
                    if (log.getRuleName().contains("SQL") || log.getRuleName().contains("SQLi")) {
                        attackType = "SQL注入";
                    } else if (log.getRuleName().contains("XSS")) {
                        attackType = "XSS攻击";
                    } else if (log.getRuleName().contains("Scanner")) {
                        attackType = "扫描器检测";
                    } else {
                        attackType = log.getRuleName();
                    }
                }
                attack.setType(attackType);
                
                // 根据国家代码生成地理位置
                String location = "未知地区";
                if (log.getCountryCode() != null) {
                    if ("CN".equals(log.getCountryCode())) {
                        location = "中国";
                    } else if ("US".equals(log.getCountryCode())) {
                        location = "美国";
                    } else if ("JP".equals(log.getCountryCode())) {
                        location = "日本";
                    } else if ("DE".equals(log.getCountryCode())) {
                        location = "德国";
                    } else if ("GB".equals(log.getCountryCode())) {
                        location = "英国";
                    } else if ("DEBUG".equals(log.getCountryCode())) {
                        location = "本地调试";
                    }
                }
                attack.setLocation(location);
                
                // 设置状态
                String status = "允许";
                if (log.getActionTaken() != null) {
                    if ("block".equals(log.getActionTaken())) {
                        status = "已拦截";
                    } else if ("alert".equals(log.getActionTaken())) {
                        status = "记录";
                    }
                }
                attack.setStatus(status);
                
                // 设置风险等级
                String severity = "低危";
                if (log.getThreatScore() != null) {
                    if (log.getThreatScore() >= 80) {
                        severity = "高危";
                    } else if (log.getThreatScore() >= 50) {
                        severity = "中危";
                    }
                }
                attack.setSeverity(severity);
                
                // 设置其他字段（添加空值检查）
                attack.setRule(log.getRuleId() != null ? log.getRuleId() : "");
                attack.setMethod(log.getHttpMethod() != null ? log.getHttpMethod() : "");
                attack.setUrl(log.getUrl() != null ? log.getUrl() : "");
                attack.setUserAgent(log.getUserAgent() != null ? log.getUserAgent() : "");
                
                attackList.add(attack);
            }

            // 模拟分页信息
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("total", dbLogs.size());
            pagination.put("page", page);
            pagination.put("size", size);
            pagination.put("totalPages", (int) Math.ceil(dbLogs.size() * 1.0 / size));

            // 组装最终数据
            Map<String, Object> data = new HashMap<>();
            data.put("summary", summary);
            data.put("attackDetails", attackList);
            data.put("pagination", pagination);

            logger.info("报告数据生成成功：totalAttacks={}, blockedAttacks={}, attackListSize={}", 
                    summary.getTotalAttacks(), summary.getBlockedAttacks(), attackList.size());
            
            return new ReportResponse(200, "success", data);
        } catch (Exception e) {
            logger.error("生成报告数据失败：", e);
            // 返回错误响应
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return new ReportResponse(500, "error", errorData);
        }
    }
}