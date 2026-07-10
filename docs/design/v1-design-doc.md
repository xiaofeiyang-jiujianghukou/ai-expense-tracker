# V1.0 设计稿 — AI Expense Tracker

> 本文档记录 V1.0 MVP 的设计目标、预期效果、设计思路和开发设计规范。
> 后续版本的设计稿将独立成文，放在 `docs/design/` 目录下。

---

## 1. V1.0 设计目标

### 一句话目标

> **一个前后端分离、开箱即用的个人记账应用 —— 前端有界面可交互，后端 API 清晰规范，代码结构支持未来微服务拆分。**

### 用户故事

```
作为 一个普通用户
我 可以通过 Web 浏览器登录系统
以便 记录每天的收支、查看消费统计

作为 一个开发者
我 克隆项目后一条命令启动前后端
以便 本地开发调试、后续扩展 AI 功能
```

---

## 2. V1.0 预期效果

### 2.1 用户端体验流程

```
浏览器打开
    │
    ▼
┌────────┐    ┌────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────┐
│ 登录页  │───▶│ 仪表盘  │───▶│  账单列表     │───▶│  新增账单弹窗  │───▶│  列表刷新 │
│        │    │ 本月概览 │    │ 筛选/搜索/分页 │    │  选分类填金额  │    │  可编辑  │
└────────┘    └────────┘    └──────────────┘    └──────────────┘    │  可删除  │
                                                                   └──────────┘

                   ┌──────────┐    ┌──────────────┐
                   │ 分类管理  │    │  月度统计     │
                   │ 收入/支出  │    │  收入/支出/结余│
                   └──────────┘    │  分类占比     │
                                   └──────────────┘
```

### 2.2 页面效果描述

#### 登录 / 注册

```
┌──────────────────────────────────────┐
│                                      │
│         AI Expense Tracker           │
│         智能个人财务管理系统            │
│                                      │
│    ┌──────────────────────────┐      │
│    │  邮箱                     │      │
│    ├──────────────────────────┤      │
│    │  密码                     │      │
│    ├──────────────────────────┤      │
│    │        登  录             │      │
│    └──────────────────────────┘      │
│                                      │
│    还没有账号？立即注册 →              │
│                                      │
└──────────────────────────────────────┘
```

#### 首页仪表盘

```
┌──────────────────────────────────────────────┐
│  Logo   仪表盘  账单  分类  统计    [用户头像]  │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │ 本月收入  │ │ 本月支出  │ │ 本月结余  │      │
│  │ ¥8,000   │ │ ¥3,500   │ │ ¥4,500   │      │
│  └──────────┘ └──────────┘ └──────────┘      │
│                                              │
│  最近账单                           [+新增]  │
│  ┌──────────────────────────────────────┐    │
│  │ 07-11 │ 餐饮 │ 午餐        │ -35.50  │    │
│  │ 07-10 │ 交通 │ 地铁        │ -8.00   │    │
│  │ 07-09 │ 工资 │ 7月工资     │ +8000   │    │
│  │ 07-08 │ 购物 │ 日用品      │ -120.00 │    │
│  └──────────────────────────────────────┘    │
│                                              │
└──────────────────────────────────────────────┘
```

#### 账单管理

```
┌──────────────────────────────────────────────┐
│  Logo   仪表盘  账单  分类  统计    [用户头像]  │
├──────────────────────────────────────────────┤
│                                              │
│  [+新增账单]                                 │
│                                              │
│  筛选: [全部▾] [全部分类▾] [2026-07-01 ~ 07-11] │
│                                              │
│  ┌──────────────────────────────────────┐    │
│  │日期    │分类  │描述    │金额    │操作 │    │
│  ├──────────────────────────────────────┤    │
│  │07-11  │餐饮  │午餐    │-35.50 │✏️🗑️│    │
│  │07-09  │工资  │工资    │+8000  │✏️🗑️│    │
│  │ ...   │     │        │       │    │    │
│  ├──────────────────────────────────────┤    │
│  │         ◀ 1 2 3 ... 10 ▶           │    │
│  └──────────────────────────────────────┘    │
│                                              │
└──────────────────────────────────────────────┘
```

