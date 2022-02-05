# forex-proxy
Http service that returns the latest currency exchange

## How to run
The local computer must have Java 11 and maven 3.0 or above installed to build/run this application.

1. Pull this repository from master branch, which will have the latest changes.
2. Run the One Frame API using docker
   https://hub.docker.com/r/paidyinc/one-frame
3. Run this application using mvn
```
mvn clean spring-boot:run
```
Or alternatively, build and run jar
```
mvn install
java –jar target/forex-proxy-0.0.1-SNAPSHOT.jar
```
4. The application should be running on port 9090
```
$ curl 'localhost:9090/v1/rates?from=JPY&to=USD'
```

## API interface
### GET /v1/rates
| Request params      | Description |
| ----------- | ----------- |
| from      | String of currency      |
| to   | String of currency      |

Acceptable currencies are the following. The request parameter must be an exact match.
- AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD

#### Response (application/json)
The response JSON has the below format
```
{
    "status": <Int>, 
    "result": {
        "price": <BigDecimal>,
        "timestamp: <Long>
    },
    "error": {
        "message": <String>
    }
}
```
Example:
```
$ curl 'localhost:9090/v1/rates?from=JPY&to=USD'
{"status":200,"result":{"price":0.71810472617368925,"timestamp":1644042884}}
```

TODO document about possible exceptions
## Application features
TODO just some ideas for now
- If I know the rate of AC and the rate of AB, how do I get CB = AC * 1/AB
  - Convert rates using JPY as the middle rate ex) USD -> JPY -> AUD
      - JPYUSD = 0.00867877, AUDJPY = 0.0124126, USDAUD = 1.43023
      - USDAUD = 1/0.00867877 * (0.0124126) = 1.43022571
- Use caffeine lib for cache https://github.com/ben-manes/caffeine
  - store every JPYXXX pair rates in the cache
  - cache should expire after 5 minutes
  - if the rate is not available in the cache, fetch from one frame api and update cache
  - distributed cache is ideal but will skip it for now
- Use enum to store currency values, validate that to != from
- Errors
  - 400 for unsupported currency, missing parameter
  - 500 when data can't be fetched, etc.
- Issues with caching (probably won't have time to work on it but will leave notes)
  - if we cannot update the currency values due to one frame api error, the cache will be evicted
  - what if one frame api returns rates that are older than 5 minutes
  - we can keep a timestamp of when the data was fetched, the time_stamp response from one frame, and expire at
  - we could instead keep the value in the cache, but update expire time by +5 minutes (?)
  - how do we test this cache... 