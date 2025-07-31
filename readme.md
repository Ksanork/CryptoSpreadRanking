# Crypto Spread Ranking App

## Tech stack
* Java 21
* Spring Boot 3.5.4
* Redis 8
* Gradle 8

## Prerequisites

* Docker

## How to run

Run docker compose command within this repo:
```
docker-compose up --build
```
## Endpoints

In the requirements from the exercise list two endpoints: `/ranking`
and `/calculate`

Also, the calculation status should be stored only in the program memory, that's why I designed this endpoints as follows.
### Get ranking
```
curl -H "Authorization: Bearer ABC123" http://localhost:8080/api/spread/ranking
```

It returns spread ranking from the app memory (from `spreadRankingMap` in `SpreadRankingServiceImpl`)

This variable is a `null` at the start — after sending the first GET request, the spread calculation is performed.

After that, the next calculation can be only run by POST `/calculate` request


### Calculate ranking
```
curl -X POST -H "Authorization: Bearer ABC123" http://localhost:8080/api/spread/calculate 
```

It generates spread ranking and saves it to `spreadRankingMap` variable.

By default, 50 workers is used to fetch orderbooks for all available pairs

Spread ranking can be fetched by GET request `/ranking`

## Cache

Redis is used to cache Kanga API responses.

TTL:
* **1h** for response from https://public.kanga.exchange/api/market/pairs - as I assume, available pairs rarely changes
* **5 min** for response from https://public.kanga.exchange/api/market/orderbook/

## Unit tests

Unit tests can be run with:
```
./gradlew test
```