#### 月度统计

```
┌──────────────────────────────────────────────┐
│  Logo   仪表盘  账单  分类  统计    [用户头像]  │
├──────────────────────────────────────────────┤
│                                              │
│  选择月份: [2026年▾] [7月▾]                    │
│                                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │  收入    │ │  支出    │ │  结余    │      │
│  │ ¥8,000  │ │ ¥3,500  │ │ ¥4,500  │      │
│  └──────────┘ └──────────┘ └──────────┘      │
│                                              │
│  支出分类占比（V2 加饼图，V1 文字列表）          │
│  ┌──────────────────────────────────────┐    │
│  │ 餐饮     ¥1,200    34%              │    │
│  │ 交通     ¥500      14%              │    │
│  │ 购物     ¥800      23%              │    │
│  │ 娱乐     ¥400      11%              │    │
│  │ 住房     ¥600      17%              │    │
│  └──────────────────────────────────────┘    │
└──────────────────────────────────────────────┘
```

---

## 3. 设计思路

### 3.1 核心理念：企业级标准 + 简单优先 + 为扩展预留

| 维度 | V1.0 做法 | 未来扩展 |
|------|-----------|----------|
| **分层** | Controller → Manager → Service → Mapper | Manager 处理跨模块编排，Service 保持原子 |
| **ORM** | MyBatis-Plus | SQL 完全可控，复杂查询直接写 |
| **模块** | Maven 多模块，单体部署 | 拆 POM + 加启动类即可微服务化 |
| **前端** | Vue 3 + Element Plus | 组件化，V2 加 ECharts 图表 |
| **分类** | 用户手动创建 | 表结构预留 AI 扩展字段 |
| **认证** | JWT 无状态 | 可扩展角色/权限体系 |

### 3.2 前端设计原则

```
页面层级：
  AppLayout（侧边栏导航 + 顶部栏）
    ├── DashboardView（3 个金额卡片 + 最近账单表格）
    ├── TransactionList（搜索栏 + 表格 + 分页 + 新增/编辑弹窗）
    ├── CategoryManage（标签页切换收入/支出 + 列表 + 新增行）
    └── MonthlyStats（月份选择器 + 汇总卡片 + 分类明细）

交互原则：
  - 每个操作有反馈：成功 Toast / 失败提示
  - 删除操作有二次确认
  - 表格数据为空时有空状态占位
  - 请求中按钮 Loading 防重复提交
```

### 3.3 后端设计原则

```
模块内部结构（每个业务模块统一）：
  ├── controller/    → @RestController, @Validated
  ├── manager/       → 编排多个 Service，处理复合业务（具体类）
  ├── service/       → 单一原子业务（具体类）
  ├── mapper/        → extends BaseMapper<T>（MyBatis-Plus）
  ├── entity/        → @Data, @TableName
  └── dto/           → @Data, request + response

调用链路：
  Controller → Manager → Service → Mapper → DB
       ↕          ↕         ↕
      DTO       编排      原子业务
                  
  Service 和 Manager 默认是具体类，不需要 interface + impl。
  只有当需要多态（策略模式、多实现）时才提取接口。
```

### 3.4 多模块依赖设计

```
expense-server
    ↓ 依赖（编译时）
expense-user / expense-category / expense-transaction / expense-statistics
    ↓ 依赖
expense-security
    ↓ 依赖
expense-common
    ↓ 依赖（只有这个）
Spring Boot / MyBatis-Plus / Lombok / MySQL Driver / ...
```

**关键决策**：业务模块之间**不直接相互依赖**。跨模块编排通过 expense-server 的 Manager 实现：`server/manager/DashboardManager` 注入各模块的 Service，协调调用。拆分微服务时，Manager 对 Service 的本地调用改为 Feign 远程调用，业务代码不变。

---

## 4. 开发设计规范

