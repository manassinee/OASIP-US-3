FROM node:lts-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN [ "npm", "install" ]

COPY . .
ARG VITE_API_URL
RUN [ "npm", "run", "build" ]



FROM nginx:latest AS nginx

COPY --from=build /app/dist/ /usr/share/nginx/html/