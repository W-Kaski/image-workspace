# Image Workspace（图片协作平台）

> 全栈团队图片协作平台 —— 支持共享空间管理、基于角色的权限控制、腾讯云 COS 上传管道、数据分析看板，以及基于 WebSocket 的实时协同编辑。

[![后端](https://img.shields.io/badge/后端-Spring%20Boot%202.7.6-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![前端](https://img.shields.io/badge/前端-Vue%203%20%2B%20Vite-42b883?style=flat-square)](https://vuejs.org)
[![鉴权](https://img.shields.io/badge/鉴权-Sa--Token-orange?style=flat-square)](https://sa-token.cc)
[![ORM](https://img.shields.io/badge/ORM-MyBatis--Plus%203.5.9-blue?style=flat-square)](https://baomidou.com)
[![数据库](https://img.shields.io/badge/数据库-MySQL%20%2B%20Redis-336791?style=flat-square)](#)
[![存储](https://img.shields.io/badge/存储-腾讯云COS-1a6cf5?style=flat-square)](#)
[![许可证](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![PRs](https://img.shields.io/badge/PRs-欢迎-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[English](./README.md) · [架构](#架构) · [快速开始](#快速开始) · [核心功能](#核心功能) · [接口文档](#接口文档)

---

## 概览

**Image Workspace** 将共享图片系统定位为团队的基础设施，而非简单的图库。核心设计包括：

- **存储隔离** —— 每个团队拥有独立的命名*空间（Space）*，彼此隔离
- **细粒度权限模型** —— 每个空间成员拥有 `viewer / editor / admin` 三级角色，由 Sa-Token 在 Controller 层强制执行
- **完整上传管道** —— 文件流式传输至腾讯云 COS，服务端自动提取元数据（尺寸、格式、主色调、宽高比）
- **内容审核工作流** —— 三态审核流程（`待审 / 通过 / 拒绝`），记录审核人 ID 与时间戳
- **实时协同编辑** —— 基于 WebSocket 的图片编辑房间，底层采用 LMAX Disruptor 环形队列实现无锁事件分发
- **数据分析看板** —— 基于 ECharts 的 6 个分析面板：存储用量、分类分布、标签词云、文件大小分段、用户上传行为、空间排行榜

本仓库为前后端统一的 Monorepo。

---

## 架构

```
浏览器
  → Vue 3 SPA（Vite、Ant Design Vue、Pinia、ECharts）
  → HTTP REST + WebSocket（/api，端口 8123）
Spring Boot 2.7.6 后端（Java 11）
  → Sa-Token          — 会话鉴权 + 空间级别 RBAC
  → MyBatis-Plus      — ORM，支持逻辑删除与分页
  → AOP               — 通过 @SaCheckPermission 横切鉴权
  → Disruptor         — 无锁 WebSocket 事件队列
MySQL 8                — 主要应用状态（4 张表）
Redis                  — Sa-Token 会话存储（jackson 序列化）
Caffeine               — 本地进程内缓存
腾讯云 COS             — 对象存储 + CI 图像处理管道
Knife4j / OpenAPI 2    — 交互式接口文档，访问 /api/doc.html
```

### 数据库结构（4 张表）

| 表名 | 用途 |
|------|------|
| `user` | 账号凭证、角色、软删除 |
| `picture` | 图片元数据、COS URL、审核状态、主色调、spaceId 外键 |
| `space` | 命名工作空间 —— 级别、配额（maxSize / maxCount）、类型（私有 / 团队） |
| `space_user` | 用户与空间的多对多关联，含 `viewer / editor / admin` 角色 |

---

## 核心功能

| 模块 | 说明 |
|------|------|
| **空间协作** | 支持私有空间和团队空间；角色感知 CRUD 由 Sa-Token `StpInterface` 强制执行 |
| **图片上传** | Multipart 文件上传 + URL 抓取（Jsoup）；COS CI 返回宽高、格式、主色调 |
| **批量抓取** | 管理员可从外部 URL 批量导入图片 |
| **图像处理** | 缩略图生成、颜色提取、宽高比归一化 |
| **内容审核** | 三态工作流，记录审核人 ID 与时间戳 |
| **色彩感知搜索** | 支持按主色调结合文本 / 分类 / 标签筛选 |
| **数据分析** | 6 个 ECharts 面板：用量配额、分类、标签、大小分段、用户上传、空间排行 |
| **实时协同编辑** | 每张图片独立 WebSocket 房间；Disruptor 环形队列；进入 / 操作 / 退出协议 |
| **鉴权与 RBAC** | Sa-Token 会话 + 空间级别权限；自定义 `StpInterfaceImpl` 从请求路径解析上下文 |
| **接口文档** | Knife4j 界面，访问 `http://localhost:8123/api/doc.html` |

---

## 目录结构

```
image-workspace/
├── backend/                        # Spring Boot 2.7.6，Java 11
│   ├── src/main/java/com/eric/ekcloudgallerybackend/
│   │   ├── annotation/             # 自定义注解（如鉴权检查）
│   │   ├── aop/                    # AOP 拦截器
│   │   ├── common/                 # 统一响应体、分页结果
│   │   ├── config/                 # CORS、COS 客户端、MybatisPlus、JSON 配置
│   │   ├── constant/               # UserConstant 等常量
│   │   ├── controller/             # REST 接口（User、Picture、Space、SpaceUser、SpaceAnalyze、File）
│   │   ├── exception/              # BusinessException + 全局异常处理器
│   │   ├── manage/
│   │   │   ├── auth/               # Sa-Token StpInterfaceImpl、SpaceUserAuthManager
│   │   │   ├── upload/             # 图片上传策略（文件上传 / URL 上传）
│   │   │   └── websocket/          # PictureEditHandler + Disruptor 事件管道
│   │   ├── mapper/                 # MyBatis-Plus Mapper 接口
│   │   ├── model/
│   │   │   ├── dto/                # 请求 DTO
│   │   │   ├── entity/             # 实体类（Picture、Space、SpaceUser、User）
│   │   │   ├── enums/              # SpaceTypeEnum、SpaceRoleEnum 等
│   │   │   └── vo/                 # 响应视图对象
│   │   └── service/                # Service 接口 + impl/ 实现类
│   ├── src/main/resources/
│   │   ├── application.yml         # 激活 prod profile（数据库 / Redis 在 prod 配置中）
│   │   └── biz/spaceUserAuthConfig.json  # 角色 → 权限映射配置
│   └── sql/create_table.sql        # 完整 DDL（含增量 ALTER TABLE）
│
└── frontend/                       # Vue 3，Vite 6，TypeScript，Ant Design Vue 4
    └── src/
        ├── api/                    # 由 @umijs/openapi 自动生成的 OpenAPI 客户端
        ├── components/
        │   ├── analyze/            # 6 个 ECharts 分析面板
        │   └── pictureRelated/     # PictureList、PictureCard 等
        ├── layouts/                # 全局顶栏（GlobalHeader）、侧边栏（GlobalSider）
        ├── pages/
        │   ├── admin/              # 用户 / 图片 / 空间 / 空间成员管理表格
        │   ├── picture/            # 上传（单张 + 批量）、详情
        │   ├── space/              # 创建、我的空间、详情、分析
        │   └── user/               # 登录、注册
        ├── router/                 # Vue Router 4 —— 所有路由定义在 index.ts
        ├── stores/                 # Pinia —— useLoginUserStore
        ├── utils/                  # 公共工具函数
        └── access.ts               # 全局导航守卫（/admin 路由鉴权）
```

---

## 快速开始

### 环境要求

| 依赖 | 版本 |
|------|------|
| Java | 11+ |
| Maven | 3.6+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6+ |
| 腾讯云 COS | 已开通 Bucket |

### 1. 数据库初始化

```bash
# 执行 DDL 脚本
mysql -u root -p < backend/sql/create_table.sql
```

### 2. 后端配置

创建 `backend/src/main/resources/application-prod.yml`（不提交到 Git），填入私密配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ek_cloud_gallery?useSSL=false&serverTimezone=UTC
    username: 数据库用户名
    password: 数据库密码
  redis:
    host: 127.0.0.1
    port: 6379

cos:
  client:
    accessKey: 腾讯云访问密钥ID
    secretKey: 腾讯云访问密钥Key
    region: ap-guangzhou
    bucket: your-bucket-name
    host: https://your-bucket.cos.ap-guangzhou.myqcloud.com
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
# API 地址：http://localhost:8123/api
# 接口文档：http://localhost:8123/api/doc.html
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
# 应用地址：http://localhost:3000/projects/image-workspace/
```

### 前端脚本说明

| 命令 | 说明 |
|------|------|
| `npm run dev` | 以开发模式启动（端口 3000） |
| `npm run build` | 类型检查 + 生产构建 |
| `npm run pure` | 仅生产构建（跳过类型检查） |
| `npm run type-check` | `vue-tsc` 类型校验 |
| `npm run lint` | ESLint 自动修复 |
| `npm run format` | Prettier 格式化 `src/` |
| `npm run openapi` | 根据后端 OpenAPI 规范重新生成 API 客户端 |

---

## 接口文档

后端启动后，通过 **Knife4j** 访问可交互接口文档：

```
http://localhost:8123/api/doc.html
```

接口分组说明：

- **用户（User）** —— 注册、登录、退出、个人信息、管理员用户管理
- **图片（Picture）** —— 上传（文件 / URL / 批量）、查询、审核、编辑、删除
- **空间（Space）** —— 创建、列表、更新、删除、级别管理
- **空间成员（SpaceUser）** —— 添加 / 移除成员、角色分配
- **空间分析（SpaceAnalyze）** —— 用量、分类、标签、大小、用户行为、排行榜
- **文件（File）** —— 测试上传接口（仅开发环境）

---

## 贡献

参见 [CONTRIBUTING.md](./CONTRIBUTING.md)。

## 安全

参见 [SECURITY.md](./SECURITY.md)。

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](./LICENSE)。

---

## 作者

**Eric Wang** — [个人主页](https://ek-flowity.site) · [LinkedIn](https://linkedin.com/in/-ericwang-) · [邮箱](mailto:ericwang7717@gmail.com)
