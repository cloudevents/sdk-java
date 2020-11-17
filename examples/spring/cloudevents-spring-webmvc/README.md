## Examples of Cloud Events with Spring MVC

### Introduction
The current example uses [Spring Web MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html) and the support provided by [Cloud Events Java SDK](https://github.com/cloudevents/sdk-java).

The example consists of a `CloudeventDemoApplication` which mapps two HTTP endpoints to accept Cloud Events. One endpoint is for binary-mode and one for structured-mode Cloud Event.

```java
@PostMapping("/")
public ResponseEntity<Person> binary(@RequestBody Person person, @RequestHeader HttpHeaders headers) {
    . . . .
}

@PostMapping(path = "/", consumes = "application/cloudevents+json")
public ResponseEntity<Object> structured(@RequestBody Map<String, Object> body,
            @RequestHeader HttpHeaders headers) {
    . . . .
}
```

Once you start the application you can post HTTP Request as Cloud Event in _binary-mode_ using the following `curl` command:

```text
curl -w'\n' localhost:8080/ \
 -H "ce-specversion: 1.0" \
 -H "ce-type: com.example.person" \
 -H "ce-source: https://spring.io/" \
 -H "Content-Type: application/json" \
 -H "ce-id: 0001" \
 -d '{"name":"Julien"}' -i
 ```
 
...and receive the following response

```text
ce-specversion: 1.0
ce-id: 5c8e994f-b11e-4ec1-8ae5-c989f7e4838b
ce-source: https://spring.io/ce-webmvc/binary
ce-type: io.cloudevents.spring.webmvc.CloudeventDemoApplication$Person
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 20 Nov 2020 13:56:26 GMT

{"name":"Julien"}
```

You can also interact with the same functionality by posting Cloud Event in structured mode using the following `curl` command:

```text
curl -w'\n' localhost:8080/ \
 -H "Content-Type: application/cloudevents+json" \
 -d '{
    "specversion" : "1.0",
    "type" : "com.example.person",
    "source" : "https://spring.io/",
    "id" : "A234-1234-1234",
    "datacontenttype" : "application/json",
    "data" : {"name":"Julien"}
}' -i
```
... and observe the  results:

```text
ce-datacontenttype: application/json
ce-specversion: 1.0
ce-id: 3db55b31-6344-4d9b-b39d-04377a137d19
ce-source: https://spring.io/ce-webmvc/structured
ce-type: io.cloudevents.spring.webmvc.CloudeventDemoApplication$Person
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 20 Nov 2020 13:56:54 GMT

{"name":"Julien"}
```
 
There is also a test case which contains two test which uses Spring's RestTemplate to post Cloud Event in binary-mode and structured-mode.