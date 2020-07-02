# Vertx + CloudEvents sample

## Build

```shell
mvn package
```

## Start HTTP Server

```shell
mvn exec:java -Dexec.mainClass="io.cloudevents.examples.vertx.SampleHTTPServer" -Dexec.args="8080"
```

## Start HTTP Client

```shell
mvn exec:java -Dexec.mainClass="io.cloudevents.examples.vertx.SampleHTTPClient" -Dexec.args="http://localhost:8080"
```
