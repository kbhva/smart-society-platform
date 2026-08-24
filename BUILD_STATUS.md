# Build status

Implemented repository scaffold and core production paths. The execution container used for generation does not have Maven or Docker installed, so full JVM compilation, integration tests, image builds and deployment smoke tests could not be executed here. Node dependencies are likewise not installed. These are explicitly not claimed as passed.

Next verification on a development machine/CI: `cd backend && mvn test`, `cd frontend && npm install && npm run build`, then `docker compose up --build`.
