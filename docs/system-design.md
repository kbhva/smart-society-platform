# Smart Society Operations Platform — System Design

The platform is a modular monolith with a React/TypeScript client, Spring Boot REST API and PostgreSQL database. A single deployment keeps operational complexity low while package-level modules separate authentication, complaints, SLA, notifications, notices, analytics and media.

A complaint is stored as the current operational record plus an immutable status-history timeline. Valid transitions are OPEN → IN_PROGRESS → RESOLVED. Each transition records the previous status, new status, actor, timestamp and optional note. This prevents the common failure mode where an application overwrites a status and loses the audit trail. Backend ownership checks ensure residents can only retrieve their own complaints.

Overdue detection is derived from `due_at`. Category SLA hours provide the baseline and priority modifies the effective SLA. Because overdue is derived from timestamps and current status, the application does not need a continuously updated boolean column. Dashboard queries aggregate directly in PostgreSQL so the browser never downloads the full complaint table.

Complaint photos are not stored as PostgreSQL blobs. The media layer validates MIME type and size, stores a generated key and writes the binary to a storage provider. The included development implementation uses local filesystem storage; production can use an S3-compatible implementation without changing complaint business logic.

Status-change and important-notice email notifications are dispatched asynchronously. A failed email must not silently undo a successful complaint transition. Delivery failures are logged for observability. The notification abstraction also leaves room for in-app notifications.

Flyway manages schema evolution, PostgreSQL constraints protect data integrity, and optimistic locking on complaints addresses concurrent administrative updates. REST endpoints are versioned under `/api/v1`, DTOs prevent entities from becoming an accidental public contract, and centralized exception handling keeps responses predictable.

The system is containerized and designed for deployment as separate frontend/backend services with managed PostgreSQL. CI builds, tests and packages the application.
