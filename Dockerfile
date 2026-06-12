# Build frontend assets
FROM node:20-alpine AS frontend-builder
WORKDIR /src

COPY agriscan-ai/frontend/package.json agriscan-ai/frontend/package-lock.json ./
RUN npm ci

COPY agriscan-ai/frontend .
RUN npm run build

# Build backend image
FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY agriscan-ai/backend/requirements.txt /app/backend/requirements.txt
RUN pip install --no-cache-dir -r /app/backend/requirements.txt

COPY --from=frontend-builder /src/dist /app/frontend/dist
COPY agriscan-ai/backend /app/backend
COPY agriscan-ai/ml /app/ml
COPY agriscan-ai/database /app/database

WORKDIR /app/backend

EXPOSE 5000

CMD ["gunicorn", "--bind", "0.0.0.0:5000", "run:app"]
