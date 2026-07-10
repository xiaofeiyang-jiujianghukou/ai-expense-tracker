# CLAUDE.md — AI Expense Tracker

> 本文档是 Claude Code 的项目工作指南。每次会话开始时，Claude Code 会先阅读本文档以理解项目全貌。
> 工作流：**Analyze（分析） → Design（设计） → Implement（实现） → Review（审查）**

---

## 1. 项目概况

**AI Expense Tracker**（AI 智能个人财务管理系统）是一个前后端分离的个人财务管理应用。

- **当前版本**: V1.0 MVP
- **核心目标**: 提供可交互的个人记账能力 — 注册/登录、收支记录、分类管理、财务统计
- **项目阶段**: Sprint 0 — 项目初始化（进行中）
- **架构模式**: 前后端分离 + Maven 多模块（V1 单体部署，架构预留微服务拆分能力）

---

## 2. 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 语言 |
| Spring Boot | 3.x | 框架 |
| Spring MVC | 6.x | Web 层 |
| MyBatis-Plus | 3.5+ | ORM（替代 JPA） |
| Spring Security | 6.x | 安全框架 |
| JWT (jjwt) | 0.12+ | 无状态认证 |
| MySQL | 8.0 | 数据库 |
| Flyway | 10.x | 数据库版本管理 |
| Lombok | 1.18+ | 减少样板代码 |
| Maven | 3.x | 构建 & 多模块管理 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 前端框架（Composition API） |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.x | UI 组件库 |
| Axios | 1.x | HTTP 客户端 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由 |

### 测试

| 技术 | 用途 |
|------|------|
| JUnit 5 | 单元测试 |
| Mockito | Mock 测试 |
| TestContainers | 集成测试（真实 MySQL） |

---

## 3. 关键文档索引

| 文档 | 路径 | 说明 |
|------|------|------|
| 项目需求 | [docs/project-requirements.md](docs/project-requirements.md) | 唯一需求真相来源（含前端需求） |
| 架构设计 | [docs/architecture-design.md](docs/architecture-design.md) | 唯一架构真相来源（含前端架构+多模块设计） |
| 开发计划 | [docs/development-plan.md](docs/development-plan.md) | Sprint 级别任务分解 |
| 迭代日志 | [docs/iteration-log.md](docs/iteration-log.md) | 需求/架构变更追踪 |
| V1 设计稿 | [docs/design/v1-design-doc.md](docs/design/v1-design-doc.md) | V1.0 设计目标、效果、思路、规范 |

---

## 4. 项目目录结构

```
ai-expense-tracker/
├── CLAUDE.md
├── README.md
├── docs/
│   ├── project-design.md              # 原始设计（只读参考）
│   ├── project-requirements.md        # 需求文档（持续更新）
│   ├── architecture-design.md         # 架构设计（持续更新）
│   ├── development-plan.md            # 开发计划
│   ├── iteration-log.md               # 迭代日志
│   └── design/
│       └── v1-design-doc.md           # V1.0 设计稿
│
├── backend/                           # Maven 多模块父项目
│   ├── pom.xml                        # 父 POM（依赖管理 + 模块聚合）
│   ├── expense-common/                # 公共模块（工具、异常、常量、通用 DTO）
│   ├── expense-security/              # 安全模块（JWT、Security Config、Filter）
│   ├── expense-user/                  # 用户模块
│   │   ├── controller/
│   │   ├── manager/                    # Manager 编排层（具体类，无 impl/）
│   │   ├── service/                    # 原子业务（具体类，无 impl/）
│   │   ├── mapper/
│   │   ├── entity/
│   │   └── dto/
│   ├── expense-category/              # 分类模块（同结构）
│   ├── expense-transaction/           # 账单模块（同结构）
│   ├── expense-statistics/            # 统计模块（同结构）
│   └── expense-server/                # 启动模块（Spring Boot 入口 + 全局配置 + 跨模块 Manager）
│
└── frontend/                          # Vue 3 前端项目（独立）
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── api/                       # 后端 API 封装
        ├── router/                    # 路由配置 + 守卫
        ├── stores/                    # Pinia 状态（Token、用户信息）
        ├── views/                     # 页面组件
        │   ├── login/                 # 登录/注册
        │   ├── dashboard/             # 首页仪表盘
        │   ├── transactions/          # 账单管理
        │   ├── categories/            # 分类管理
        │   └── statistics/            # 月度统计
        ├── components/                # 公共组件
        ├── utils/                     # 工具函数（Axios 实例、Token 管理）
        └── assets/                    # 静态资源
```

