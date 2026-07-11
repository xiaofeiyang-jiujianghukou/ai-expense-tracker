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
  单 Service 场景：  Controller → Service → Mapper → DB
  多 Service 编排：  Controller → Manager → Service → Mapper → DB
                       ↕          ↕         ↕
                      DTO       编排      原子业务
                  
  Manager 使用规则：
  ┌──────────────────────────────────────────────────────────────┐
  │ ✅ 有 Manager：编排 2+ 个 Service 时才需要（如 UserManager）│
  │ ❌ 无 Manager：单 Service 调用，Controller 直调 Service      │
  │                                                              │
  │ Manager 不是必须有的层，是"需要时才加"的工具                   │
  │ 禁止写只做透传的 Manager（Controller → Manager → Service）    │
  │ 命名：{Domain}Manager，表明编排的业务领域                      │
  └──────────────────────────────────────────────────────────────┘

  Service 和 Manager 默认是具体类，不需要 interface + impl。
  只有当需要多态（策略模式、多实现）时才提取接口。

  写操作返回值：
  - insert / update / delete 返回 void，Controller 返回 ApiResponse<Void>
  - 调用方只需知道成功（code=200）或失败（异常），不需要返回对象
  - 特例：注册/登录等需返回 ID/Token 的可返回数据
  - 特例：乐观锁场景需判断 affected rows 时可返回 int

  HTTP 方法限制：
  - 仅使用 @GetMapping 和 @PostMapping，禁止 @PutMapping/@DeleteMapping
  - @GetMapping 仅限单参数（路径变量 /{id} 或少量 QueryParam）
  - 多参数查询、含 RequestBody 的请求一律 @PostMapping
```

### 3.4 多模块依赖设计

```
expense-server
    ↓ 依赖（编译时）
expense-user / expense-category / expense-bill / expense-statistics
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
    username: ${EXPENSE_DB_USERNAME}   # ← 部署级，强制环境变量，无默认值
    password: ${EXPENSE_DB_PASSWORD}   # ← 部署级，强制环境变量，无默认值
jwt:
  secret: xxxxx                # ← 项目级，硬编码
  expiration: 604800000        # ← 项目级，硬编码
```

| 配置类型 | 存放方式 | 示例 |
|----------|----------|------|
| DB_HOST, DB_PORT（全局）; EXPENSE_DB_USERNAME, EXPENSE_DB_PASSWORD（项目专属） | 环境变量，无默认值 | `${DB_HOST}` |
| 数据库名 | 硬编码 | `ai_expense_tracker` |
| JWT 秘钥 | 硬编码 | `YWktZXhw...` |
| JWT 过期时间 | 硬编码 | `604800000` |
| Server 端口 | 环境变量 + 默认值 | `${SERVER_PORT:8080}` |

#### 环境变量命名与设置策略

**变量分为「全局共享」和「项目专属」两类：**

| 类别 | 命名 | 示例 | 说明 |
|------|------|------|------|
| 全局共享 | 通用名，无前缀 | `DB_HOST`、`DB_PORT`、`LLM_API_KEY`、`LLM_BASE_URL`、`LLM_MODEL` | 跨项目通用的基础设施配置，统一使用，不重复定义 |
| 项目专属 | `EXPENSE_` 前缀 | `EXPENSE_DB_USERNAME`、`EXPENSE_DB_PASSWORD`、`EXPENSE_JAVA_HOME` | 仅本项目使用，避免与其它项目同名变量冲突 |

**设置规则（强制执行）：**

1. **先检查再设置**：创建新环境变量前，先检查系统中是否已存在同名变量
2. **同名同值不覆盖**：如果已有同名变量且值相同，直接复用，不重复设置
3. **同名异值用项目前缀**：如果已有同名变量但值不同（如 `JAVA_HOME` 指向 JDK 17 而本项目需要 JDK 21），创建项目专属变量（如 `EXPENSE_JAVA_HOME`），并在项目配置中引用新变量名
4. **绝不复用同名变量承载不同值**——这会破坏其它项目或系统功能

### 4.10 验收流程规范

**每次功能开发完成后，必须走完以下验收流程，全部通过后方可交由人工验收。**

**核心原则：**
1. **仅验证 API 代理可用 ≠ 联调通过。** 联调验收必须在浏览器中实际操作每个页面/按钮/交互。
2. **增量验证，不全量重跑。** 修复 BUG 后只验证修复项及其影响范围。
3. **测试方式必须如实标注。** PowerShell/curl 的叫「API 模拟测试」，浏览器点击的叫「浏览器测试」，两者不可混淆。
4. **验收用例必须包含「预期」和「实测」两列，实测必须填具体数值（耗时、长度、状态码等），不能只写"通过"。**

```
验收流程：
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ 1. 编译   │───▶│ 2. API    │───▶│ 3. 浏览器  │───▶│ 4. 增量  │───▶│ 5. 人工   │
│    通过    │    │  模拟测试  │    │  验收测试  │    │  回归    │    │   验收    │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
                                                    │
                                                    ▼
                                            ┌──────────────┐
                                            │ 遇 BUG → 修复 │
                                            │ 仅验证修复项   │
                                            └──────────────┘
