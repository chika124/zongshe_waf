package org.example.waf.dto;



import java.util.List;
import java.util.Map;

public class ReportResponse {
    private int code;
    private String message;
    private Map<String, Object> data;

    public ReportResponse(int code, String message, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, Object> getData() { return data; }
}
