# Image Workspace（图片协作平台）

> 全栈团队图片协作平台，支持共享媒体空间管理、权限控制、上传工作流、数据分析与实时协同编辑。

[![Backend](https://img.shields.io/badge/后端-Spring%20Boot-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/前端-Vue%203-42b883?style=flat-square)](https://vuejs.org)
[![Database](https://img.shields.io/badge/数据库-MySQL%20%2B%20Redis-336791?style=flat-square)](#)
[![Storage](https://img.shields.io/badge/存储-腾讯云COS-1a6cf5?style=flat-square)](#)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![Contributing](https://img.shields.io/badge/PRs-欢迎-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[English](./README.md) · [架构](#架构) · [快速开始](#快速开始) · [核心功能](#核心功能)

---

## 概览

`Image Workspace` 的核心理念是：团队共享图片系统不只是一个图库。团队需要存储隔离、权限边界、上传工作流、内容审核和运营可视化。本仓库是整个平台的统一 Monorepo，前后端代码集中管理。

---

## 架构

```text
浏览器
  -> Vue 3 SPA
  -> REST + WebSocket
Spring Boot 后端
  -> 认证、空间管理、图片工作流、数据分析
MySQL + Redis
  -> 应用状态与会话管理
腾讯云 COS
  -> 媒体存储与图像处理管道
```

---

## 核心功能

- **空间协作** — 隔离存储单元，基于角色的访问控制
- **图片上传与处理** — 由腾讯云 COS 管道驱动的上传工作流
- **色彩感知搜索与分析** — 可视化浏览与运营数据看板
- **实时协同编辑** — 基于 WebSocket 的协同流程
- **管理员与审核路径** — 公共内容与托管内容的控制面板

---

## 目录结构

```
image-workspace/
├── frontend/    # Vue 3 客户端 — 图库、空间、管理、分析
├── backend/     # Spring Boot — 用户、图片、空间、实时协作
└── docs/        # 技术文档与审计资料
```

---

## 快速开始

### 后端

```bash
cd backend
mvn spring-boot:run
```

### 前端

```bash
cd frontend
npm install
npm run dev
```

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
