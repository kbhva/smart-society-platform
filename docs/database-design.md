# Database design

Core tables: `users`, `complaint_categories`, `complaints`, `complaint_status_history`, `complaint_photos`, `notices`, `notifications`, `audit_logs`.

Indexes target ownership queries, status filtering, overdue detection and chronological history. Foreign keys enforce ownership and cleanup of complaint history/photos when a complaint is deleted. Flyway owns schema evolution.

The history table intentionally duplicates lifecycle events instead of trying to infer them from the current complaint row. This gives an auditable timeline.
