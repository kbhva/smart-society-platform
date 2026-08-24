# API design

Base path: `/api/v1`.

- `POST /auth/register`, `POST /auth/login`
- `GET/POST /complaints`
- `GET /complaints/{id}`
- `GET /complaints/{id}/history`
- `POST /complaints/{id}/photos`
- `PATCH /admin/complaints/{id}/status`
- `PATCH /admin/complaints/{id}/priority`
- `GET /admin/dashboard`
- `GET /categories`
- `GET /notices`
- `POST /notices/admin`

Errors use a stable JSON shape with timestamp, status, error code, message, path and validation details.
