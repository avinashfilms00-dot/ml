package com.nirman.ledger.model;

import java.math.BigDecimal;

public class WorkerResponse {
    private Long id;
    private String name;
    private String mobile;
    private String skill;
    private BigDecimal dailyWage;
    private boolean active;
    private Long siteId;
    private String siteName;

    public WorkerResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public BigDecimal getDailyWage() { return dailyWage; }
    public void setDailyWage(BigDecimal dailyWage) { this.dailyWage = dailyWage; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
}
