# AI Expense Tracker

AI 智能个人财务管理系统 — 前后端分离的个人记账应用。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 21 · Spring Boot 3.4 · MyBatis-Plus 3.5 · MySQL 8 · Flyway |
| 前端 | Vue 3 · Vite · Element Plus · Axios · Pinia · Vue Router |
| 安全 | Spring Security · JWT (jjwt) · BCrypt |
| 构建 | Maven 多模块 · npm |
| 测试 | JUnit 5 · Mockito · TestContainers |

## 项目结构

```
ai-expense-tracker/
├── backend/                         # Maven 多模块后端
│   ├── expense-common/              # 公共：响应体、异常、错误码
│   ├── expense-security/            # 安全：JWT、SecurityConfig
│   ├── expense-user/                # 用户模块
│   ├── expense-category/            # 分类模块
│   ├── expense-transaction/         # 账单模块
│   ├── expense-statistics/          # 统计模块
│   └── expense-server/              # 启动入口
├── frontend/                        # Vue 3 前端
│   └── src/
│       ├── api/                     # 后端接口封装
│       ├── router/                  # 路由 + 守卫
│       ├── stores/                  # Pinia 状态
│       ├── views/                   # 6 个页面
│       └── components/              # 公共组件
└── docs/                            # 项目文档
    ├── project-requirements.md      # 需求文档
    ├── architecture-design.md       # 架构设计
    ├── development-plan.md          # 开发计划
    ├── iteration-log.md             # 迭代日志
    └── design/
        └── v1-design-doc.md         # V1 设计稿
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0

### 环境变量

```bash
# 必须设置（敏感信息，无默认值）
DB_HOST=47.111.104.180
DB_PORT=3306
DB_USERNAME=ai-expense-tracker
DB_PASSWORD=your_password
```

### 启动后端

```bash
cd backend
mvn clean install -DskipTests
mvn -pl expense-server spring-boot:run
```

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## API 概览

```
POST /api/users/register            注册
POST /api/users/login               登录 → JWT
POST /api/categories                创建分类
POST /api/categories/list           分类列表
GET  /api/categories/{id}           分类详情
POST /api/categories/update         修改分类
POST /api/categories/delete         删除分类
POST /api/transactions              创建账单
POST /api/transactions/list          筛选分页查询
GET  /api/transactions/{id}         账单详情
POST /api/transactions/update       修改账单
POST /api/transactions/delete       删除账单
POST /api/statistics/monthly        月度统计
```

## 开发规范

详见 [docs/design/v1-design-doc.md](docs/design/v1-design-doc.md)

- Controller → Manager → Service → Mapper 五层架构
- 仅 GET(单参) + POST 两种 HTTP 方法
- 写操作不返回对象
- 建表必须加 COMMENT
- 时间戳由 DB 管理，代码不注入
- 敏感配置走环境变量，无默认值
