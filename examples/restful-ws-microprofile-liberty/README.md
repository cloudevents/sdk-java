# Cloudevents Restful WS Microprofile Example

This project uses Microprofile 5.0 with OpenLiberty

If you would like to know more about Microprofile go to https://microprofile.io

This Example uses Jakarta EE9 features as such the top level namespace of the `ws-api` packages has changed from `javax` to `jakarta` and uses the `cloudevents-http-restful-ws-jakarta` artifact.

## Build and Execution

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn liberty:dev
```

### Packaging and running the application

To Package and run as a minimum jar without a full OpenLiberty server
```
mvn liberty:package -Drunnable=mvn liberty:package -Dinclude=runnable
```

### Making requests against the server

This sample application has a `/events` RESTful endpoint on the application `cloudevents-restful-ws-microprofile-example
the base application is available at `http://localhost:9080/cloudevents-restful-ws-microprofile-example/`

There are three operations that can be performed:
#### GET /events
Returns a Cloud event with a payload containing a message

```shell
curl -v http://localhost:9080/cloudevents-restful-ws-microprofile-example/events

*   Trying 127.0.0.1:9080...
* Connected to localhost (127.0.0.1) port 9080 (#0)
> GET /cloudevents-restful-ws-microprofile-example/events HTTP/1.1
> Host: localhost:9080
> User-Agent: curl/7.83.1
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 201 no-content
< Ce-Id: hello
< Ce-Source: http://localhost
< Ce-Specversion: 1.0
< Ce-Type: example.http
< Content-Type: application/json
< Content-Language: en-GB
< Content-Length: 64
< Date: Wed, 17 Aug 2022 14:01:50 GMT
<
{"message":"Welcome to this Cloudevents + Microprofile example"}
```

#### POST /events
POST a Cloudevent with a payload that is printed out in the server logs

```shell
curl -v http://localhost:9080/cloudevents-restful-ws-microprofile-example/events \
-H "Ce-Specversion: 1.0" \
-H "Ce-Type: User" \
-H "Ce-Source: io.cloudevents.examples/user" \
-H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78" \
-H "Content-Type: application/json" \
-H "Ce-Subject: SUBJ-0001" \
-d "hello"

*   Trying 127.0.0.1:9080...
* Connected to localhost (127.0.0.1) port 9080 (#0)
> POST /cloudevents-restful-ws-microprofile-example/events HTTP/1.1
> Host: localhost:9080
> User-Agent: curl/7.83.1
> Accept: */*
> Ce-Specversion: 1.0
> Ce-Type: User
> Ce-Source: io.cloudevents.examples/user
> Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78
> Content-Type: application/json
> Ce-Subject: SUBJ-0001
> Content-Length: 5
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 204 No Content
< Content-Language: en-GB
< Content-Length: 0
< Date: Thu, 18 Aug 2022 13:33:03 GMT
<
* Connection #0 to host localhost left intact
```
Server log statement
```
[INFO] Received request providing a event with body hello
[INFO] CloudEvent{id='536808d3-88be-4077-9d7a-a3f162705f78', source=io.cloudevents.examples/user, type='User', datacontenttype='application/json', subject='SUBJ-0001', data=BytesCloudEventData{value=[104, 101, 108, 108, 111]}, extensions={}}
```

#### POST /events/echo
POST a Cloudevent with a payload and have it echoed back as a Cloudevent

```shell
curl -v http://localhost:9080/cloudevents-restful-ws-microprofile-example/events/echo \
-H "Ce-Specversion: 1.0" \
-H "Ce-Type: User" \
-H "Ce-Source: io.cloudevents.examples/user" \
-H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78" \
-H "Content-Type: application/json" \
-H "Ce-Subject: SUBJ-0001" \
-d "hello"

*   Trying 127.0.0.1:9080...
* Connected to localhost (127.0.0.1) port 9080 (#0)
> POST /cloudevents-restful-ws-microprofile-example/rest/events/echo HTTP/1.1
> Host: localhost:9080
> User-Agent: curl/7.83.1
> Accept: */*
> Ce-Specversion: 1.0
> Ce-Type: User
> Ce-Source: io.cloudevents.examples/user
> Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78
> Content-Type: application/json
> Ce-Subject: SUBJ-0001
> Content-Length: 5
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Ce-Id: echo
< Ce-Source: http://localhost
< Ce-Specversion: 1.0
< Ce-Type: echo.http
< Content-Type: application/json
< Content-Language: en-GB
< Content-Length: 17
< Date: Wed, 17 Aug 2022 12:57:59 GMT
<
{"echo": "hello"}* Connection #0 to host localhost left intact
```