```

#### 步骤 1：编译通过
- `mvn compile` 零错误、零乱码
- 前端 `npm run dev` 启动无报错

#### 步骤 2：API 模拟测试

**前置规则：调用任何 API 前，必须先查 DTO 确认字段名。**

- 登录接口的字段名是 `email` 不是 `username`，是 `password` 不是 `pwd`
- 请求体字段名 = Java DTO 的 `private` 字段名（Lombok `@Data` 生成的 getter/setter 名），不是数据库列名也不是直觉猜测
- 不确定时直接 `grep "class XxxRequest"` 定位 DTO，读源码确认字段名
- **禁止不查 DTO 直接盲测** — 字段名猜错的探索成本完全可避免

用 PowerShell/curl 逐一调用本次新增/变更的 API。**验收报告必须用以下格式逐项填写实测值：**

```
| # | 测试项 | 方式 | 预期 | 实测 |
|---|--------|------|------|------|
| 1 | POST /api/ai/categorize | API模拟 | code=200 | code=200, 餐饮, conf=1.0 |
| 2 | POST /api/ai/analysis (缓存) | API模拟 | <100ms | 17ms |
| 3 | POST /api/ai/analysis/stream | API模拟 | SSE多chunk | 124 lines, 21115 bytes |
```

**标准**：
- 所有新增/变更 API：HTTP 200
- SSE 流式接口：chunk 数 > 0，总字节数 > 100
- 缓存命中：< 200ms
- 鉴权拦截：401 或 403
- 重新生成（forceRefresh）：调用时长 > 3s（说明走了 LLM）

**允许纯 API 模拟测试，但仅限不涉及前端 UI 的后端接口。涉及前端交互的必须走浏览器实测。**

#### 步骤 3：浏览器验收测试（前端必测）

**必须用真实浏览器（或 Playwright MCP）逐项操作，每项填实测结果：**

```
| # | 操作 | 方式 | 预期 | 实测 |
|---|------|------|------|------|
| 1 | 打开 http://localhost:5173 | 浏览器 | 显示登录页 | |
| 2 | 输入账号密码，点击登录 | 浏览器 | 跳转仪表盘，无报错 | |
| 3 | 仪表盘→观察 AI 洞察区域 | 浏览器 | 缓存内容立即显示 | |
| 4 | 仪表盘→点击"重新生成" | 浏览器 | Loading→文字逐行流式出现 | |
| 5 | 月度统计→观察报告区域 | 浏览器 | 缓存内容立即显示 | |
| 6 | 月度统计→点击"生成 AI 报告" | 浏览器 | Loading→文字逐段流式出现 | |
| 7 | DevTools Network 面板 | 浏览器 | 零 4xx/5xx/timeout | |
| 8 | 退出登录→访问 /dashboard | 浏览器 | 跳转登录页 | |
```

**标准**（前端交互必须浏览器实测，不允许用 API 模拟替代）：
- 每个按钮点击后必须有 Loading 反馈，不可无响应
- 流式输出必须边生成边展示（文字逐段出现），不可等全部生成完才一次性显示
- DevTools Network：零 4xx、零 5xx、零 timeout
- 页面切换无报错、无空白

**禁止行为：**
- ❌ API 模拟测试标成浏览器测试
- ❌ 实测列只写"通过"不写具体数值
- ❌ 跳过浏览器 DevTools 检查

#### 步骤 4：增量回归
- 至少走 2 个已有页面，确保未破坏已有功能

#### 步骤 5：人工验收
- 出具验收报告，标注每项实测结果是否达标
- 全部达标后交由人工最终验收

### 4.11 联调测试规范

**每次前后端开发完成后，必须进行完整联调测试，不能仅靠单元测试或单接口验证。**

#### 测试环境要求

```
后端先启，API 全部就绪后再测前端
前端通过 Vite proxy (/api → localhost:8080) 调用后端
```

#### 联调测试清单（必须逐项通过）

| # | 测试项 | 方法 | 预期 |
|---|--------|------|------|
| 1 | 首页访问 | 浏览器打开 `/` | 未登录 → 登录页；已登录 → 仪表盘 |
| 2 | 注册 | 填写邮箱+密码+昵称，点击注册 | 成功提示 → 跳转登录页 |
| 3 | 登录 | 输入已注册的邮箱+密码 | 成功提示 → 跳转仪表盘 |
| 4 | 默认分类 | 登录后检查分类管理页 | 8 个默认分类（5支出 + 3收入） |
| 5 | 创建账单 | 选择分类+金额+日期，新增 | 列表刷新，新纪录出现 |
| 6 | 筛选分页 | 切换类型/分类/日期范围，查询 | 列表正确过滤，分页正常 |
| 7 | 编辑账单 | 修改金额/描述，保存 | 列表刷新，数据更新 |
| 8 | 删除账单 | 点击删除，确认 | 列表刷新，记录消失 |
| 9 | 分类管理 | 新增/编辑/删除分类 | 操作成功，列表更新 |
| 10 | 月度统计 | 选择月份查看 | 收入/支出/结余正确，分类占比可见 |
| 11 | Token 过期 | 清除 localStorage token，刷新页面 | 自动跳转登录页，不报错 |
| 12 | 未登录拦截 | 直接访问 `/dashboard`、`/bills` | 自动跳转登录页 |
| 13 | 登录回跳 | 已登录时访问 `/login` | 自动跳转仪表盘 |

#### 前端体验要求

```
- 所有操作有反馈：成功提示 / 失败提示
- 删除操作有二次确认弹窗
- 按钮在请求中显示 loading，防止重复提交
- 表格数据为空时显示空状态占位
- 网络异常时不白屏，有错误提示
- 未登录访问任何页面 → 跳转登录页，不显示 403/空白
```

#### HTTP 状态码处理

| 状态码 | 含义 | 前端处理 |
|--------|------|----------|
| 200 + code=200 | 成功 | 正常展示 |
| 200 + code≠200 | 业务错误 | `ElMessage.error` 展示 message |
| 401 | 未认证 | 清 token → 跳 `/login` |
| 403 | 无权限 | 清 token → 跳 `/login` |
| 500 | 服务器错误 | `ElMessage.error` 展示 |

#### 边界情况测试

```
- 注册：重复邮箱 → 提示"邮箱已被注册"
- 登录：错误密码 → 提示"邮箱或密码错误"
- 登录：不存在的邮箱 → 提示"邮箱或密码错误"
- 账单：金额输入 0 → 校验拦截
- 账单：不填分类/日期 → 校验拦截
- 统计：无数据的月份 → 显示 0 而非报错
```

### 4.11 文档同步规范

**每次完成大的实现阶段（一个或多个 Sprint）后，必须同步更新所有受影响的文档，代码和文档保持一致。**

#### 触发时机

| 触发条件 | 需同步的内容 |
|----------|-------------|
| 一个或多个 Sprint 完成 | `development-plan.md` 状态标记 |
| 项目结构变更（模块改名、新增/删除） | 所有引用该模块的文档（见下方清单） |
| 每次迭代结束 | `iteration-log.md` 追加记录 |
| API 变更（新增/修改/删除接口） | `README.md` API 概览 |

#### 文档同步检查清单

当发生**项目结构变更**（如模块改名 `expense-transaction` → `expense-bill`）时，以下文件**全部**需要检查和更新：

```
☐ CLAUDE.md                        — §4 项目目录结构
☐ README.md                        — 项目结构
☐ docs/architecture-design.md      — 模块结构 + 依赖关系图
☐ docs/design/v1-design-doc.md     — 多模块依赖设计
☐ docs/development-plan.md         — 所有涉及该名称的 Sprint 描述
```

**方法**：`grep` 全局搜索旧名称 → 逐文件替换 → 再次 `grep` 确认零遗漏。

#### 反模式

| 反模式 | 问题 | 正确做法 |
|--------|------|----------|
| 代码改了文档没改 | 文档与实际不一致，误导后续开发 | 每次 Plan 完成后逐项检查文档同步 |
| 只改一个文档 | 其他文档残留旧引用 | 全局搜索 + 全部更新 |

### 4.12 Maven 编译环境规范

**每次代码变更后必须编译通过，且编译输出不得有乱码。**

#### 编码配置（强制）

父 POM 中必须配置 UTF-8 编码：

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

项目根目录 `.mvn/maven.config` 中必须包含：

```
-Dfile.encoding=UTF-8
```

#### Maven 仓库策略

| 场景 | 仓库 | 要求 |
|------|------|------|
| 下载公开依赖（如 AgentScope） | 阿里云镜像 `https://maven.aliyun.com/repository/public` | 通过 `~/.m2/settings.xml` 的 mirror 配置 |
| 本地仓库 | 用户个人 `~/.m2/repository` | 使用项目 `.mvn/maven.config` 中的 `-Dmaven.repo.local` 指定，**不得使用公司统一仓库路径** |
| 公司内部私有依赖 | 公司 Nexus | 仅在确需公司内部包时配置 |

