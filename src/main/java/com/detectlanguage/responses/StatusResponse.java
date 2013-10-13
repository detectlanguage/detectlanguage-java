package com.detectlanguage.responses;

import java.util.Date;

public class StatusResponse extends Response {
	private Date date;
	private Integer requests;
	private Integer bytes;
	private String plan;
	private Date plan_expires;
	private Integer daily_requests_limit;
	private Integer daily_bytes_limit;
	private String status;

	public Date getDate() {
		return date;
	}

	public Integer getRequests() {
		return requests;
	}

	public Integer getBytes() {
		return bytes;
	}

	public String getPlan() {
		return plan;
	}

	public Date getPlanExpires() {
		return plan_expires;
	}

	public Integer getDailyRequestsLimit() {
		return daily_requests_limit;
	}

	public Integer getDailyBytesLimit() {
		return daily_bytes_limit;
	}

	public String getStatus() {
		return status;
	}
}
