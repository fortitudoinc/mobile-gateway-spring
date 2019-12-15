# Spring Based Mobile Gateway

Mobile gateway written in spring rather than python

## Build

```bash
# Check java version (should be 1.8)
java -version

# Build jar
./gradlew build

# Dockerize
docker build -t mobile-gateway-spring .
```


## Run

This container is meant to be run in the context of the rest of the fortitudoinc infrastructure (see: https://github.com/fortitudoinc/fortitudoinc-infra) but can be run standalone:

```bash
docker run -p 3000:3000 mobile-gateway-spring
curl localhost:3000/
```