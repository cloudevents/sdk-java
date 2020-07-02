# Kafka + CloudEvents sample

## Build

```shell
mvn package
```

## Prepare a Kafka instance

You can use docker to start a sample kafka instance:

```shell
docker run --rm --net=host -e ADV_HOST=localhost -e SAMPLEDATA=0 lensesio/fast-data-dev
```

## Produce events

```shell
mvn exec:java -Dexec.mainClass="io.cloudevents.examples.kafka.SampleProducer" -Dexec.args="localhost:9092 sample-topic"
```

## Consume events

```shell
mvn exec:java -Dexec.mainClass="io.cloudevents.examples.kafka.SampleConsumer" -Dexec.args="localhost:9092 sample-topic"
```