**原则**：
- **公司仓库用于发布，个人仓库用于开发** — 避免污染公司仓库、避免依赖冲突
- **编译输出乱码 → 立刻修复** — 不可接受的代码质量问题
- **新依赖查找优先去阿里云镜像仓库搜索最新版本**，找到后直接注入
- **遇到问题不许退缩** — 编译失败、API 变化等必须设法解决，不允许私自降级或放弃。如需降级版本，必须先获得批准

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
| 写操作返回对象 | 浪费序列化开销、调用方不需要 | insert/update/delete 返回 ApiResponse\<Void\> |
| 使用 @PutMapping/@DeleteMapping | HTTP 方法过多，维护复杂 | 仅保留 GetMapping(单参) + PostMapping(其他) |
| 全限定类名代替 import | 冗余、可读性差 | 无冲突时一律用 import，禁止 `com.xxx.Foo` 内联 |
| 代码改了文档没同步 | 文档沦为摆设，误导后续开发 | Plan 完成后走文档同步检查清单，grep 确认零遗漏 |
| 遇到问题就退缩放弃 | 技术债务累积，问题不解决会反复出现 | 追根究底，查文档/源码/仓库，解决为止；降级需审批 |
| 编译输出有乱码不修 | 掩盖真实错误信息，浪费排查时间 | 强制 UTF-8 编码，发现乱码立即修复 |
| 用 curl 测代理就宣称联调完成 | 遗漏 timeout/前端超时/UI 不响应等真实用户路径的 BUG | 必须用浏览器逐一操作每个新功能，打开 DevTools 确认零错误 |
| API 模拟标成浏览器测试 | 实测列造假，人工验收看到假数据 | 方式列严格标注"API模拟"或"浏览器"，不做假 |
| 实测列只写"通过" | 无具体数据无法判断是否达标 | 必须填具体数值（耗时 ms、字节数、chunk 数、状态码等） |

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