### 4.1 后端命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| Entity | 表名驼峰 + @TableName | `User`, `Transaction`, `Category` |
| Mapper | Entity名 + Mapper | `UserMapper extends BaseMapper<User>` |
| Service | 模块 + Service | `UserService`（具体类） |
| Manager | 模块 + Manager | `UserManager`（具体类） |
| Controller | 模块 + Controller | `UserController` |
| DTO | 用途 + Request/Response/VO | `RegisterRequest`, `LoginResponse` |
| Maven 模块 | expense-xxx | `expense-user`, `expense-common` |

### 4.2 前端命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 页面组件 | XxxView.vue | `LoginView.vue`, `DashboardView.vue` |
| 业务组件 | 功能名.vue | `TransactionForm.vue`, `AmountCard.vue` |
| API 模块 | 资源名.ts | `user.ts`, `transaction.ts` |
| Store | 资源名.ts | `user.ts`, `app.ts` |
| 路由路径 | kebab-case | `/transactions`, `/monthly-stats` |

### 4.3 异常处理规范（后端）

```
throw new BusinessException(ErrorCode.USER_NOT_FOUND)
    → GlobalExceptionHandler 拦截
    → 返回 { "code": 40401, "message": "用户不存在", "data": null }
```

| 场景 | HTTP 状态码 | 错误码 |
|------|------------|--------|
| 资源不存在 | 404 | 40401~40499 |
| 参数校验失败 | 400 | 40001~40099 |
| 无权访问 | 403 | 40301~40399 |
| 未认证 | 401 | 40101~40199 |
| 服务器错误 | 500 | 50001~50099 |

### 4.4 参数校验规范

```java
// Controller — JSR-303 基础校验
@PostMapping("/register")
public ApiResponse<UserVO> register(@Valid @RequestBody RegisterRequest request) { ... }

// DTO — @Data + 校验注解
@Data
public class RegisterRequest {
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6, max = 32) private String password;
    @Size(max = 50) private String nickname;
}

// Service — 业务校验（邮箱是否已注册等）
if (userMapper.selectCount(
    new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0) {
    throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
}
```

### 4.5 前端校验规范

```typescript
// Element Plus 表单校验规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32 位', trigger: 'blur' }
  ]
}
```

### 4.6 测试规范

```
后端测试目录：
src/test/java/com/example/expense/
├── user/
│   ├── service/UserServiceTest.java
│   ├── controller/UserControllerTest.java
│   └── UserIntegrationTest.java
└── ...

测试命名：should_Result_When_Condition
  should_ReturnUser_When_ValidCredentials
  should_ThrowException_When_EmailAlreadyExists
```

### 4.7 Git 提交规范

```
feat(module): what was done
fix(module): what was fixed
refactor(module): what was refactored
test(module): what was tested
docs: documentation
style: UI/layout changes

示例：
  feat(user): add register and login API
  feat(frontend): add login page and auth flow
  fix(transaction): fix date range query boundary
  style(dashboard): adjust amount card layout
```

### 4.8 数据库建表规范

**所有表和字段必须添加 COMMENT**，这是强制规范，不是可选项。

```sql
-- ✅ 正确：表有注释，字段有注释
CREATE TABLE `user` (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    email   VARCHAR(255) NOT NULL           COMMENT '邮箱',
    ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ❌ 错误：无注释
CREATE TABLE `user` (
    id      BIGINT NOT NULL AUTO_INCREMENT,
    email   VARCHAR(255) NOT NULL,
    ...
);
```

| 规范项 | 要求 |
|--------|------|
| 表注释 | `COMMENT='表用途说明'`，说明这张表存什么数据 |
| 字段注释 | 每个字段加 `COMMENT`，说明含义；枚举字段注明可选值 |
| 索引命名 | `uk_xxx`（唯一索引）、`idx_xxx`（普通索引） |
| 字符集 | 统一 `utf8mb4` + `utf8mb4_unicode_ci` |
| 引擎 | 统一 `InnoDB` |
| 主键 | `BIGINT NOT NULL AUTO_INCREMENT`，不允许使用业务主键 |
| 时间戳 | `created_time` 用 `DEFAULT CURRENT_TIMESTAMP`，`updated_time` 用 `ON UPDATE CURRENT_TIMESTAMP`，代码不注入时间。insert/update 后不查询时间，DB 自动维护即可 |

