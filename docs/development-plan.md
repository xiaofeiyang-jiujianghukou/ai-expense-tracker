# 开发计划 — AI Expense Tracker

> 按 Sprint 迭代执行。每个 Sprint 完成后更新状态。
> 关联文档: [project-requirements.md](project-requirements.md) | [architecture-design.md](architecture-design.md)

---

## Sprint 0：项目初始化

**状态**: ✅ 已完成

| 任务 | 描述 | 状态 |
|------|------|------|
| T001 | 创建项目目录结构 | ✅ |
| T002 | 编写 docs/project-design.md（原始设计） | ✅ |
| T003 | 创建 CLAUDE.md | ✅ |
| T004 | 创建 docs/project-requirements.md | ✅ |
| T005 | 创建 docs/architecture-design.md | ✅ |
| T006 | 创建 docs/development-plan.md | ✅ |
| T007 | 创建 docs/iteration-log.md | ✅ |
| T008 | 创建 docs/design/v1-design-doc.md | ✅ |
| T009 | 创建 .gitignore + 初始化 Git 仓库 | ✅ |
| T010 | 关联远程 + 首次推送 | ✅ |

---

## Sprint 1：项目骨架（后端）

**状态**: ⬜ 待开始

**目标**: Maven 多模块父 POM + expense-common + expense-server 可启动 + 数据库连通

| 任务 | 描述 | 关联 |
|------|------|------|
| T101 | 创建 Maven 多模块父 POM（版本管理 + 模块聚合） | NFR-11 |
| T102 | 创建 expense-common 模块（ApiResponse、BusinessException、ErrorCode、工具类） | NFR-04,08 |
| T103 | 创建 expense-security 模块（JwtTokenProvider、JwtAuthFilter、SecurityConfig） | SEC-01 |
| T104 | 创建 expense-server 启动模块（application.yml、入口类） | — |
| T105 | 配置多环境（dev/test）数据库连接 + Flyway | NFR-05 |
| T106 | 全局异常处理器 | NFR-08 |
| T107 | 基础包结构（user/category/transaction/statistics 空壳） | — |
| T108 | 验证项目可启动、数据库可连接、Flyway 可执行 | — |

**产出物**:
- 7 个 Maven 模块：common、security、user、category、transaction、statistics、server
- `mvn spring-boot:run` 启动成功
- 统一响应 + 全局异常 + JWT 基础设施就位

---

## Sprint 2：用户模块（后端）

**状态**: ⬜ 待开始

**目标**: 注册 + 登录 + JWT 认证链路打通

| 任务 | 描述 | 关联 |
|------|------|------|
| T201 | User Entity + Mapper | USR-01 |
| T202 | UserService（注册 + BCrypt 加密） | USR-01,03 |
| T203 | UserController（register + login 接口） | USR-01,02 |
| T204 | JwtTokenProvider 实现（生成/验证 Token、7天过期） | SEC-01,02 |
| T205 | SecurityConfig 配置（放行 /register /login，拦截其他） | SEC-01 |
| T206 | Flyway V1 迁移脚本 | NFR-05 |
| T207 | UserService 单元测试 | NFR-01 |
| T208 | UserController MockMvc 测试 | NFR-02 |
| T209 | 用户集成测试（TestContainers） | NFR-03 |

**产出物**:
- `POST /api/users/register` 注册成功
- `POST /api/users/login` 返回 JWT
- 所有后续 API 需 Token

---

## Sprint 3：分类模块（后端）

**状态**: ⬜ 待开始

**目标**: 分类 CRUD + 新用户注册自动创建默认分类

| 任务 | 描述 | 关联 |
|------|------|------|
| T301 | Category Entity + Mapper | CAT-01 |
| T302 | CategoryService（CRUD + 默认分类初始化） | CAT-01,02,03 |
| T303 | CategoryController | CAT-01,02 |
| T304 | 注册时自动创建默认分类（在 UserService 中调用） | CAT-03 |
| T305 | Flyway V2 迁移脚本 | NFR-05 |
| T306 | CategoryService 单元测试 | NFR-01 |
| T307 | CategoryController MockMvc 测试 | NFR-02 |
| T308 | 分类集成测试 | NFR-03 |

**产出物**:
- 分类 CRUD API 完成
- 新用户自动拥有 8 个默认分类

---

## Sprint 4：账单模块（后端核心）

**状态**: ⬜ 待开始

**目标**: 账单 CRUD + 多条件筛选分页 + 归属校验

| 任务 | 描述 | 关联 |
|------|------|------|
| T401 | Transaction Entity + Mapper（自定义查询 + 分页） | TXN-01,02 |
| T402 | TransactionService（CRUD + 筛选 + 归属校验） | TXN-01~05 |
| T403 | TransactionController | TXN-01~04 |
| T404 | Flyway V3 迁移脚本 | NFR-05 |
| T405 | TransactionService 单元测试 | NFR-01 |
| T406 | TransactionController MockMvc 测试 | NFR-02 |
| T407 | 账单集成测试 | NFR-03 |

