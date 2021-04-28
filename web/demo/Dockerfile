FROM node:8-alpine

COPY . /var/app

WORKDIR /var/app

EXPOSE 8080

CMD ["-u", "http://localhost:8088", "--webhdfs", "http://iota-m1:50070"]

ENTRYPOINT [ "node", "server.js" ]