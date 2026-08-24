# ADR 0001 — Modular monolith

## Decision
Use one Spring Boot deployment with explicit feature modules.

## Why
The domain does not justify distributed systems overhead. A modular monolith gives clear boundaries, transactions and simple deployment while remaining easy to split later if scale actually requires it.
