FROM clojure:temurin-17-tools-deps-alpine

WORKDIR /usr/src/app

COPY deps.edn .
RUN clj -P

COPY src ./src