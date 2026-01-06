package org.example.waf;

import org.example.waf.domain.DbLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DbLogRepository extends JpaRepository<DbLog, Long> {
    // 根据时间范围查询日志
    List<DbLog> findByLogTimestampBetween(Date startDate, Date endDate);
    
    // 根据状态码查询日志
    List<DbLog> findByStatusCode(Integer statusCode);
    
    // 根据攻击类型查询日志
    List<DbLog> findByLogLevelAndActionTaken(String logLevel, String actionTaken);
    
    // 根据国家代码查询日志
    List<DbLog> findByCountryCode(String countryCode);
    
    // 查询所有被拦截的日志
    List<DbLog> findByActionTaken(String actionTaken);
    
    // 根据时间查询之后的日志
    List<DbLog> findByLogTimestampAfter(Date timestamp);
    
    // 根据时间和处理结果查询日志
    List<DbLog> findByLogTimestampAfterAndActionTaken(Date timestamp, String actionTaken);
}