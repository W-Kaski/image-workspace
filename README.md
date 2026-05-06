# Image Workspace

> A full-stack team image collaboration platform for managing shared media spaces, permissions, uploads, analytics, and real-time collaborative editing.

[![Backend](https://img.shields.io/badge/backend-Spring%20Boot-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/frontend-Vue%203-42b883?style=flat-square)](https://vuejs.org)
[![Database](https://img.shields.io/badge/database-MySQL%20%2B%20Redis-336791?style=flat-square)](#)
[![Storage](https://img.shields.io/badge/storage-Tencent%20COS-1a6cf5?style=flat-square)](#)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![Contributing](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[中文](./README.zh.md) · [Architecture](#architecture) · [Quickstart](#quickstart) · [Features](#core-capabilities)

---

## Overview

`Image Workspace` is built around the idea that shared image systems are more than galleries. Teams need storage isolation, permission boundaries, upload workflows, moderation, and operational visibility. This repository is the consolidated monorepo for the entire platform — frontend and backend together.

---

## Architecture

```text
Browser
  -> Vue 3 SPA
  -> REST + WebSocket
Spring Boot backend
  -> auth, space management, picture workflows, analytics
MySQL + Redis
  -> application state and sessions
Tencent COS
  -> media storage and image processing pipeline
```

---

## Core Capabilities

- **Space-based collaboration** — isolated storage units with role-aware access control
- **Image ingestion & processing** — upload workflows powered by Tencent COS pipelines
- **Color-aware search & analytics** — visual browsing and operational dashboards
- **Real-time editing coordination** — WebSocket-based collaborative flows
- **Admin & moderation paths** — controls for public and managed content

---

## Repository Layout

```
image-workspace/
├── frontend/    # Vue 3 client — gallery, spaces, admin, analytics
├── backend/     # Spring Boot — users, pictures, spaces, real-time
└── docs/        # Technical writeups and audit material
```

---

## Quickstart

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

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
