# Cloudevents Restful WS Quarkus example

This sample application has a `/users` REST endpoint in which you can manage the different users.
The way to create users is through CloudEvents. Here is an example POST:

```shell script
curl -v http://localhost:8080/users \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: User" \
  -H "Ce-Source: io.cloudevents.examples/user" \
  -H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78" \
  -H "Content-Type: application/json" \
  -H "Ce-Subject: SUBJ-0001" \
  -d @examples/user.json

> POST /users HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.69.1
> Accept: */*
> Ce-Specversion: 1.0
> Ce-Type: User
> Ce-Source: io.cloudevents.examples/user
> Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78
> Content-Type: application/json
> Ce-Subject: SUBJ-0001
> Content-Length: 88

< HTTP/1.1 201 Created
< Content-Length: 0
< Location: http://localhost:8080/users
```

In order to also show how to create and send CloudEvents, generated events will be periodically sent
each 2 seconds through HTTP to the same endpoint using a REST client.

Check the following URL to view the existing users:

```shell script
$ curl http://localhost:8080/users
{
  "user1": {
    "username": "user1",
    "firstName": "firstName1",
    "lastName": "lastName1",
    "age": 1
  },
  "user2": {
    "username": "user2",
    "firstName": "firstName2",
    "lastName": "lastName2",
    "age": 2
  }
}
```

## Build and execution

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn quarkus:dev
```

### Packaging and running the application

The application can be packaged using `mvn package`.
It produces the `cloudevents-restful-ws-quarkus-example-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/cloudevents-restful-ws-quarkus-example-1.0-SNAPSHOT-runner.jar`.

### Creating a native executable

You can create a native executable using: `mvn package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `mvn package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/cloudevents-restful-ws-quarkus-example-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
