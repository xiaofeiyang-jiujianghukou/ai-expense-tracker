# 迭代日志 — AI Expense Tracker

> 记录项目需求、架构设计、开发计划的每一次变更。
> 关联文档: [project-requirements.md](project-requirements.md) | [architecture-design.md](architecture-design.md) | [development-plan.md](development-plan.md)

---

## 迭代记录

### #007 — 2026-07-11 | 前端全部完成 + 接口验证 + 代码 Review

**类型**: 实现 + Review

**内容**:
- Sprint 6: Vue 3 前端骨架（Vite + Element Plus + Router + Pinia + Axios）
- Sprint 7: 仪表盘、账单管理、分类管理页面
- Sprint 8: 月度统计页面
- Sprint 9: 代码规范检查、安全审查、README
- 后端 13 个接口全部验证通过
- 修复: TransactionManager Bean 名冲突、Flyway checksum 不匹配、createdTime null
- 新增规范: 仅 GetMapping(单参) + PostMapping、写操作返回 void
- 新增规范: 禁止全限定类名内联、时间戳由 DB 管理

**文档版本**:
- development-plan.md: 全部 Sprint 标记完成
- README.md: 新建

---

### #006 — 2026-07-11 | 开发规范沉淀：建表注释 + 配置管理

**类型**: 规范

**变更原因**: Sprint 1-4 开发过程中发现缺失强制规范：建表无注释、敏感信息硬编码风险。

**内容**:
- v1-design-doc.md 新增 §4.8 数据库建表规范（表/字段 COMMENT 强制、索引命名、字符集引擎统一）
- v1-design-doc.md 新增 §4.9 配置管理规范（项目级 vs 部署级分层、敏感信息走环境变量无默认值）
- CLAUDE.md 新增 §5.4、§5.5 引用规范
- project-requirements.md 新增 NFR-15、NFR-16
- 反模式新增：建表不加 COMMENT、敏感信息硬编码

**文档版本**:
- v1-design-doc.md: 1.3 → 1.4
- project-requirements.md: 1.3 → 1.4
- CLAUDE.md: 同步更新

---

### #005 — 2026-07-11 | 去掉 interface + impl 鸡肋拆分

**类型**: 设计规范

**变更原因**: Service 和 Manager 默认只有一个实现，强行定义 interface + impl 增加无意义的样板代码。只在需要多态（策略模式、多实现）时才提取接口。

**内容**:
- Service 和 Manager 目录去掉 `impl/` 子目录，类即为具体实现
- 命名：`UserService`（不是 `UserService` + `UserServiceImpl`），`UserManager`（不是 `UserManager` + `UserManagerImpl`）
- 代码示例中 `@Manager` 改为 `@Component`（Spring 无 @Manager 注解）
- 新增反模式："强行 interface + impl — 只有一个实现时纯属鸡肋"
- 更新 CLAUDE.md、architecture-design.md、v1-design-doc.md 的所有相关描述

**文档版本**:
- architecture-design.md: 2.1 → 2.2（去掉 impl/ 目录结构 + AD-11）
- v1-design-doc.md: 1.2 → 1.3（命名规范精简）
- CLAUDE.md: 同步更新

---

### #004 — 2026-07-11 | 引入 Manager 编排层

**类型**: 架构设计

**变更原因**: Controller 和 Service 之间缺少编排层，Service 承担编排职责容易越界调用、职责不清。增加 Manager 层使分层更符合国内企业级标准。

**内容**:
- **新增 Manager 层**：位于 Controller 和 Service 之间
  - Service：专注单一原子业务，不跨模块调用
  - Manager：编排多个 Service，处理复合业务逻辑，控制事务
  - Controller：只做参数校验和路由，委托 Manager 处理