---

## 5. 开发规范

### 5.1 后端分层职责

```
Controller  →  Manager  →  Service  →  Mapper  →  DB
   │              │           │
   DTO         编排/聚合   原子业务
```

| 层 | 职责 | 禁止 |
|----|------|------|
| **Controller** | 参数接收、JSR-303 校验、调用 Manager、返回结果 | 写任何业务逻辑 |
| **Manager** | 编排多个 Service、处理复杂复合业务、事务控制 | 直接访问 Mapper |
| **Service** | 单一原子业务、模块专属逻辑 | 跨模块调用其他 Service |
| **Mapper** | 数据访问（MyBatis-Plus BaseMapper） | 写业务逻辑 |
| **Entity** | 数据模型定义 | 写逻辑代码 |
| **DTO** | 接口数据传输对象（Request/Response/VO） | — |

**Manager vs Service 的边界：**

```java
// ❌ 没有 Manager：Service 承担编排，越界调用其他模块
UserService {
    register() {
        createUser();
        categoryService.initDefaults();  // ← 越界了
    }
}

// ✅ 有 Manager：各层职责清晰，类本身就是实现
UserManager {
    @Transactional
    register(request) {
        userService.createUser();              // 原子 1
        categoryService.initDefaultCategories(); // 原子 2
    }
}
UserService {
    createUser() { /* 只做用户创建 */ }
}
CategoryService {
    initDefaultCategories() { /* 只做分类初始化 */ }
}
```

> **关于接口**：Service 和 Manager 默认是具体类，不强行定义 interface + impl。只有当需要多态（如策略模式、多实现切换）时才提取接口，此时接口名描述能力（如 `PaymentStrategy`），实现名描述具体策略（如 `AlipayPayment`、`WechatPayment`）。

### 5.2 前端分层职责

| 层 | 职责 |
|----|------|
| **views/** | 页面布局 + 组件组装，不含复杂业务逻辑 |
| **components/** | 可复用组件，不直接调用 API |
| **api/** | 封装所有后端请求，view 通过 api/ 拿数据 |
| **stores/** | 全局状态（Token、用户信息），不存页面私有状态 |
| **router/** | 页面路由 + 导航守卫（未登录重定向） |

### 5.3 API 设计

- 统一 RESTful 风格，URL 前缀 `/api/`
- 请求/响应格式 JSON
- 认证方式 JWT Bearer Token
- 统一响应体 `{ code, message, data }`

### 5.4 数据库建表规范

> 详见 [v1-design-doc.md §4.8](docs/design/v1-design-doc.md)

- **表和字段必须加 COMMENT**，不允许无注释建表
- 索引命名：`uk_xxx`（唯一）、`idx_xxx`（普通）
- 统一 utf8mb4 + InnoDB

### 5.5 配置管理规范

> 详见 [v1-design-doc.md §4.9](docs/design/v1-design-doc.md)

- **敏感信息（数据库密码等）走环境变量，无默认值**
- 项目级配置（库名、JWT 秘钥）可硬编码
- `${VAR}` 无默认值 = 强制设置；`${VAR:default}` = 可选

### 5.4 Git 规范

```
分支: main → develop → feature/* / bugfix/*
提交: type(module): message
示例: feat(transaction): add create transaction API
      fix(user): fix login token issue
      feat(frontend): add dashboard page
```

### 5.5 测试要求

- Service 层单元测试覆盖率 > 80%
- Controller 使用 MockMvc 测试
- 集成测试使用 TestContainers 启动真实 MySQL

---

## 6. Claude Code 工作流程

1. **Analyze** — 阅读相关代码，输出当前实现、问题、修改方案
2. **Design** — 确认文件变化、数据变化、API 变化
3. **Implement** — 修改代码、编写测试、运行测试确认通过
4. **Review** — 检查代码质量、性能、安全、测试完整性

### 重要规则

- 所有需求变更 → 更新 `docs/project-requirements.md`
- 所有架构变更 → 更新 `docs/architecture-design.md`
- 每次迭代完成 → 追加 `docs/iteration-log.md`
- 需求/架构文档保持精简，只保留最新状态
- 历史决策和变更原因记录在迭代日志中
