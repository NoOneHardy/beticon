FROM node:22 AS build

WORKDIR /app

COPY ./frontend ./

RUN npm install -g pnpm@11.9.0
RUN pnpm ci

RUN pnpm run build

FROM nginx:1.30.7-alpine AS final

COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
