# 🧊 项目已封板 — Monolith End-of-Life

> **封板日期**: 2026-07-12
> **最终版本**: V3.0
> **封板原因**: V4.0 工程化改造（Docker / K8s / CI/CD / 云部署 / 微服务拆分）

---

## 项目归档说明

本项目（`ai-expense-tracker`）已完成 V1.0 ~ V3.0 全部功能开发并验收通过，作为**单体应用最终版本**封板留存。

后续 V4.0 微服务化改造将在新项目中进行：

> 🔗 **新项目**: `ai-expense-tracker-cloud`（https://github.com/xiaofeiyang-jiujianghukou/ai-expense-tracker-cloud）

## 版本历史

| 版本 | 主题 | 关键产出 |
|------|------|----------|
| V1.0 | 基础记账 | 注册/登录、收支记录、分类管理、财务统计、前后端分离 |
| V2.0 | 智能化 | AI 自动分类、消费洞察、财务报告、AgentScope 集成、Redis 缓存 |
| V3.0 | 数据能力 | ECharts 可视化、预算管理、AI 预算建议、CSV/Excel/PDF 导出 |

## 项目快照

```
后端: Spring Boot 3.x + MyBatis-Plus + JWT + Flyway + MySQL 8.0
前端: Vue 3 + Vite + Element Plus + ECharts + Pinia
模块: expense-server/expense-common/expense-security/expense-user/
       expense-category/expense-bill/expense-statistics/expense-ai/expense-budget
部署: 单体 jar (mvn package → java -jar)
```

---

**V1.0 ~ V3.0 使命完成。再见，单体时代。你好，云原生。**
