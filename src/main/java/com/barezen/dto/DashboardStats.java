package com.barezen.dto;

import java.util.List;

/** 后台总览统计数据。 */
public class DashboardStats {

    public long userCount;
    public long labCount;
    public long equipmentCount;
    public long equipmentAvailable;
    public long loanTotal;
    public long loanBorrowing;
    public long loanOverdue;
    public long loanReturned;
    public List<CategoryCount> categoryStats;
    public List<LabCount> labStats;
    public List<LoanResponse> recentLoans;
    public List<LoanResponse> overdueTop;

    public static class CategoryCount {
        public String name;
        public long count;

        public CategoryCount(String name, long count) {
            this.name = name;
            this.count = count;
        }
    }

    public static class LabCount {
        public Long labId;
        public String labName;
        public long count;

        public LabCount(Long labId, String labName, long count) {
            this.labId = labId;
            this.labName = labName;
            this.count = count;
        }
    }
}
