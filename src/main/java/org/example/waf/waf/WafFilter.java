package org.example.waf.waf;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
public class WafFilter extends OncePerRequestFilter {
    private final WafService wafService;

    public WafFilter(WafService wafService) {
        this.wafService = wafService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!wafService.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        List<Rule> rules = wafService.listRules();
        String query = request.getQueryString();
        String path = request.getRequestURI();
        String lowerQuery = query == null ? "" : query.toLowerCase();
        String lowerPath = path == null ? "" : path.toLowerCase();

        for (Rule r : rules) {
            if (!r.isEnabled()) continue;
            String patt = r.getPattern();
            if (patt == null || patt.isEmpty()) continue;
            String lp = patt.toLowerCase();
            boolean hit = false;

            switch (r.getScope()) {
                case "query":
                    hit = lowerQuery.contains(lp);
                    break;
                case "path":
                    hit = lowerPath.contains(lp);
                    break;
                case "headers": {
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if (headerNames != null) {
                        while (headerNames.hasMoreElements()) {
                            String hn = headerNames.nextElement();
                            String hv = request.getHeader(hn);
                            if ((hn + ":" + hv).toLowerCase().contains(lp)) {
                                hit = true;
                                break;
                            }
                        }
                    }
                    break;
                }
                default:
                    // body 检查留作未来扩展
                    break;
            }

            if (hit) {
                if ("block".equalsIgnoreCase(r.getAction())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                    response.getWriter().write("403 Forbidden - Blocked by WAF rule\n");
                    return;
                }
                // log 或 allow 先直接放行（日志可后续接入）
                break;
            }
        }

        filterChain.doFilter(request, response);
    }
}