### 4.9 配置管理规范

**敏感信息与核心资产永远不硬编码，走环境变量。**

```
分层原则：
┌─────────────────────────────────────────────┐
│  项目级配置（硬编码，随代码一起管理）          │
│  · 数据库名、JWT 秘钥、JWT 过期时间          │
│  · 这些配置不随部署环境变化                    │
├─────────────────────────────────────────────┤
│  部署级配置（环境变量，每个部署环境各自设置）    │
│  · 数据库地址、端口、用户名、密码              │
│  · 第三方服务 Key、Secret                    │
│  · 这些配置随 dev/staging/prod 变化          │
└─────────────────────────────────────────────┘
```

```yaml
# application.yml — 项目级配置可以硬编码
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/ai_expense_tracker?...  # 库名硬编码
    username: ${DB_USERNAME}   # ← 部署级，强制环境变量，无默认值
    password: ${DB_PASSWORD}   # ← 部署级，强制环境变量，无默认值
jwt:
  secret: xxxxx                # ← 项目级，硬编码
  expiration: 604800000        # ← 项目级，硬编码
```

| 配置类型 | 存放方式 | 示例 |
|----------|----------|------|
| DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD | 环境变量，无默认值 | `${DB_HOST}` |
| 数据库名 | 硬编码 | `ai_expense_tracker` |
| JWT 秘钥 | 硬编码 | `YWktZXhw...` |
| JWT 过期时间 | 硬编码 | `604800000` |
| Server 端口 | 环境变量 + 默认值 | `${SERVER_PORT:8080}` |

---

## 5. 设计反模式（避坑指南）

| 反模式 | 问题 | 正确做法 |
|--------|------|----------|
| Controller 写业务逻辑 | 逻辑分散、难复用 | 全放 Manager + Service |
| Entity 直接返给前端 | 暴露表结构 | 用 DTO/VO 隔离 |
| Service 跨模块调用 | 微服务拆不动、循环依赖 | Manager 编排，Service 之间不互相调用 |
| 强行 interface + impl | 只有一个实现时纯属鸡肋 | Service/Manager 默认为具体类，需多态时再提取接口 |
| 前端直接操作 localStorage | 散落各处、难维护 | 封装在 utils/auth.ts |
| 硬编码 API 地址 | 环境切换全改一遍 | 用 .env + Vite proxy |
| 跨域靠后端临时配置 | 生产不可控 | 开发用 proxy，生产用 Nginx |
| 业务模块间直接依赖 | 微服务拆不动 | 通过 server 层编排 |
| 没有空状态/加载态 | 白屏用户以为坏了 | Loading + Empty 组件 |
| 建表不加 COMMENT | 无人知道表/字段含义 | 表注释 + 字段注释，强制规范 |
| 敏感信息硬编码 | 泄露风险、环境切换困难 | DB 密码等走环境变量，无默认值 |

---

## 6. 版本迭代设计方向

```
V1.0                   V2.0                  V3.0                  V4.0
┌──────────┐         ┌──────────┐          ┌──────────┐          ┌──────────┐
│ 手动记账  │   →     │ AI 分类   │    →    │ Python   │    →    │ Docker   │
│ 基础统计  │         │ 消费分析  │          │ 数据分析  │          │ K8s      │
│ Web 交互  │         │ ECharts   │          │ 推荐模型  │          │ CI/CD    │
│ 多模块单体 │         │ 依然单体  │          │ 微服务    │          │ 云原生    │
└──────────┘         └──────────┘          └──────────┘          └──────────┘
```

**V1.0 核心设计原则：今天的简单，是明天扩展的基础。不做无意义的抽象，但做有远见的预留。**
