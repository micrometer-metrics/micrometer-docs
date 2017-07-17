## Quick Start

Pre-release artifacts are being published frequently, but are NOT intended for production use.

  In Gradle:

  ```groovy
compile 'org.springframework.metrics:spring-metrics:latest.release'{gradle}
```

Or in Maven:

```xml
<dependency>
  <groupId>org.springframework.metrics</groupId>
  <artifactId>spring-metrics</artifactId>
  <version>${metrics.version}</version>
</dependency>{maven}
```

Enable metrics in your Spring Boot application with `{enableAnnotation}`:

```java
@SpringBootApplication
{enableAnnotation}
public class MyApp {
}

@RestController
@Timed
class PersonController {
    Map<Integer, Person> people = new Map<Integer, Person>();

    public PersonController(MeterRegistry registry) {
        // constructs a gauge to monitor the size of the population
        registry.mapSize("population", people);
    }

    @GetMapping("/api/people")
    public List<Person> listPeople() {
        return people;
    }

    @GetMapping("/api/person/{id}")
    public Person findPerson(@PathVariable Integer id) {
        return people.get(id);
    }
}
```

{extraSetup}

In this sample code, multiple dimensional time series are created with a variety of metrics:

  1) Adding `@Timed` to the controller creates a `Timer` time series named `http_server_requests` which
by default contains dimensions for the HTTP status of the response, HTTP method, exception type if the request fails,
  and the pre-variable substitution parameterized endpoint URI.
2) Calling `collectionSize` on our meter registry adds a `Gauge` time series named `population` that
changes when observed by a metrics backend or exporter.
3) Metrics will be published for JVM GC metrics.
4) If you are using logback, counts will be collected for log events at each level.

  Let's break down the key pieces of the instrumentation API in detail.
