package com.detectlanguage.responses;

import java.util.Date;

public class AccountStatusResponse extends Response {
    private Date date;
    private Double requests;
    private Double bytes;
    private String plan;
    private Date plan_expires;
    private Double daily_requests_limit;
    private Double daily_bytes_limit;
    private String status;

    public Date getDate() {
        return date;
    }

    public Long getRequests() {
        return requests.longValue();
    }

    public Long getBytes() {
        return bytes.longValue();
    }

    public String getPlan() {
        return plan;
    }

    public Date getPlanExpires() {
        return plan_expires;
    }

    public Long getDailyRequestsLimit() {
        return daily_requests_limit.longValue();
    }

    public Long getDailyBytesLimit() {
        return daily_bytes_limit.longValue();
    }

    public String getStatus() {
        return status;
    }
}
