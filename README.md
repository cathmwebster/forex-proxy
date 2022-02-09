# forex-proxy
Http service that returns the latest currency exchange

This web service uses the cross currency exchange method to return the exchange rate AC.
By default, JPY→A　is used as the base currency rate. The formula is as followed:

```AC = 1/AB * BC```

Because of this calculation, the returned rate is implied from the ratio of two currencies other than the base currency. 
Also, the web service caches the response from One Frame and will not return rate older than five minutes.

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

| Field      | Type      | Description |
| ----------- | ----------- | ----------- |
| status      | Integer | HTTP status code |
| result   | Object | The result body |
| ｜├ price | BigDecimal |  The exchange rate |
| ｜├ timestamp | Long | The timestamp of the retrieved rate in UTC epoch miliseconds  |
| error   | Object | The error body |
| ｜├ message | String |  The error description |

The following are possible errors:

| Error      | Status code      |
| ----------- | ----------- |
| CurrencyNotSupportedException      | 400 |
| InvalidRatesRequestException      | 400 |
| DataNotAvailableException      | 500 |
| InternalServerError      | 500 |

Example:
```
$ curl 'localhost:9090/v1/rates?from=JPY&to=USD'
{"status":200,"result":{"price":0.71810472617368925,"timestamp":1644042884}}
```

## Initial ideas
To follow One Frame's rate limit and in order for forex-proxy to support 10,000+ requests a day, the One Frame response needs to be cached for at most five minutes by requirement. However with the current number supported currencies (and assuming that more will be supported later), it's impossible to fulfill both One Frame's and forex-proxy's requirements if we call One Frame for every AB pairs possible.

The first idea was to get and cache unique pairs AB and if we need the rate of BA, the service will just return the inverse 1/AB. Even then, that's still 28 possible pairs of currently supported currencies and the service will not fulfill the requirements. One Frame accepts a list of pairs in one call, but it would be inefficient to send a large number of requests in one call.

This reminded me of the cross currency exchange formula, where if we know the exchange rate AB and AC, we can also calculate BC and CB. With the currently supported currencies and using JPY as the base currency, 8 pairs in one request would not be so large - this service will fetch the rate of every JPYXXX pairs in one api call, then cache the response for up to five minutes.

## Application features
 - For the given pair of currencies, the API will respond with the rate and timestamp of fetched rate
 - Use a scheduler to call One Frame API to get the rates of every JPYXXX pairs by sending all pairs in one request
   - 86400 seconds in a day / 1000 req per day to One Frame = 1 request every 86.4 seconds in a day allowed -> round up to 90 seconds
   - The scheduler will execute the action every 90 seconds
 - The API will validate that the requested currencies are supported, else the API will respond with an error
 - Use in memory cache (https://github.com/ben-manes/caffeine) to cache the rates, expires after five minutes
 - If the requested rates are not in the cache, the API will response with an error
 - If One Frame responds with time_stamp older than 5 minutes, forex-proxy will ignore it
 - 
## Future improvements
 - Test using wiremock
   - I wanted to do something like {{now}} to dynamically put the current datetime on the wiremock response, but I was getting parse errors and formatting was not working correctly (it was giving me non-UTC dt, so I ended up just going for putting in a date super far into the future so that tests pass.
   - Need to improve using wiremock (or use a different testing library) to mock One Frame responses
 - A distributed cache will be needed if forex-proxy will run on multiple servers
    - The cache should be synchronized amongst all nodes
    - Only one server should be scheduled to refresh the cache
 - The response from One Frame could be validated better
   - I think there could be better handling of parsing One Frame API response
   - The service will ignore timestamp that cannot be parsed (sometime One Frame responds with an invalid format) and will not cache it
   - The cache expiry should be the one frame response + 5 minutes (currently, it just sets it to 5 minutes from now assuming one frame timestamp will is now)
 - The rates that are not JPY based can be cached based on most requested pairs
   - This will avoid calculating the rate by formula repeatedly and give a faster response to the user
 - If the list of supported currencies grow, the refreshing stragety will need to be revised
   - This means that the size of pairs requested to One Frame will also grow
 - When a user calls this API right after application starts running, but before the cache is available, the user will get "DataNotAvailable" exception
   - Ideally, the data should be available immediately after the application starts running
