package com.barezen.lab.service;

import com.barezen.lab.dao.LoanDao;
import com.barezen.lab.dao.StatsDao;
import com.barezen.lab.entity.Loan;

import java.util.List;
import java.util.Map;

/** 后台总览统计业务（管理员）：8 项指标 + 分布 + 最近借用 + 逾期 TOP */
public class StatsService {

    private final StatsDao statsDao = new StatsDao();
    private final LoanDao loanDao = new LoanDao();

    public int users() { return statsDao.countUsers(); }

    public int labs() { return statsDao.countLabs(); }

    /** 设备种类 */
    public int equipments() { return statsDao.countEquipments(); }

    /** 可借库存 */
    public int equipmentAvailable() { return statsDao.sumAvailable(); }

    public int loanTotal() { return statsDao.countLoans(); }

    public int loansActive() { return loanDao.countActive(); }

    public int loansOverdue() { return loanDao.countOverdue(); }

    public int loansReturned() { return statsDao.countReturned(); }

    /** 类别分布：类别 → 台数 */
    public Map<String, Integer> categoryDistribution() { return statsDao.categoryDistribution(); }

    /** 实验室分布：实验室 → 台数 */
    public Map<String, Integer> labDistribution() { return statsDao.labDistribution(); }

    public List<Loan> recentLoans(int limit) { return loanDao.findRecent(limit); }

    public List<Loan> overdueTop() { return loanDao.findOverdue(); }
}
