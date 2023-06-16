FROM node:15.14 AS builder
WORKDIR /opt/app
COPY package.json /opt/app/package.json
COPY package-lock.json /opt/app/package-lock.json
RUN npm i --quiet --no-progress
COPY . /opt/app
RUN npm run build

FROM nginx:1.25.1-alpine
ENV NGINX_PORT=80
COPY ./nginx /etc/nginx
COPY  --from=builder /opt/app/dist/ui-server /opt/app
