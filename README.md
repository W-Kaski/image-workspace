# Image Workspace

> A full-stack team image collaboration platform for managing shared media spaces, permissions, uploads, analytics, and collaborative editing.

## Overview

`Image Workspace` is built around the idea that shared image systems are not just galleries. Teams need storage isolation, permission boundaries, upload workflows, moderation, and operational visibility.

This repository now serves as the single home for the project, with the frontend and backend consolidated into one monorepo-style layout.

## Architecture

```text
Vue frontend
  -> REST + WebSocket
Spring Boot backend
  -> auth, space management, picture workflows, analytics
MySQL + Redis
  -> application state and sessions
Tencent COS
  -> media storage and image processing pipeline
```

## Repository Layout

- `frontend/` — Vue 3 client for gallery, spaces, admin, and analytics surfaces
- `backend/` — Spring Boot backend for users, pictures, spaces, and real-time collaboration
- `docs/` — deeper technical writeups and audit material

## Core Capabilities

- **Space-based collaboration** with isolated storage units and role-aware access
- **Image ingestion and processing** through Tencent COS workflows
- **Color-aware search and analytics** for browsing and operational insight
- **Real-time editing coordination** using WebSocket-based collaboration flows
- **Admin and moderation paths** for public and managed content

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

## Documentation

- Technical audit: `docs/image_workspace_technical_audit.md.resolved`
- Frontend workspace notes: `frontend/README.md`

## Status

- This repo is the consolidated replacement for the earlier split frontend/backend repositories.
- Backend package names still retain older internal naming and have not been mass-renamed.
- The root README is intentionally concise; deeper engineering detail lives under `docs/`.

## Contributing

See `CONTRIBUTING.md`.

## Security

See `SECURITY.md`.

## License

This repository is licensed under MIT. See `LICENSE`.
