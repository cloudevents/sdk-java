## Examples of Cloud Events with Spring Cloud Function Web

### Introduction
The current example uses [Spring Cloud Function](https://spring.io/projects/spring-cloud-function) framework as its core as well as 
the support provided by [Cloud Events Java SDK](https://github.com/cloudevents/sdk-java).
As many things in Spring, Spring Cloud Function allows users to concentrate only on functional aspects of 
their requirement while taking care-off the non-functional ones. 
For more information on Spring Cloud Function please visit our [project page](https://spring.io/projects/spring-cloud-function).

The example consists of a `CloudeventDemoApplication` class that is a typical Spring Boot Application with a single 
function bean which provides implementation of some imaginary functional requirements.

```java
@Bean
public Function<SpringReleaseEvent, SpringReleaseEvent> pojoToPojo() {
        return event -> event.setReleaseDateAsString("01-10-2006").setVersion("2.0");
}
```
As you can see from its definition it expects and instance of POJO (i.e., `SpringReleaseEvent`) which it also returns 
after few updates. 

Once you start the application you can post HTTP Request as Cloud Event in _binary-mode_ using the following `curl` command:

```text
curl -w'\n' localhost:8080/pojoToPojo \
 -H "ce-specversion: 1.0" \
 -H "ce-type: com.example.springevent" \
 -H "ce-source: spring.io/spring-event" \
 -H "Content-Type: application/json" \
 -H "ce-id: 0001" \
 -d '{"releaseDate":"24-03-2004", "releaseName":"Spring Framework", "version":"1.0"}' -i
 ```
 
...and receive the following response

```text
{"releaseDate":"01-10-2006","releaseName":"Spring Framework","version":"2.0"}
```

You can also inspect response headers and notice that some response headers corresponding to Cloud Event attributes are 
different then the request ones. 

```
. . .
ce-source: http://spring.io/application-application
ce-specversion: 1.0
ce-type: io.spring.cloudevent.SpringReleaseEvent
ce-id: cf1745f2-3c5a-4095-82f1-29ad5b1ec4f3
. . .
```
That is because framework will automatically generate default values for Cloud Event output attributes.

In the event you want to have control over setting these attributes you can simply define `CloudEventAttributesProvider` 
bean where you can set any attribute you want. There is one already provided for you in the example. It's 
commented out, but feel free to un-comment and see the difference in the results.

You can also interact with the same functionality by posting Cloud Event in structured mode using the following `curl` command:

```text
curl -w'\n' localhost:8080/pojoToPojo \
 -H "Content-Type: application/cloudevents+json" \
 -d '{
    "specversion" : "1.0",
    "type" : "org.springframework",
    "source" : "https://spring.io/",
    "id" : "A234-1234-1234",
    "datacontenttype" : "application/json",
    "data" : {
        "version" : "1.0",
        "releaseName" : "Spring Framework",
        "releaseDate" : "24-03-2004"
    }
}'
```
... and observe the same results.
 
There is also a test case which contains two test which uses Spring's RestTemplate to post Cloud Event in binary-mode and structured-mode.