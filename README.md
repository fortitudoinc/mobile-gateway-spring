# Spring Based Mobile Gateway

Mobile gateway written in spring rather than python

## Build

```bash
./gradlew build
docker build -t mobile-gateway-spring .
```


## Run

```bash
docker run -p 3000:3000 mobile-gateway-spring
curl localhost:3000/
```