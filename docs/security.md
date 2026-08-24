# Security

- Passwords are BCrypt hashed.
- JWT access tokens expire.
- Admin endpoints require `ROLE_ADMIN`.
- Resident complaint ownership is enforced in the backend, not the UI.
- File uploads allow only JPEG/PNG/WebP and a 5 MB limit.
- Secrets are environment variables.
- Error responses do not expose stack traces or database details.
- Audit logging should never contain passwords, tokens or secrets.

For a public production deployment, add refresh-token rotation, authentication rate limiting, CSP/security headers, antivirus/media scanning and object-storage signed URLs.