- **Manager 位置策略**：模块内编排放模块 `manager/`，跨模块编排放 expense-server 的 `manager/`
- **模块目录结构更新**：每个业务模块新增 `manager/` 及 `manager/impl/`
- **更新文档**：
  - CLAUDE.md：重写分层职责表，新增 Manager vs Service 边界示例
  - architecture-design.md：新增第 3 节"分层架构与 Manager 层"，含代码示例和位置策略
  - v1-design-doc.md：更新调用链路、模块结构、命名规范、反模式
  - project-requirements.md：NFR-06 分层描述更新

**文档版本**:
- project-requirements.md: 1.2 → 1.3（NFR-06 更新）
- architecture-design.md: 2.0 → 2.1（新增 Manager 层章节 + AD-10）
- v1-design-doc.md: 1.1 → 1.2（Manager 层设计）
- development-plan.md: 2.0（未变更）

---

### #003 — 2026-07-11 | 架构重大补充：前端 + 企业级技术栈 + 多模块设计

**类型**: 架构设计

**变更原因**: 架构遗漏前端交互层，技术栈未对齐国内企业标准，缺少微服务拆分预备设计。

**内容**:
- **新增前端架构**：Vue 3 + Vite + Element Plus + Axios + Pinia + Vue Router
  - 6 个页面：登录、注册、仪表盘、账单管理、分类管理、月度统计
  - 前后端分离，开发环境 Vite proxy 联调，生产 Nginx
  - 前端路由守卫 + Axios 拦截器自动附加 Token
- **ORM 替换**：Spring Data JPA → MyBatis-Plus 3.5+
- **新增 Lombok**：减少 Entity/DTO 样板代码
- **Maven 多模块设计**：
  - backend/ 下 7 个子模块：common、security、user、category、transaction、statistics、server
  - 业务模块间禁止直接依赖，通过 server 层编排
  - V4 微服务拆分时只改 POM 和启动类，业务代码不变
- **项目结构调整**：design/ 移至 docs/design/
- **全面更新文档**：
  - CLAUDE.md：新增前端技术栈、多模块目录结构、前端分层规范
  - project-requirements.md：新增 FE-01~06 前端需求、NFR-11~14
  - architecture-design.md：完全重写，新增前端架构、多模块设计、微服务拆分路径
  - v1-design-doc.md：新增前端页面效果描述、前端设计原则、前端校验规范
  - development-plan.md：扩展为 10 个 Sprint（后端 5 + 前端 4），~71h

**文档版本**:
- project-requirements.md: 1.1 → 1.2（新增 FE 需求 + NFR-11~14）
- architecture-design.md: 1.0 → 2.0（重写：前端架构 + 多模块 + MyBatis-Plus）
- v1-design-doc.md: 1.0 → 1.1（新增前端设计 + 规范更新）
- development-plan.md: 1.0 → 2.0（Sprint 0→5 扩展为 0→9）

---

### #002 — 2026-07-11 | 创建设计稿和开发设计规范

**类型**: 设计

**内容**:
- 创建 `docs/design/` 设计稿目录
- 创建 `docs/design/v1-design-doc.md` V1.0 设计稿（原在 design/，后移至 docs/design/）
- 更新 `project-requirements.md`：新增 NFR-07 ~ NFR-10 设计规范需求
- 更新 `CLAUDE.md`：新增设计稿索引

---

### #001 — 2026-07-11 | 项目初始化

**类型**: 初始化

**内容**:
- 创建项目目录结构
- 编写原始设计文档 `docs/project-design.md`
- 创建 `CLAUDE.md`、需求文档、架构文档、开发计划、迭代日志

---

## 文档版本追踪

| 文档 | 当前版本 | 最后更新 | 变更次数 |
|------|----------|----------|----------|
| project-requirements.md | 1.3 | 2026-07-11 | 4 |
| architecture-design.md | 2.2 | 2026-07-11 | 4 |
| development-plan.md | 2.0 | 2026-07-11 | 2 |
| v1-design-doc.md | 1.3 | 2026-07-11 | 4 |
