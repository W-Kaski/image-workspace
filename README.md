# Image Workspace

> A full-stack team image collaboration platform — shared spaces, role-based access control, COS-powered upload pipelines, analytics dashboards, and real-time WebSocket co-editing.

[![Backend](https://img.shields.io/badge/backend-Spring%20Boot%202.7.6-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/frontend-Vue%203%20%2B%20Vite-42b883?style=flat-square)](https://vuejs.org)
[![Auth](https://img.shields.io/badge/auth-Sa--Token-orange?style=flat-square)](https://sa-token.cc)
[![ORM](https://img.shields.io/badge/ORM-MyBatis--Plus%203.5.9-blue?style=flat-square)](https://baomidou.com)
[![Database](https://img.shields.io/badge/database-MySQL%20%2B%20Redis-336791?style=flat-square)](#)
[![Storage](https://img.shields.io/badge/storage-Tencent%20COS-1a6cf5?style=flat-square)](#)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![PRs](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[中文](./README.zh.md) · [Architecture](#architecture) · [Quickstart](#quickstart) · [Features](#core-capabilities) · [API Docs](#api-documentation)

---

## Overview

**Image Workspace** treats shared image systems as first-class team infrastructure. Instead of a simple gallery, it provides:

- **Storage isolation** — every team operates inside its own named *Space*
- **Fine-grained permission model** — `viewer / editor / admin` roles per Space member, enforced by Sa-Token at the controller level
- **Full upload pipeline** — files are streamed to Tencent Cloud COS with server-side metadata extraction (dimensions, format, dominant color, aspect ratio)
- **Content moderation** — a three-state review workflow (`pending / approved / rejected`) with reviewer audit trail
- **Real-time co-editing** — WebSocket sessions per picture, powered by a LMAX Disruptor ring buffer for lock-free event dispatch
- **Analytics dashboards** — ECharts-driven views covering storage usage, category distribution, tag word cloud, size segmentation, user activity, and space ranking

This repository is the consolidated monorepo: frontend and backend together.

---

## Architecture

```
Browser
  → Vue 3 SPA (Vite, Ant Design Vue, Pinia, ECharts)
  → HTTP REST + WebSocket (/api, port 8123)
Spring Boot 2.7.6 backend (Java 11)
  → Sa-Token          — session auth + per-Space RBAC
  → MyBatis-Plus      — ORM with logic-delete & pagination
  → AOP               — cross-cutting auth checks via @SaCheckPermission
  → Disruptor         — lock-free WebSocket event queue
MySQL 8                — primary application state (4 tables)
Redis                  — Sa-Token session store (jackson serialization)
Caffeine               — local in-process cache layer
Tencent Cloud COS      — object storage + CI image processing pipeline
Knife4j / OpenAPI 2    — interactive API documentation at /api/doc.html
```

### Database Schema (4 tables)

| Table | Purpose |
|-------|---------|
| `user` | Account credentials, roles, soft-delete |
| `picture` | Image metadata, COS URL, review state, dominant color, spaceId FK |
| `space` | Named workspace — level, quota (maxSize / maxCount), type (private / team) |
| `space_user` | M:N join — user ↔ space with `viewer / editor / admin` role |

---

## Core Capabilities

| Area | Detail |
|------|--------|
| **Space collaboration** | Private and team Spaces; role-aware CRUD enforced via Sa-Token's `StpInterface` |
| **Picture ingestion** | Multipart upload + URL scraping (Jsoup); COS CI returns width, height, format, color |
| **Batch crawling** | Admin can bulk-import pictures from an external URL |
| **Image processing** | Thumbnail generation, color extraction, aspect-ratio normalization |
| **Content review** | Three-state workflow; reviewer ID + timestamp recorded |
| **Color-aware search** | Filter by dominant color alongside text / category / tag |
| **Analytics** | 6 ECharts panels: usage quota, categories, tags, size distribution, user uploads, space ranking |
| **Real-time editing** | WebSocket per-picture room; Disruptor ring buffer; enter / action / exit protocol |
| **Auth & RBAC** | Sa-Token session + Space-scoped permissions; custom `StpInterfaceImpl` resolves context from request path |
| **API docs** | Knife4j UI at `http://localhost:8123/api/doc.html` |

---

## Repository Layout

```
image-workspace/
├── backend/                        # Spring Boot 2.7.6, Java 11
│   ├── src/main/java/com/eric/ekcloudgallerybackend/
│   │   ├── annotation/             # Custom annotations (e.g. auth checks)
│   │   ├── aop/                    # AOP interceptors
│   │   ├── common/                 # Unified response wrapper, page result
│   │   ├── config/                 # CORS, COS client, MybatisPlus, JSON
│   │   ├── constant/               # UserConstant and other literals
│   │   ├── controller/             # REST endpoints (User, Picture, Space, SpaceUser, SpaceAnalyze, File)
│   │   ├── exception/              # BusinessException + global handler
│   │   ├── manage/
│   │   │   ├── auth/               # Sa-Token StpInterfaceImpl, SpaceUserAuthManager
│   │   │   ├── upload/             # Picture upload strategies (file / URL)
│   │   │   └── websocket/          # PictureEditHandler + Disruptor pipeline
│   │   ├── mapper/                 # MyBatis-Plus mappers
│   │   ├── model/
│   │   │   ├── dto/                # Request DTOs
│   │   │   ├── entity/             # JPA-style entities (Picture, Space, SpaceUser, User)
│   │   │   ├── enums/              # SpaceTypeEnum, SpaceRoleEnum, etc.
│   │   │   └── vo/                 # Response View Objects
│   │   └── service/                # Service interfaces + impl/
│   ├── src/main/resources/
│   │   ├── application.yml         # Active profile: prod (db/redis in prod config)
│   │   └── biz/spaceUserAuthConfig.json  # Role → permission mapping
│   └── sql/create_table.sql        # Full DDL (incremental ALTER TABLE included)
│
└── frontend/                       # Vue 3, Vite 6, TypeScript, Ant Design Vue 4
    └── src/
        ├── api/                    # Auto-generated OpenAPI clients (@umijs/openapi)
        ├── components/
        │   ├── analyze/            # 6 ECharts analytics panels
        │   └── pictureRelated/     # PictureList, PictureCard, etc.
        ├── layouts/                # GlobalHeader, GlobalSider
        ├── pages/
        │   ├── admin/              # User / Picture / Space / SpaceUser manage tables
        │   ├── picture/            # Add (single + batch), Detail
        │   ├── space/              # Add, My Space, Detail, Analyze
        │   └── user/               # Login, Register
        ├── router/                 # Vue Router 4 — all routes defined in index.ts
        ├── stores/                 # Pinia — useLoginUserStore
        ├── utils/                  # Shared helpers
        └── access.ts               # Global navigation guard (admin route protection)
```

---

## Quickstart

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Java | 11+ |
| Maven | 3.6+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6+ |
| Tencent Cloud COS | Active bucket |

### 1. Database

```bash
# Run the DDL script
mysql -u root -p < backend/sql/create_table.sql
```

### 2. Backend Configuration

Create `backend/src/main/resources/application-prod.yml` (not committed) with your secrets:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ek_cloud_gallery?useSSL=false&serverTimezone=UTC
    username: your_db_user
    password: your_db_password
  redis:
    host: 127.0.0.1
    port: 6379

cos:
  client:
    accessKey: YOUR_COS_ACCESS_KEY
    secretKey: YOUR_COS_SECRET_KEY
    region: ap-guangzhou
    bucket: your-bucket-name
    host: https://your-bucket.cos.ap-guangzhou.myqcloud.com
```

### 3. Run Backend

```bash
cd backend
mvn spring-boot:run
# API available at http://localhost:8123/api
# Swagger UI at  http://localhost:8123/api/doc.html
```

### 4. Run Frontend

```bash
cd frontend
npm install
npm run dev
# App available at http://localhost:3000/projects/image-workspace/
```

### Available Frontend Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Dev server on port 3000 |
| `npm run build` | Type-check + production build |
| `npm run pure` | Production build only (skip type-check) |
| `npm run type-check` | `vue-tsc` type validation |
| `npm run lint` | ESLint auto-fix |
| `npm run format` | Prettier format `src/` |
| `npm run openapi` | Regenerate API client from backend OpenAPI spec |

---

## API Documentation

Once the backend is running, interactive docs are available via **Knife4j**:

```
http://localhost:8123/api/doc.html
```

Endpoint groups:

- **User** — register, login, logout, profile, admin user management
- **Picture** — upload (file/URL/batch), query, review, edit, delete
- **Space** — create, list, update, delete, level management
- **SpaceUser** — add/remove members, role assignment
- **SpaceAnalyze** — usage, category, tag, size, user activity, ranking
- **File** — test upload endpoint (dev only)

---

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md).

## Security

See [SECURITY.md](./SECURITY.md).

## License

Licensed under MIT. See [LICENSE](./LICENSE).

---

## Author

**Eric Wang** — [Portfolio](https://ek-flowity.site) · [LinkedIn](https://linkedin.com/in/-ericwang-) · [Email](mailto:ericwang7717@gmail.com)