**产出物**:
- 完整账单 CRUD API
- 支持 type / categoryId / startDate / endDate 筛选 + 分页

---

## Sprint 5：统计模块（后端）

**状态**: ⬜ 待开始

**目标**: 月度汇总 + 分类统计

| 任务 | 描述 | 关联 |
|------|------|------|
| T501 | StatisticsService（月度汇总 + 分类支出统计） | STAT-01,02 |
| T502 | StatisticsController | STAT-01,02 |
| T503 | StatisticsService 单元测试 | NFR-01 |
| T504 | StatisticsController MockMvc 测试 | NFR-02 |
| T505 | 统计集成测试 | NFR-03 |

**产出物**:
- 月度收入/支出/结余查询
- 分类支出金额汇总

---

## Sprint 6：前端项目骨架

**状态**: ⬜ 待开始

**目标**: Vue 3 项目初始化 + 路由 + 布局 + Axios 封装

| 任务 | 描述 | 关联 |
|------|------|------|
| T601 | `npm create vue@latest` 创建 Vite + Vue 3 项目 | NFR-12 |
| T602 | 安装依赖：Element Plus、Axios、Pinia、Vue Router | NFR-12 |
| T603 | Axios 实例封装（baseURL、请求/响应拦截器、Token 自动附加） | NFR-12,14 |
| T604 | Pinia user store（Token 存储、登录/登出） | SEC-01 |
| T605 | Vue Router 配置（路由表 + 导航守卫） | FE-01 |
| T606 | AppLayout 布局组件（侧边栏导航 + 顶部栏 + 内容区） | — |
| T607 | Vite proxy 配置（/api → localhost:8080） | NFR-14 |
| T608 | 登录页 + 注册页（纯前端，调用后端 API） | FE-01,02 |

**产出物**:
- 前端项目可启动，登录/注册可调通后端
- 路由守卫生效，未登录自动跳转

---

## Sprint 7：前端核心页面

**状态**: ⬜ 待开始

**目标**: 仪表盘 + 账单管理 + 分类管理三个页面

| 任务 | 描述 | 关联 |
|------|------|------|
| T701 | DashboardView：3 个金额卡片 + 最近 10 条账单列表 | FE-03 |
| T702 | TransactionList：筛选栏 + 表格 + 分页 + 新增/编辑弹窗 | FE-04 |
| T703 | CategoryManage：收入/支出标签页切换 + 列表 + 新增/编辑行 | FE-05 |
| T704 | 删除二次确认 + 操作 Toast 反馈 + Loading 状态 | NFR-13 |

**产出物**:
- 完整的账单管理闭环（查/增/改/删）
- 分类管理可用
- 仪表盘首页可见本月概览

---

## Sprint 8：前端统计页 + 整体联调

**状态**: ⬜ 待开始

**目标**: 月度统计页面 + 前后端全流程打通

| 任务 | 描述 | 关联 |
|------|------|------|
| T801 | MonthlyStats：月份选择 + 汇总卡片 + 分类明细列表 | FE-06 |
| T802 | 前后端全流程联调测试（注册→登录→记账→查看统计） | — |
| T803 | 边界情况处理（空数据、网络异常、Token 过期） | NFR-13 |

**产出物**:
- 所有页面可用
- 全流程走通

---

## Sprint 9：完善与 Review

**状态**: ⬜ 待开始

**目标**: 代码质量 + 文档 + 安全检查

| 任务 | 描述 |
|------|------|
| T901 | 后端代码 Review（规范、注释、异常处理） |
| T902 | 前端代码 Review（组件拆分、重复逻辑抽取） |
| T903 | 安全审查（密码加密、Token 过期、SQL 注入、XSS） |
| T904 | 性能审查（N+1 查询、数据库索引） |
| T905 | README.md 完善（启动说明、技术栈、项目结构） |
| T906 | API 文档整理 |

---

## 工时汇总

| Sprint | 内容 | 预估工时 |
|--------|------|----------|
| Sprint 0 | 项目初始化 | 已完成 |
| Sprint 1 | 后端骨架 | 8h |
| Sprint 2 | 用户模块 | 9h |
| Sprint 3 | 分类模块 | 7h |
| Sprint 4 | 账单模块 | 9h |
| Sprint 5 | 统计模块 | 4h |
| Sprint 6 | 前端骨架 | 8h |
| Sprint 7 | 前端核心页面 | 12h |
| Sprint 8 | 前端统计+联调 | 6h |
| Sprint 9 | 完善 Review | 8h |
| **合计** | | **~71h** |

---

## 执行策略

```
后端优先:
  Sprint 0 → Sprint 1 → Sprint 2 → Sprint 3 → Sprint 4 → Sprint 5
                                                              ↓
前端对接:                                              Sprint 6 → Sprint 7 → Sprint 8
                                                              ↓
收尾:                                                    Sprint 9
```

后端先用 Postman 自测，API 稳定后再启动前端开发，减少前后端返工。
